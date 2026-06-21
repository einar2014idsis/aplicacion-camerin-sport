package com.example.camerinosports;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.CarritoMemoria;
import com.example.camerinosports.model.Producto;
import com.example.camerinosports.model.Promocion;
import com.example.camerinosports.viewmodel.ProductoViewModel;
import com.example.camerinosports.viewmodel.PromocionViewModel;

public class DetalleProductoActivity extends AppCompatActivity {

    private ProductoViewModel productoViewModel;
    private PromocionViewModel promocionViewModel;
    private Producto producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        productoViewModel = new ProductoViewModel(this);
        promocionViewModel = new PromocionViewModel(this);

        int productoId = getIntent().getIntExtra("productoId", -1);
        producto = productoViewModel.obtenerProductoPorId(productoId);

        if (producto == null) {
            Toast.makeText(this, "producto no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mostrarProducto();
        configurarEventos();
    }

    private void mostrarProducto() {
        ImageView imgProductoDetalle = findViewById(R.id.imgProductoDetalle);
        TextView txtNombreDetalle = findViewById(R.id.txtNombreDetalle);
        TextView txtCategoriaDetalle = findViewById(R.id.txtCategoriaDetalle);
        TextView txtPrecioDetalle = findViewById(R.id.txtPrecioDetalle);
        TextView txtDisponibleDetalle = findViewById(R.id.txtDisponibleDetalle);
        TextView txtEtiquetaTallaDetalle = findViewById(R.id.txtEtiquetaTallaDetalle);
        TextView txtTallaDetalle = findViewById(R.id.txtTallaDetalle);
        TextView txtColorDetalle = findViewById(R.id.txtColorDetalle);
        TextView txtDescripcionDetalle = findViewById(R.id.txtDescripcionDetalle);
        TextView txtStockDetalle = findViewById(R.id.txtStockDetalle);
        TextView txtBadgeCarritoDetalle = findViewById(R.id.txtBadgeCarritoDetalle);

        txtNombreDetalle.setText(producto.getNombre());
        txtCategoriaDetalle.setText(producto.getCategoria() + (producto.isSostenible() ? " | producto sostenible" : ""));
        configurarDatosPorCategoria(txtEtiquetaTallaDetalle, txtTallaDetalle, txtColorDetalle);
        txtDescripcionDetalle.setText(producto.getDescripcion());
        txtStockDetalle.setText("stock disponible: " + producto.getStock() + " unidades");
        txtDisponibleDetalle.setText(producto.getStock() > 0 ? "disponible" : "agotado");
        txtBadgeCarritoDetalle.setText(String.valueOf(CarritoMemoria.contarProductos()));

        txtPrecioDetalle.setText("S/ " + String.format("%.2f", producto.getPrecio()));
        promocionViewModel.obtenerPromocionApi(producto.getId(), new PromocionViewModel.ResultadoOperacion() {
            @Override
            public void alCompletar(Promocion promocion) {
                if (promocion != null) {
                    txtPrecioDetalle.setText("S/ " + String.format("%.2f", promocion.getPrecioOferta()) +
                            " antes S/ " + String.format("%.2f", promocion.getPrecioOriginal()));
                }
            }

            @Override
            public void alFallar(String mensaje) {
                // se conserva visible el precio normal del producto.
            }
        });

        mostrarImagen(imgProductoDetalle, producto.getImagen());
    }

    private void configurarDatosPorCategoria(TextView etiqueta, TextView valor, TextView color) {
        String categoria = producto.getCategoria() == null ? "" : producto.getCategoria().trim();

        if ("Accesorios".equalsIgnoreCase(categoria)) {
            etiqueta.setText("Presentación");
            valor.setText(textoDisponible(producto.getTalla(), "Unidad"));
        } else {
            etiqueta.setText("Talla");
            valor.setText(textoDisponible(producto.getTalla(), "No especificada"));
        }

        color.setText(textoDisponible(producto.getColor(), "No especificado"));
    }

    private String textoDisponible(String texto, String valorPorDefecto) {
        if (texto == null || texto.trim().isEmpty()) {
            return valorPorDefecto;
        }

        return texto.trim();
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverDetalle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.contenedorCarritoDetalle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetalleProductoActivity.this, CarritoActivity.class));
            }
        });

        findViewById(R.id.btnAgregarPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAlCarrito();
            }
        });
    }

    private void agregarAlCarrito() {
        if (producto.getStock() <= 0) {
            Toast.makeText(this, "producto sin stock", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean agregado = CarritoMemoria.agregarProducto(producto);

        if (agregado) {
            Toast.makeText(this, "producto agregado", Toast.LENGTH_SHORT).show();
            mostrarProducto();
        } else {
            Toast.makeText(this, "no puedes superar el stock", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarImagen(ImageView imageView, String nombreImagen) {
        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                nombreImagen,
                R.drawable.ic_tag
        );
    }
}
