package com.example.camerinosports;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.util.Validaciones;
import com.example.camerinosports.viewmodel.ProductoViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.UUID;

public class GestionProductosActivity extends AppCompatActivity {

    private EditText edtNombreProducto;
    private AutoCompleteTextView edtCategoriaProducto;
    private EditText edtTallaProducto;
    private EditText edtColorProducto;
    private EditText edtPrecioProducto;
    private EditText edtStockProducto;
    private EditText edtImagenProducto;
    private ImageView imgPreviewProducto;
    private TextView btnGuardarProducto;
    private TextView btnLimpiarProducto;
    private TextView txtProductosVacios;
    private ScrollView scrollGestionProductos;
    private View cardFormularioProducto;
    private LinearLayout contenedorLista;
    private ProductoViewModel productoViewModel;
    private Producto productoSeleccionado;
    private ActivityResultLauncher<String[]> selectorImagen;
    private String imagenSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_productos);

        productoViewModel = new ProductoViewModel(this);
        prepararSelectorImagen();
        enlazarVistas();
        prepararLista();
        configurarEventos();
        productoViewModel.sincronizarProductos(new ProductoViewModel.ResultadoSincronizacion() {
            @Override
            public void alCompletar() {
                cargarProductos();
            }

            @Override
            public void alFallar() {
                Toast.makeText(GestionProductosActivity.this,
                        "No se pudieron cargar los productos desde el servidor",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enlazarVistas() {
        edtNombreProducto = findViewById(R.id.edtNombreProducto);
        edtCategoriaProducto = findViewById(R.id.edtCategoriaProducto);
        edtTallaProducto = findViewById(R.id.edtTallaProducto);
        edtColorProducto = findViewById(R.id.edtColorProducto);
        edtPrecioProducto = findViewById(R.id.edtPrecioProducto);
        edtStockProducto = findViewById(R.id.edtStockProducto);
        edtImagenProducto = findViewById(R.id.edtImagenProducto);
        imgPreviewProducto = findViewById(R.id.imgPreviewProducto);
        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);
        btnLimpiarProducto = findViewById(R.id.btnLimpiarProducto);
        txtProductosVacios = findViewById(R.id.txtProductosVaciosAdmin);
        scrollGestionProductos = findViewById(R.id.scrollGestionProductos);
        cardFormularioProducto = findViewById(R.id.cardFormularioProducto);
        configurarDesplegableCategorias();
    }

    private void configurarDesplegableCategorias() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                obtenerCategoriasDisponibles()
        );

        edtCategoriaProducto.setAdapter(adapter);
        edtCategoriaProducto.setKeyListener(null);
        edtCategoriaProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtCategoriaProducto.showDropDown();
            }
        });

        edtCategoriaProducto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean tieneFoco) {
                if (tieneFoco) {
                    edtCategoriaProducto.showDropDown();
                }
            }
        });

        edtCategoriaProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long id) {
                actualizarFormularioPorCategoria(edtCategoriaProducto.getText().toString());
            }
        });
    }

    private ArrayList<String> obtenerCategoriasDisponibles() {
        // se usa un conjunto para evitar categorias repetidas en el desplegable
        LinkedHashSet<String> categorias = new LinkedHashSet<>();
        categorias.add("Accesorios");
        categorias.add("Ropa");
        categorias.add("Calzado");

        return new ArrayList<>(categorias);
    }

    private void actualizarFormularioPorCategoria(String categoria) {
        // cada categoria necesita un tipo de talla diferente
        if ("Accesorios".equalsIgnoreCase(categoria)) {
            edtTallaProducto.setHint("Tamaño / medida opcional");
            edtTallaProducto.setInputType(InputType.TYPE_CLASS_TEXT);
            edtColorProducto.setHint("Color / material");
            return;
        }

        if ("Ropa".equalsIgnoreCase(categoria)) {
            edtTallaProducto.setHint("Talla: XS, S, M, L, XL...");
            edtTallaProducto.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            edtColorProducto.setHint("Color");
            return;
        }

        edtTallaProducto.setHint("Talla de calzado: 20 a 50");
        edtTallaProducto.setInputType(
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtColorProducto.setHint("Color");
    }

    private void prepararSelectorImagen() {
        // conserva el permiso de lectura para poder mostrar la imagen despues
        selectorImagen = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri == null) {
                return;
            }

            try {
                getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException ignored) {
                // Algunos proveedores permiten leer la imagen, pero no conservar el permiso.
            }

            String imagenLocal = copiarImagenAlmacenamientoInterno(uri);
            if (imagenLocal == null) {
                Toast.makeText(this, "No se pudo guardar la imagen seleccionada",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            actualizarImagenSeleccionada(imagenLocal);
        });
    }

    private String copiarImagenAlmacenamientoInterno(Uri origen) {
        File carpeta = new File(getFilesDir(), "imagenes_productos");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            return null;
        }

        String tipo = getContentResolver().getType(origen);
        String extension = tipo != null && tipo.contains("png") ? ".png" : ".jpg";
        File destino = new File(carpeta, "producto_" + UUID.randomUUID() + extension);

        try (InputStream entrada = getContentResolver().openInputStream(origen);
             OutputStream salida = new FileOutputStream(destino)) {
            if (entrada == null) {
                return null;
            }

            byte[] buffer = new byte[8192];
            int leidos;
            while ((leidos = entrada.read(buffer)) != -1) {
                salida.write(buffer, 0, leidos);
            }
            return Uri.fromFile(destino).toString();
        } catch (IOException | SecurityException e) {
            if (destino.exists()) {
                destino.delete();
            }
            return null;
        }
    }

    private void prepararLista() {
        contenedorLista = findViewById(R.id.contenedorProductosAdmin);
        View itemUno = findViewById(R.id.itemProductoAdminUno);
        View itemDos = findViewById(R.id.itemProductoAdminDos);
        View itemTres = findViewById(R.id.itemProductoAdminTres);

        if (itemUno != null) {
            contenedorLista.removeView(itemUno);
        }

        if (itemDos != null) {
            contenedorLista.removeView(itemDos);
        }

        if (itemTres != null) {
            contenedorLista.removeView(itemTres);
        }
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverGestionProductos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarProducto();
            }
        });

        btnLimpiarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarFormulario();
            }
        });

        findViewById(R.id.btnNuevoProducto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarFormulario();
                mostrarFormulario();
            }
        });

        imgPreviewProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorImagen.launch(new String[]{"image/*"});
            }
        });
    }

    private void mostrarFormulario() {
        cardFormularioProducto.setVisibility(View.VISIBLE);
        scrollGestionProductos.post(new Runnable() {
            @Override
            public void run() {
                scrollGestionProductos.smoothScrollTo(0, cardFormularioProducto.getTop());
            }
        });
    }

    private void guardarProducto() {
        Producto producto = leerFormulario();

        if (producto == null) {
            return;
        }

        if (productoSeleccionado != null) {
            // si hay un producto seleccionado se actualiza; si no, se crea uno nuevo
            producto.setId(productoSeleccionado.getId());
        }

        findViewById(R.id.btnGuardarProducto).setEnabled(false);
        productoViewModel.guardarProductoApi(
                producto,
                productoSeleccionado == null,
                new ProductoViewModel.ResultadoOperacion() {
            @Override
            public void alCompletar() {
                findViewById(R.id.btnGuardarProducto).setEnabled(true);
                Toast.makeText(GestionProductosActivity.this,
                        "producto guardado", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                configurarDesplegableCategorias();
                cargarProductos();
            }

            @Override
            public void alFallar(String mensaje) {
                findViewById(R.id.btnGuardarProducto).setEnabled(true);
                Toast.makeText(GestionProductosActivity.this,
                        mensaje, Toast.LENGTH_SHORT).show();
            }
                });
    }

    private Producto leerFormulario() {
        // primero se validan todos los campos y luego se construye el producto
        String nombre = edtNombreProducto.getText().toString().trim();
        String categoria = edtCategoriaProducto.getText().toString().trim();
        String talla = edtTallaProducto.getText().toString().trim();
        String color = edtColorProducto.getText().toString().trim();
        String precioTexto = edtPrecioProducto.getText().toString().trim();
        String stockTexto = edtStockProducto.getText().toString().trim();
        String imagen = imagenSeleccionada;

        if (!Validaciones.nombreProducto(nombre)) {
            edtNombreProducto.setError("Nombre inválido");
            edtNombreProducto.requestFocus();
            return null;
        }

        if (!Validaciones.categoriaProducto(categoria)) {
            edtCategoriaProducto.setError("Selecciona una categoría");
            edtCategoriaProducto.requestFocus();
            return null;
        }

        if (!Validaciones.tallaProducto(categoria, talla)) {
            edtTallaProducto.setError("Talla inválida para " + categoria);
            edtTallaProducto.requestFocus();
            return null;
        }

        if (!Validaciones.colorProducto(color)) {
            edtColorProducto.setError("Usa solo letras");
            edtColorProducto.requestFocus();
            return null;
        }

        if (precioTexto.isEmpty() || stockTexto.isEmpty()) {
            Toast.makeText(this, "completa precio y stock", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (imagen.isEmpty()) {
            Toast.makeText(this, "selecciona una imagen del producto", Toast.LENGTH_SHORT).show();
            return null;
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setTalla(talla);
        producto.setColor(color);
        producto.setDescripcion("producto registrado en camerino sports");
        producto.setImagen(imagen);
        try {
            // la conversion se realiza aqui para controlar valores vacios o no numericos
            producto.setPrecio(Double.parseDouble(precioTexto));
            producto.setStock(Integer.parseInt(stockTexto));
            if (producto.getPrecio() <= 0 || producto.getPrecio() > 100000) {
                edtPrecioProducto.setError("Precio mayor que 0");
                return null;
            }
            if (producto.getStock() < 0 || producto.getStock() > 100000) {
                edtStockProducto.setError("Stock entre 0 y 100000");
                return null;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "precio o stock inválido", Toast.LENGTH_SHORT).show();
            return null;
        }

        producto.setSostenible(false);
        return producto;
    }

    private void cargarProductos() {
        contenedorLista.removeAllViews();

        ArrayList<Producto> productos = productoViewModel.listarProductos();
        txtProductosVacios.setVisibility(productos.isEmpty() ? View.VISIBLE : View.GONE);
        int i;

        for (i = 0; i < productos.size(); i++) {
            final Producto producto = productos.get(i);
            LinearLayout item = crearItemProducto(producto);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seleccionarProducto(producto);
                }
            });

            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    confirmarEliminar(producto);
                    return true;
                }
            });

            contenedorLista.addView(item);
        }
    }

    private void seleccionarProducto(Producto producto) {
        productoSeleccionado = producto;
        edtNombreProducto.setText(producto.getNombre());
        edtCategoriaProducto.setText(producto.getCategoria());
        actualizarFormularioPorCategoria(producto.getCategoria());
        edtTallaProducto.setText(producto.getTalla());
        edtColorProducto.setText(producto.getColor());
        edtPrecioProducto.setText(String.valueOf(producto.getPrecio()));
        edtStockProducto.setText(String.valueOf(producto.getStock()));
        actualizarImagenSeleccionada(producto.getImagen());
        mostrarFormulario();
    }

    private void confirmarEliminar(final Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle("eliminar producto")
                .setMessage("¿deseas eliminar este producto?")
                .setPositiveButton("sí", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialogInterface, int i) {
                        productoViewModel.eliminarProductoApi(
                                producto.getId(),
                                new ProductoViewModel.ResultadoOperacion() {
                                    @Override
                                    public void alCompletar() {
                                        Toast.makeText(
                                                GestionProductosActivity.this,
                                                "producto eliminado",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                        limpiarFormulario();
                                        cargarProductos();
                                    }

                                    @Override
                                    public void alFallar(String mensaje) {
                                        Toast.makeText(
                                                GestionProductosActivity.this,
                                                mensaje,
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                        );
                    }
                })
                .setNegativeButton("no", null)
                .show();
    }

    private void limpiarFormulario() {
        productoSeleccionado = null;
        edtNombreProducto.setText("");
        edtCategoriaProducto.setText("");
        edtTallaProducto.setText("");
        edtColorProducto.setText("");
        edtTallaProducto.setHint("Talla");
        edtColorProducto.setHint("Color");
        edtPrecioProducto.setText("");
        edtStockProducto.setText("");
        actualizarImagenSeleccionada("");
    }

    private LinearLayout crearItemProducto(final Producto producto) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(dp(12), dp(12), dp(12), dp(12));
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setBackgroundResource(R.drawable.fondo_item_pedido);
        item.setClickable(true);
        item.setFocusable(true);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        itemParams.setMargins(0, 0, 0, dp(12));
        item.setLayoutParams(itemParams);

        ImageView imagen = new ImageView(this);
        mostrarImagenEnVista(imagen, producto.getImagen());
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        item.addView(imagen, new LinearLayout.LayoutParams(dp(88), dp(76)));

        TextView datos = new TextView(this);
        datos.setTextColor(0xFFFFFFFF);
        datos.setText(producto.getNombre() + "\n" +
                producto.getCategoria() + " | talla " + producto.getTalla() + " | " + producto.getColor() + "\n" +
                "stock: " + producto.getStock() + " | S/ " + String.format("%.2f", producto.getPrecio()) +
                (producto.getStock() <= 3 ? "\nstock bajo" : ""));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(dp(14), 0, 0, 0);
        item.addView(datos, params);

        LinearLayout acciones = new LinearLayout(this);
        acciones.setOrientation(LinearLayout.VERTICAL);
        acciones.setGravity(android.view.Gravity.CENTER);

        TextView editar = new TextView(this);
        editar.setWidth(dp(42));
        editar.setHeight(dp(36));
        editar.setGravity(android.view.Gravity.CENTER);
        editar.setBackgroundResource(R.drawable.bg_btn_cantidad);
        editar.setText("✎");
        editar.setTextColor(0xFF00AFFF);
        editar.setTextSize(18);
        editar.setTypeface(null, android.graphics.Typeface.BOLD);
        editar.setClickable(true);
        editar.setFocusable(true);

        ImageView eliminar = new ImageView(this);
        eliminar.setBackgroundResource(R.drawable.bg_btn_cantidad);
        eliminar.setImageResource(R.drawable.ic_delete_red);
        eliminar.setPadding(dp(9), dp(8), dp(9), dp(8));
        eliminar.setClickable(true);
        eliminar.setFocusable(true);

        LinearLayout.LayoutParams botonParams = new LinearLayout.LayoutParams(dp(42), dp(36));
        acciones.addView(editar, botonParams);

        LinearLayout.LayoutParams eliminarParams = new LinearLayout.LayoutParams(dp(42), dp(36));
        eliminarParams.setMargins(0, dp(8), 0, 0);
        acciones.addView(eliminar, eliminarParams);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarProducto(producto);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarEliminar(producto);
            }
        });

        item.addView(acciones, new LinearLayout.LayoutParams(dp(48), LinearLayout.LayoutParams.WRAP_CONTENT));

        return item;
    }

    private void mostrarImagen(String nombreImagen) {
        mostrarImagenEnVista(imgPreviewProducto, nombreImagen);
    }

    private void actualizarImagenSeleccionada(String imagen) {
        imagenSeleccionada = imagen == null ? "" : imagen.trim();

        if (imagenSeleccionada.isEmpty()) {
            edtImagenProducto.setText("");
        } else {
            edtImagenProducto.setText("Imagen seleccionada");
        }

        mostrarImagen(imagenSeleccionada);
    }

    private void mostrarImagenEnVista(ImageView imageView, String nombreImagen) {
        if (nombreImagen == null || nombreImagen.trim().isEmpty()) {
            imageView.setImageDrawable(null);
            return;
        }

        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                nombreImagen,
                R.drawable.ic_tag
        );
    }

    private int obtenerDrawableProducto(String nombreImagen) {
        // compatibilidad con imagenes empaquetadas como recursos de android.
        int idImagen = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());

        if (idImagen == 0) {
            return R.drawable.ic_tag;
        }

        return idImagen;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
