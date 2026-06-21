package com.example.camerinosports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camerinosports.adapter.CarritoAdapter;
import com.example.camerinosports.model.CarritoItem;
import com.example.camerinosports.model.CarritoMemoria;

import java.util.ArrayList;
import java.util.Locale;

public class CarritoActivity extends AppCompatActivity {

    private View itemPedido1;
    private View itemPedido2;
    private TextView txtTotalPedido;
    private TextView txtSubtotalCantidadPedido;
    private TextView txtSubtotalPedido;
    private TextView badgeCarrito;
    private TextView badgeCarritoBottom;
    private RecyclerView recyclerCarrito;
    private CarritoAdapter carritoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        enlazarVistas();
        configurarEventos();
        cargarCarrito();
    }

    private void enlazarVistas() {
        itemPedido1 = findViewById(R.id.itemPedido1);
        itemPedido2 = findViewById(R.id.itemPedido2);
        txtTotalPedido = findViewById(R.id.txtTotalPedido);
        txtSubtotalCantidadPedido = findViewById(R.id.txtSubtotalCantidadPedido);
        txtSubtotalPedido = findViewById(R.id.txtSubtotalPedido);
        badgeCarrito = findViewById(R.id.badgeCarrito);
        badgeCarritoBottom = findViewById(R.id.badgeCarritoBottom);
        recyclerCarrito = findViewById(R.id.recyclerCarrito);

        itemPedido1.setVisibility(View.GONE);
        itemPedido2.setVisibility(View.GONE);
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.navInicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CarritoActivity.this, CatalogoActivity.class));
                finish();
            }
        });

        findViewById(R.id.navCategorias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarritoActivity.this, CatalogoActivity.class);
                intent.putExtra("mostrarCategorias", true);
                startActivity(intent);
            }
        });

        findViewById(R.id.navPerfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CarritoActivity.this, PerfilUsuarioActivity.class));
            }
        });

        findViewById(R.id.btnContinuarPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CarritoMemoria.obtenerItems().isEmpty()) {
                    Toast.makeText(CarritoActivity.this, "el carrito está vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(CarritoActivity.this, ConfirmarPedidoActivity.class));
            }
        });
    }

    private void cargarCarrito() {
        ArrayList<CarritoItem> items = CarritoMemoria.obtenerItems();
        int cantidadProductos = CarritoMemoria.contarProductos();
        double total = CarritoMemoria.calcularTotal();
        actualizarBadgesCarrito(cantidadProductos);
        txtSubtotalCantidadPedido.setText("Subtotal (" + cantidadProductos +
                (cantidadProductos == 1 ? " producto)" : " productos)"));
        txtSubtotalPedido.setText(String.format(Locale.US, "S/ %.2f", total));
        txtTotalPedido.setText(String.format(Locale.US, "S/ %.2f", total));

        if (carritoAdapter == null) {
            carritoAdapter = new CarritoAdapter(this, items, new CarritoAdapter.OnCarritoClickListener() {
                @Override
                public void onAumentar(CarritoItem item) {
                    if (!CarritoMemoria.aumentar(item.getProducto().getId())) {
                        Toast.makeText(CarritoActivity.this, "no puedes superar el stock", Toast.LENGTH_SHORT).show();
                    }
                    cargarCarrito();
                }

                @Override
                public void onDisminuir(CarritoItem item) {
                    CarritoMemoria.disminuir(item.getProducto().getId());
                    cargarCarrito();
                }

                @Override
                public void onEliminar(CarritoItem item) {
                    CarritoMemoria.eliminarProducto(item.getProducto().getId());
                    cargarCarrito();
                }
            });
            recyclerCarrito.setAdapter(carritoAdapter);
        } else {
            carritoAdapter.actualizar();
        }

        if (items.isEmpty()) {
            Toast.makeText(this, "no hay productos en el carrito", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarBadgesCarrito(int cantidad) {
        String textoCantidad = String.valueOf(cantidad);
        int visibilidad = cantidad > 0 ? View.VISIBLE : View.GONE;

        badgeCarrito.setText(textoCantidad);
        badgeCarrito.setVisibility(visibilidad);
        badgeCarritoBottom.setText(textoCantidad);
        badgeCarritoBottom.setVisibility(visibilidad);
    }

    private void cargarItem(View itemView, ArrayList<CarritoItem> items, int posicion) {
        if (posicion >= items.size()) {
            itemView.setVisibility(View.GONE);
            return;
        }

        itemView.setVisibility(View.VISIBLE);
        final CarritoItem item = items.get(posicion);
        int numero = posicion + 1;

        ImageView imagen = findViewById(getResources().getIdentifier("imgProducto" + numero, "id", getPackageName()));
        TextView nombre = findViewById(getResources().getIdentifier("txtProducto" + numero, "id", getPackageName()));
        TextView info = findViewById(getResources().getIdentifier("txtInfoProducto" + numero, "id", getPackageName()));
        TextView disponible = findViewById(getResources().getIdentifier("txtDisponible" + numero, "id", getPackageName()));
        TextView cantidad = findViewById(getResources().getIdentifier("txtCantidad" + numero, "id", getPackageName()));
        TextView precio = findViewById(getResources().getIdentifier("txtPrecio" + numero, "id", getPackageName()));
        View eliminar = findViewById(getResources().getIdentifier("btnEliminar" + numero, "id", getPackageName()));
        View menos = findViewById(getResources().getIdentifier("btnMenos" + numero, "id", getPackageName()));
        View mas = findViewById(getResources().getIdentifier("btnMas" + numero, "id", getPackageName()));

        nombre.setText(item.getProducto().getNombre());
        info.setText("Talla: " + item.getProducto().getTalla() + " | Color: " + item.getProducto().getColor());
        disponible.setText("stock: " + item.getProducto().getStock());
        cantidad.setText(String.valueOf(item.getCantidad()));
        precio.setText("S/ " + String.format("%.2f", item.getSubtotal()));

        com.example.camerinosports.util.Imagenes.mostrar(
                imagen,
                item.getProducto().getImagen(),
                R.drawable.ic_tag
        );

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarritoMemoria.eliminarProducto(item.getProducto().getId());
                cargarCarrito();
            }
        });

        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarritoMemoria.disminuir(item.getProducto().getId());
                cargarCarrito();
            }
        });

        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CarritoMemoria.aumentar(item.getProducto().getId())) {
                    Toast.makeText(CarritoActivity.this, "no puedes superar el stock", Toast.LENGTH_SHORT).show();
                }
                cargarCarrito();
            }
        });
    }
}
