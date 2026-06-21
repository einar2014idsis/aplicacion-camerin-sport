package com.example.camerinosports;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.viewmodel.ProductoViewModel;

import java.util.ArrayList;

public class SeleccionarProductoPromocionActivity extends AppCompatActivity {

    private ProductoViewModel productoViewModel;
    private LinearLayout contenedorProductos;
    private EditText edtBuscar;
    private ArrayList<Producto> productos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_producto_promocion);

        productoViewModel = new ProductoViewModel(this);
        contenedorProductos = findViewById(R.id.contenedorProductosPromocion);
        edtBuscar = findViewById(R.id.txtBuscarProductoPromocion);
        configurarEventos();
        productoViewModel.sincronizarProductos(new ProductoViewModel.ResultadoSincronizacion() {
            @Override
            public void alCompletar() {
                cargarProductos();
            }

            @Override
            public void alFallar() {
                contenedorProductos.addView(crearTexto(
                        "No se pudieron cargar los productos desde el servidor"));
            }
        });
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverSeleccionarProductoPromocion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mostrarProductosFiltrados(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void cargarProductos() {
        contenedorProductos.removeAllViews();
        productos = productoViewModel.listarProductos();

        if (productos.isEmpty()) {
            contenedorProductos.addView(crearTexto("No hay productos registrados"));
            Toast.makeText(this, "no hay productos registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarProductos(productos);
    }

    private void mostrarProductosFiltrados(String texto) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase();

        if (filtro.isEmpty()) {
            mostrarProductos(productos);
            return;
        }

        ArrayList<Producto> filtrados = new ArrayList<>();
        int i;

        for (i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            String nombre = producto.getNombre() == null ? "" : producto.getNombre().toLowerCase();
            String categoria = producto.getCategoria() == null ? "" : producto.getCategoria().toLowerCase();

            if (nombre.contains(filtro) || categoria.contains(filtro)) {
                filtrados.add(producto);
            }
        }

        mostrarProductos(filtrados);
    }

    private void mostrarProductos(ArrayList<Producto> lista) {
        contenedorProductos.removeAllViews();

        if (lista.isEmpty()) {
            contenedorProductos.addView(crearTexto("No hay productos con ese nombre o categoría"));
            return;
        }

        int i;
        for (i = 0; i < lista.size(); i++) {
            contenedorProductos.addView(crearItemProducto(lista.get(i)));
        }
    }

    private LinearLayout crearItemProducto(final Producto producto) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setPadding(dp(14), dp(14), dp(14), dp(14));
        item.setBackgroundResource(R.drawable.fondo_item_pedido);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(128)
        );
        itemParams.setMargins(0, 0, 0, dp(14));
        item.setLayoutParams(itemParams);

        ImageView imagen = new ImageView(this);
        mostrarImagenProducto(imagen, producto.getImagen());
        imagen.setBackgroundResource(R.drawable.bg_card_admin_moderno);
        imagen.setPadding(dp(6), dp(6), dp(6), dp(6));
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        item.addView(imagen, new LinearLayout.LayoutParams(dp(104), dp(96)));

        TextView datos = crearTexto(producto.getNombre() + "\n" + producto.getCategoria() +
                " | Stock: " + producto.getStock() + "\nS/ " + String.format("%.2f", producto.getPrecio()));
        datos.setTextSize(14);
        datos.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(dp(14), 0, dp(12), 0);
        item.addView(datos, params);

        TextView elegir = crearTexto("ELEGIR");
        elegir.setGravity(android.view.Gravity.CENTER);
        elegir.setTextColor(0xFFFFFFFF);
        elegir.setTypeface(null, android.graphics.Typeface.BOLD);
        elegir.setBackgroundResource(R.drawable.bg_btn_continuar_pedido);
        item.addView(elegir, new LinearLayout.LayoutParams(dp(92), dp(44)));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devolverProducto(producto);
            }
        };
        item.setOnClickListener(listener);
        elegir.setOnClickListener(listener);

        return item;
    }

    private TextView crearTexto(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(12);
        textView.setPadding(0, 6, 0, 6);
        return textView;
    }

    private void devolverProducto(Producto producto) {
        Intent data = new Intent();
        data.putExtra("productoId", producto.getId());
        data.putExtra("imagen", producto.getImagen());
        setResult(RESULT_OK, data);
        finish();
    }

    private void mostrarImagenProducto(ImageView imageView, String imagen) {
        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                imagen,
                R.drawable.ic_tag
        );
    }

    private int obtenerDrawableProducto(String nombreImagen) {
        int idImagen = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());
        return idImagen == 0 ? R.drawable.ic_tag : idImagen;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
