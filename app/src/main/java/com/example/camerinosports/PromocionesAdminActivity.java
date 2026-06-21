package com.example.camerinosports;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.model.Promocion;
import com.example.camerinosports.viewmodel.ProductoViewModel;
import com.example.camerinosports.viewmodel.PromocionViewModel;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PromocionesAdminActivity extends AppCompatActivity {

    private EditText edtProductoId;
    private EditText edtImagen;
    private EditText edtPrecioOriginal;
    private EditText edtPrecioOferta;
    private EditText edtFechaInicio;
    private EditText edtFechaFin;
    private ImageView imgPreview;
    private TextView txtProductoSeleccionado;
    private TextView txtDetalleProducto;
    private LinearLayout listaPromociones;
    private PromocionViewModel promocionViewModel;
    private ProductoViewModel productoViewModel;
    private Promocion promocionSeleccionada;
    private ActivityResultLauncher<Intent> seleccionarProductoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones_admin);

        promocionViewModel = new PromocionViewModel(this);
        productoViewModel = new ProductoViewModel(this);
        prepararResultadoProducto();
        enlazarVistas();
        configurarEventos();
        establecerFechasIniciales();
        cargarPromociones();
    }

    private void prepararResultadoProducto() {
        seleccionarProductoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            edtProductoId.setText(String.valueOf(data.getIntExtra("productoId", 0)));
                            edtImagen.setText(data.getStringExtra("imagen"));
                            actualizarProductoSeleccionado();
                        }
                    }
                }
        );
    }

    private void enlazarVistas() {
        edtProductoId = findViewById(R.id.edtProductoIdPromocion);
        edtImagen = findViewById(R.id.edtImagenPromocion);
        edtPrecioOriginal = findViewById(R.id.edtPrecioOriginalPromocion);
        edtPrecioOferta = findViewById(R.id.edtPrecioOfertaPromocion);
        edtFechaInicio = findViewById(R.id.edtFechaInicioPromocion);
        edtFechaFin = findViewById(R.id.edtFechaFinPromocion);
        imgPreview = findViewById(R.id.imgPreviewPromocion);
        txtProductoSeleccionado = findViewById(R.id.txtProductoSeleccionadoPromocion);
        txtDetalleProducto = findViewById(R.id.txtDetalleProductoPromocion);
        listaPromociones = findViewById(R.id.contenedorListaPromocionesAdmin);
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverPromocionesAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirSeleccionProducto();
            }
        });

        findViewById(R.id.btnSeleccionarProductoPromocionAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirSeleccionProducto();
            }
        });

        findViewById(R.id.btnGuardarPromocion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPromocion();
            }
        });

        findViewById(R.id.btnLimpiarPromocion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiar();
            }
        });

        configurarSelectorFecha(edtFechaInicio);
        configurarSelectorFecha(edtFechaFin);

        edtProductoId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarProductoSeleccionado();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void actualizarProductoSeleccionado() {
        try {
            int productoId = Integer.parseInt(edtProductoId.getText().toString().trim());
            Producto producto = productoViewModel.obtenerProductoPorId(productoId);
            if (producto != null) {
                edtImagen.setText(producto.getImagen());
                mostrarImagen(producto.getImagen());
                txtProductoSeleccionado.setText(producto.getNombre());
                txtDetalleProducto.setText(producto.getCategoria() + " | Stock: " + producto.getStock() +
                        " | Precio: S/ " + String.format("%.2f", producto.getPrecio()));
                if (edtPrecioOriginal.getText().toString().trim().isEmpty()) {
                    edtPrecioOriginal.setText(String.valueOf(producto.getPrecio()));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void guardarPromocion() {
        try {
            if (edtProductoId.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "selecciona un producto", Toast.LENGTH_SHORT).show();
                return;
            }

            Promocion promocion = new Promocion();
            promocion.setProductoId(Integer.parseInt(edtProductoId.getText().toString().trim()));
            promocion.setPrecioOriginal(Double.parseDouble(edtPrecioOriginal.getText().toString().trim()));
            promocion.setPrecioOferta(Double.parseDouble(edtPrecioOferta.getText().toString().trim()));
            promocion.setFechaInicio(edtFechaInicio.getText().toString().trim());
            promocion.setFechaFin(edtFechaFin.getText().toString().trim());
            promocion.setActiva(true);

            if (!promocion.getFechaInicio().matches("\\d{4}-\\d{2}-\\d{2}") ||
                    !promocion.getFechaFin().matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(this, "Selecciona las fechas desde el calendario", Toast.LENGTH_SHORT).show();
                return;
            }

            if (productoViewModel.obtenerProductoPorId(promocion.getProductoId()) == null) {
                Toast.makeText(this, "el producto seleccionado ya no existe", Toast.LENGTH_SHORT).show();
                return;
            }

            if (promocion.getPrecioOriginal() <= 0) {
                edtPrecioOriginal.setError("Precio mayor que 0");
                edtPrecioOriginal.requestFocus();
                return;
            }

            if (promocion.getPrecioOferta() <= 0 ||
                    promocion.getPrecioOferta() >= promocion.getPrecioOriginal()) {
                edtPrecioOferta.setError("Debe ser menor al precio original");
                edtPrecioOferta.requestFocus();
                return;
            }

            if (promocionSeleccionada != null) {
                promocion.setId(promocionSeleccionada.getId());
            }
            findViewById(R.id.btnGuardarPromocion).setEnabled(false);
            promocionViewModel.guardarPromocionApi(promocion, new PromocionViewModel.ResultadoOperacion() {
                @Override
                public void alCompletar(Promocion promocionGuardada) {
                    findViewById(R.id.btnGuardarPromocion).setEnabled(true);
                    Toast.makeText(PromocionesAdminActivity.this,
                            "promocion guardada", Toast.LENGTH_SHORT).show();
                    limpiar();
                    cargarPromociones();
                }

                @Override
                public void alFallar(String mensaje) {
                    findViewById(R.id.btnGuardarPromocion).setEnabled(true);
                    Toast.makeText(PromocionesAdminActivity.this,
                            mensaje, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPromociones() {
        listaPromociones.removeAllViews();
        promocionViewModel.listarPromocionesApi(false, new PromocionViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Promocion> promociones) {
                mostrarListaPromociones(promociones);
            }

            @Override
            public void alFallar(String mensaje) {
                listaPromociones.addView(crearTexto(mensaje));
            }
        });
    }

    private void mostrarListaPromociones(ArrayList<Promocion> promociones) {
        if (promociones.isEmpty()) {
            listaPromociones.addView(crearTexto("No hay promociones registradas"));
            return;
        }

        int i;
        for (i = 0; i < promociones.size(); i++) {
            listaPromociones.addView(crearItemPromocion(promociones.get(i)));
        }
    }

    private LinearLayout crearItemPromocion(final Promocion promocion) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(dp(14), dp(14), dp(14), dp(14));
        item.setBackgroundResource(R.drawable.fondo_item_pedido);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        itemParams.setMargins(0, 0, 0, dp(14));
        item.setLayoutParams(itemParams);

        LinearLayout filaDatos = new LinearLayout(this);
        filaDatos.setOrientation(LinearLayout.HORIZONTAL);
        filaDatos.setGravity(android.view.Gravity.CENTER_VERTICAL);

        ImageView imagen = new ImageView(this);
        mostrarImagenEnVista(imagen, promocion.getImagenProducto());
        imagen.setBackgroundResource(R.drawable.bg_card_admin_moderno);
        imagen.setPadding(dp(6), dp(6), dp(6), dp(6));
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        filaDatos.addView(imagen, new LinearLayout.LayoutParams(dp(104), dp(96)));

        TextView datos = crearTexto(promocion.getNombreProducto() + "\nAntes S/ " +
                String.format("%.2f", promocion.getPrecioOriginal()) + " | Oferta S/ " +
                String.format("%.2f", promocion.getPrecioOferta()) + "\nVigencia: " +
                promocion.getFechaInicio() + " al " + promocion.getFechaFin() +
                "\nEstado: " + (promocion.isActiva() ? "Activa" : "Inactiva"));
        datos.setTextSize(13);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(dp(14), 0, 0, 0);
        filaDatos.addView(datos, params);

        item.addView(filaDatos);

        LinearLayout filaBotones = new LinearLayout(this);
        filaBotones.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsBotones = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)
        );
        paramsBotones.setMargins(0, dp(12), 0, 0);
        filaBotones.setLayoutParams(paramsBotones);

        TextView editar = crearBoton("EDITAR", true);
        TextView eliminar = crearBoton("ELIMINAR", false);
        LinearLayout.LayoutParams paramsEditar = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        paramsEditar.setMargins(0, 0, dp(7), 0);
        LinearLayout.LayoutParams paramsEliminar = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        paramsEliminar.setMargins(dp(7), 0, 0, 0);
        filaBotones.addView(editar, paramsEditar);
        filaBotones.addView(eliminar, paramsEliminar);
        item.addView(filaBotones);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarPromocion(promocion);
            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarPromocion(promocion);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promocionViewModel.eliminarPromocionApi(promocion, new PromocionViewModel.ResultadoOperacion() {
                    @Override
                    public void alCompletar(Promocion eliminada) {
                        cargarPromociones();
                    }

                    @Override
                    public void alFallar(String mensaje) {
                        Toast.makeText(PromocionesAdminActivity.this,
                                mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return item;
    }

    private void seleccionarPromocion(Promocion promocion) {
        promocionSeleccionada = promocion;
        edtProductoId.setText(String.valueOf(promocion.getProductoId()));
        edtImagen.setText(promocion.getImagenProducto());
        edtPrecioOriginal.setText(String.valueOf(promocion.getPrecioOriginal()));
        edtPrecioOferta.setText(String.valueOf(promocion.getPrecioOferta()));
        edtFechaInicio.setText(promocion.getFechaInicio());
        edtFechaFin.setText(promocion.getFechaFin());
        actualizarProductoSeleccionado();
    }

    private void limpiar() {
        promocionSeleccionada = null;
        edtProductoId.setText("");
        edtImagen.setText("");
        edtPrecioOriginal.setText("");
        edtPrecioOferta.setText("");
        establecerFechasIniciales();
        txtProductoSeleccionado.setText("Ningún producto seleccionado");
        txtDetalleProducto.setText("Toca la imagen o el botón para elegir un producto.");
        mostrarImagen("");
    }

    private void configurarSelectorFecha(final EditText campo) {
        campo.setFocusable(false);
        campo.setClickable(true);
        campo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar fecha = Calendar.getInstance();
                String actual = campo.getText().toString().trim();

                try {
                    fecha.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(actual));
                } catch (Exception ignored) {
                }

                new DatePickerDialog(
                        PromocionesAdminActivity.this,
                        (datePicker, anio, mes, dia) ->
                                campo.setText(String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia)),
                        fecha.get(Calendar.YEAR),
                        fecha.get(Calendar.MONTH),
                        fecha.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
    }

    private void establecerFechasIniciales() {
        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        fin.add(Calendar.DAY_OF_MONTH, 7);
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        edtFechaInicio.setText(formato.format(inicio.getTime()));
        edtFechaFin.setText(formato.format(fin.getTime()));
    }

    private TextView crearTexto(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(12);
        textView.setPadding(0, 6, 0, 6);
        return textView;
    }

    private void mostrarImagen(String imagen) {
        mostrarImagenEnVista(imgPreview, imagen);
    }

    private int obtenerDrawable(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.trim().isEmpty()) {
            return R.drawable.ic_tag;
        }

        int idImagen = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());
        return idImagen == 0 ? R.drawable.ic_tag : idImagen;
    }

    private void mostrarImagenEnVista(ImageView imageView, String imagen) {
        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                imagen,
                R.drawable.ic_tag
        );
    }

    private void abrirSeleccionProducto() {
        seleccionarProductoLauncher.launch(new Intent(PromocionesAdminActivity.this, SeleccionarProductoPromocionActivity.class));
    }

    private TextView crearBoton(String texto, boolean principal) {
        TextView boton = crearTexto(texto);
        boton.setGravity(android.view.Gravity.CENTER);
        boton.setTypeface(null, android.graphics.Typeface.BOLD);
        boton.setTextColor(principal ? 0xFFFFFFFF : 0xFFFF4D4D);
        boton.setBackgroundResource(principal ? R.drawable.bg_btn_continuar_pedido : R.drawable.bg_card_admin_moderno);
        return boton;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
