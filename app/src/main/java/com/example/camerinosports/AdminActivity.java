package com.example.camerinosports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.Producto;
import com.example.camerinosports.model.Promocion;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.viewmodel.PedidoViewModel;
import com.example.camerinosports.viewmodel.ProductoViewModel;
import com.example.camerinosports.viewmodel.PromocionViewModel;
import com.example.camerinosports.viewmodel.UsuarioViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    private ProductoViewModel productoViewModel;
    private PedidoViewModel pedidoViewModel;
    private PromocionViewModel promocionViewModel;
    private UsuarioViewModel usuarioViewModel;

    private TextView txtTotalProductos;
    private TextView txtTotalPedidos;
    private TextView txtTotalStockBajo;
    private TextView txtTotalPromos;
    private TextView txtActividadPedido;
    private TextView txtActividadStock;
    private TextView txtActividadPromo;
    private TextView txtActividadProducto;
    private TextView txtClientes;
    private TextView txtVentas;
    private TextView txtIngresos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cargar pantalla del administrador
        setContentView(R.layout.activity_admin);

        inicializarDatos();
        enlazarVistas();
        configurarNavegacion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarResumenReal();
    }

    private void inicializarDatos() {
        productoViewModel = new ProductoViewModel(this);
        pedidoViewModel = new PedidoViewModel(this);
        promocionViewModel = new PromocionViewModel(this);
        usuarioViewModel = new UsuarioViewModel(this);
    }

    private void enlazarVistas() {
        txtTotalProductos = findViewById(R.id.txtTotalProductosAdmin);
        txtTotalPedidos = findViewById(R.id.txtTotalPedidosAdmin);
        txtTotalStockBajo = findViewById(R.id.txtTotalStockBajoAdmin);
        txtTotalPromos = findViewById(R.id.txtTotalPromosAdmin);
        txtActividadPedido = findViewById(R.id.txtActividadPedidoAdmin);
        txtActividadStock = findViewById(R.id.txtActividadStockAdmin);
        txtActividadPromo = findViewById(R.id.txtActividadPromoAdmin);
        txtActividadProducto = findViewById(R.id.txtActividadProductoAdmin);
        txtClientes = findViewById(R.id.txtClientesAdmin);
        txtVentas = findViewById(R.id.txtVentasAdmin);
        txtIngresos = findViewById(R.id.txtIngresosAdmin);
    }

    private void configurarNavegacion() {
        findViewById(R.id.cardGestionProductos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGestionProductos();
            }
        });

        findViewById(R.id.cardPedidosAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, PedidosAdminActivity.class));
            }
        });

        findViewById(R.id.cardPromocionesAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, PromocionesAdminActivity.class));
            }
        });

        findViewById(R.id.cardConsejosAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, ConsejosAdminActivity.class));
            }
        });

        findViewById(R.id.cardActividadRecienteAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, PedidosAdminActivity.class));
            }
        });

        findViewById(R.id.btnVerActividadAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, PedidosAdminActivity.class));
            }
        });

        findViewById(R.id.btnCerrarSesionAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsuarioSesion.cerrarSesion();
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void abrirGestionProductos() {
        Intent intent = new Intent(AdminActivity.this, GestionProductosActivity.class);
        startActivity(intent);
    }

    private void cargarResumenReal() {
        ArrayList<Producto> productosEnCache = productoViewModel.listarProductos();
        ArrayList<Producto> stockBajoEnCache = productoViewModel.listarProductosConStockBajo();
        txtTotalProductos.setText(String.valueOf(productosEnCache.size()));
        txtTotalStockBajo.setText(String.valueOf(stockBajoEnCache.size()));
        txtTotalPromos.setText("-");
        txtClientes.setText("Clientes\n-\nregistrados");
        actualizarActividadProductos(productosEnCache, stockBajoEnCache);

        productoViewModel.sincronizarProductos(new ProductoViewModel.ResultadoSincronizacion() {
            @Override
            public void alCompletar() {
                ArrayList<Producto> productosActualizados = productoViewModel.listarProductos();
                ArrayList<Producto> stockActualizado = productoViewModel.listarProductosConStockBajo();
                txtTotalProductos.setText(String.valueOf(productosActualizados.size()));
                txtTotalStockBajo.setText(String.valueOf(stockActualizado.size()));
                actualizarActividadProductos(productosActualizados, stockActualizado);
            }

            @Override
            public void alFallar() {
                ArrayList<Producto> productosDisponibles = productoViewModel.listarProductos();
                ArrayList<Producto> stockDisponible = productoViewModel.listarProductosConStockBajo();
                txtTotalProductos.setText(String.valueOf(productosDisponibles.size()));
                txtTotalStockBajo.setText(String.valueOf(stockDisponible.size()));
                actualizarActividadProductos(productosDisponibles, stockDisponible);
            }
        });

        promocionViewModel.listarPromocionesApi(true, new PromocionViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Promocion> promociones) {
                txtTotalPromos.setText(String.valueOf(promociones.size()));
                actualizarActividadPromociones(promociones);
            }

            @Override
            public void alFallar(String mensaje) {
                txtTotalPromos.setText("-");
            }
        });

        usuarioViewModel.contarClientesApi(new UsuarioViewModel.ResultadoConteo() {
            @Override
            public void alCompletar(int total) {
                txtClientes.setText("Clientes\n" + total + "\nregistrados");
            }

            @Override
            public void alFallar(String mensaje) {
                txtClientes.setText("Clientes\nNo disponibles");
            }
        });
        pedidoViewModel.listarPedidosApi(null, new PedidoViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Pedido> pedidos) {
                txtTotalPedidos.setText(String.valueOf(pedidos.size()));
                txtVentas.setText("Ventas\n" + contarPedidosVendidos(pedidos) + "\npedidos");
                txtIngresos.setText("Ingresos\nS/ " +
                        formatearDinero(calcularIngresos(pedidos)) + "\nreales");
                actualizarActividadPedidos(pedidos);
            }

            @Override
            public void alFallar(String mensaje) {
                txtTotalPedidos.setText("-");
                txtVentas.setText("Ventas\nNo disponibles");
                txtIngresos.setText("Ingresos\nNo disponibles");
            }
        });
    }

    private void actualizarActividadPedidos(ArrayList<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            txtActividadPedido.setText("• No hay pedidos registrados");
        } else {
            Pedido pedido = pedidos.get(0);
            txtActividadPedido.setText(
                    "• Pedido " + obtenerCodigoPedido(pedido) + " " + textoEstado(pedido.getEstado()));
        }
    }

    private void cargarActividadReciente(ArrayList<Pedido> pedidos, ArrayList<Producto> stockBajo,
                                         ArrayList<Promocion> promocionesActivas, int totalPromocionesActivas,
                                         ArrayList<Producto> productos) {
        if (pedidos.isEmpty()) {
            txtActividadPedido.setText("• No hay pedidos registrados");
        } else {
            Pedido pedido = pedidos.get(0);
            txtActividadPedido.setText("• Pedido " + obtenerCodigoPedido(pedido) + " " + textoEstado(pedido.getEstado()));
        }

        if (stockBajo.isEmpty()) {
            txtActividadStock.setText("• No hay productos con stock bajo");
        } else {
            Producto producto = stockBajo.get(0);
            txtActividadStock.setText("• Stock bajo: " + producto.getNombre() + " (" + producto.getStock() + ")");
        }

        if (totalPromocionesActivas == 0) {
            txtActividadPromo.setText("• No hay promociones activas");
        } else if (promocionesActivas.isEmpty()) {
            txtActividadPromo.setText("• " + totalPromocionesActivas + textoPromocionesActivas(totalPromocionesActivas));
        } else {
            Promocion promocion = promocionesActivas.get(0);
            txtActividadPromo.setText("• " + totalPromocionesActivas +
                    textoPromocionesActivas(totalPromocionesActivas) + ": " + promocion.getNombreProducto());
        }

        if (productos.isEmpty()) {
            txtActividadProducto.setText("• No hay productos registrados");
        } else {
            Producto producto = productos.get(0);
            txtActividadProducto.setText("• Producto registrado: " + producto.getNombre());
        }
    }

    private void actualizarActividadProductos(ArrayList<Producto> productos,
                                               ArrayList<Producto> stockBajo) {
        if (stockBajo.isEmpty()) {
            txtActividadStock.setText("• No hay productos con stock bajo");
        } else {
            Producto producto = stockBajo.get(0);
            txtActividadStock.setText(
                    "• Stock bajo: " + producto.getNombre() + " (" + producto.getStock() + ")");
        }

        if (productos.isEmpty()) {
            txtActividadProducto.setText("• No hay productos registrados");
        } else {
            txtActividadProducto.setText("• Producto registrado: " + productos.get(0).getNombre());
        }
    }

    private void actualizarActividadPromociones(ArrayList<Promocion> promociones) {
        int cantidad = promociones.size();
        if (cantidad == 0) {
            txtActividadPromo.setText("• No hay promociones activas");
            return;
        }

        txtActividadPromo.setText("• " + cantidad + textoPromocionesActivas(cantidad) +
                ": " + promociones.get(0).getNombreProducto());
    }

    private int contarPedidosVendidos(ArrayList<Pedido> pedidos) {
        int total = 0;

        for (int i = 0; i < pedidos.size(); i++) {
            if (!"Cancelado".equals(pedidos.get(i).getEstado())) {
                total++;
            }
        }

        return total;
    }

    private double calcularIngresos(ArrayList<Pedido> pedidos) {
        double total = 0;

        for (int i = 0; i < pedidos.size(); i++) {
            Pedido pedido = pedidos.get(i);
            if (!"Cancelado".equals(pedido.getEstado())) {
                total += pedido.getTotal();
            }
        }

        return total;
    }

    private String obtenerCodigoPedido(Pedido pedido) {
        return "#CS-" + String.format(Locale.getDefault(), "%05d", pedido.getId());
    }

    private String textoEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return "registrado";
        }

        return estado.toLowerCase(Locale.getDefault());
    }

    private String formatearDinero(double valor) {
        return String.format(Locale.getDefault(), "%.2f", valor);
    }

    private String textoPromocionesActivas(int cantidad) {
        return cantidad == 1 ? " promoción activa" : " promociones activas";
    }
}
