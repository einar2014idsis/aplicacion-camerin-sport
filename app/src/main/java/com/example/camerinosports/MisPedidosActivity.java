package com.example.camerinosports;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.PedidoDetalle;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.viewmodel.PedidoViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class MisPedidosActivity extends AppCompatActivity {

    private PedidoViewModel pedidoViewModel;
    private LinearLayout contenedorLista;
    private TextView txtPendientes;
    private TextView txtCamino;
    private TextView txtEntregados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pedidos);

        pedidoViewModel = new PedidoViewModel(this);
        enlazarVistas();
        configurarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPedidos();
    }

    private void enlazarVistas() {
        contenedorLista = findViewById(R.id.contenedorListaMisPedidos);
        txtPendientes = findViewById(R.id.txtPedidosPendientesUsuario);
        txtCamino = findViewById(R.id.txtPedidosCaminoUsuario);
        txtEntregados = findViewById(R.id.txtPedidosEntregadosUsuario);
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverMisPedidos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarPedidos() {
        contenedorLista.removeAllViews();
        pedidoViewModel.listarPedidosApi(
                UsuarioSesion.getCorreoActual(),
                new PedidoViewModel.ResultadoLista() {
                    @Override
                    public void alCompletar(ArrayList<Pedido> pedidos) {
                        mostrarPedidos(pedidos);
                    }

                    @Override
                    public void alFallar(String mensaje) {
                        contenedorLista.addView(crearTexto(mensaje));
                        Toast.makeText(MisPedidosActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarPedidos(ArrayList<Pedido> pedidos) {
        actualizarResumen(pedidos);

        if (pedidos.isEmpty()) {
            contenedorLista.addView(crearTexto("No tienes pedidos registrados"));
            Toast.makeText(this, "no tienes pedidos registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        int i;
        for (i = 0; i < pedidos.size(); i++) {
            contenedorLista.addView(crearTarjetaPedido(pedidos.get(i)));
        }
    }

    private ArrayList<Pedido> filtrarPedidosUsuario(ArrayList<Pedido> todos) {
        ArrayList<Pedido> filtrados = new ArrayList<>();
        String correo = UsuarioSesion.getCorreoActual();
        int i;

        for (i = 0; i < todos.size(); i++) {
            Pedido pedido = todos.get(i);
            if (correo == null || correo.trim().isEmpty() || correo.equals(pedido.getCorreo())) {
                filtrados.add(pedido);
            }
        }

        return filtrados;
    }

    private void actualizarResumen(ArrayList<Pedido> pedidos) {
        int pendientes = 0;
        int camino = 0;
        int entregados = 0;
        int i;

        for (i = 0; i < pedidos.size(); i++) {
            String estado = pedidos.get(i).getEstado();
            if ("Pedido confirmado".equals(estado) || "Preparando pedido".equals(estado)) {
                pendientes++;
            } else if ("En camino".equals(estado)) {
                camino++;
            } else if ("Entregado".equals(estado)) {
                entregados++;
            }
        }

        txtPendientes.setText("Confirmados\n" + pendientes);
        txtCamino.setText("En camino\n" + camino);
        txtEntregados.setText("Entregados\n" + entregados);
    }

    private LinearLayout crearTarjetaPedido(final Pedido pedido) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.fondo_item_pedido);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(cardParams);
ArrayList<PedidoDetalle> detalles = pedido.getDetalles() == null
                ? new ArrayList<>()
                : pedido.getDetalles();
        String imagenProducto = detalles.isEmpty() ? null : detalles.get(0).getImagenProducto();

        LinearLayout cabecera = new LinearLayout(this);
        cabecera.setOrientation(LinearLayout.HORIZONTAL);
        cabecera.setGravity(Gravity.CENTER_VERTICAL);

        cabecera.addView(crearVistaImagen(imagenProducto), new LinearLayout.LayoutParams(dp(88), dp(88)));

        LinearLayout datosCabecera = new LinearLayout(this);
        datosCabecera.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams datosParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        datosParams.setMargins(dp(14), 0, dp(8), 0);

        TextView titulo = crearTexto("Pedido #CS-" + String.format(Locale.getDefault(), "%05d", pedido.getId()));
        titulo.setTextColor(Color.WHITE);
        titulo.setTextSize(17);
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView productos = crearTexto(crearResumenProductos(detalles));
        productos.setTextSize(13);

        TextView fecha = crearTexto(textoSeguro(pedido.getFecha(), "Fecha no disponible"));
        fecha.setTextSize(12);

        TextView estado = crearTexto(textoSeguro(pedido.getEstado(), "Pedido confirmado"));
        estado.setTypeface(null, android.graphics.Typeface.BOLD);
        estado.setTextColor(colorEstado(pedido.getEstado()));
        estado.setPadding(0, dp(3), 0, dp(3));
        LinearLayout.LayoutParams estadoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        estadoParams.setMargins(0, dp(4), 0, 0);

        datosCabecera.addView(titulo);
        datosCabecera.addView(productos);
        datosCabecera.addView(fecha);
        datosCabecera.addView(estado, estadoParams);
        cabecera.addView(datosCabecera, datosParams);
        card.addView(cabecera);

        View separador = new View(this);
        separador.setBackgroundColor(0xFF29445C);
        LinearLayout.LayoutParams separadorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
        );
        separadorParams.setMargins(0, dp(16), 0, dp(16));
        card.addView(separador, separadorParams);

        TextView direccion = crearTexto("Entrega: " + textoSeguro(pedido.getDireccion(), "Sin dirección"));
        direccion.setTextSize(13);
        direccion.setPadding(dp(4), dp(4), dp(4), dp(4));
        card.addView(direccion);

        String pago = textoSeguro(pedido.getMetodoPago(), "No especificado");
        TextView metodoPago = crearTexto("Pago: " + pago);
        metodoPago.setTextSize(12);
        metodoPago.setPadding(dp(4), dp(4), dp(4), dp(4));
        card.addView(metodoPago);

        LinearLayout pie = new LinearLayout(this);
        pie.setOrientation(LinearLayout.HORIZONTAL);
        pie.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams pieParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
        );
        pieParams.setMargins(0, dp(12), 0, 0);
        pie.setLayoutParams(pieParams);

        TextView total = crearTexto("Total: S/ " + String.format(Locale.getDefault(), "%.2f", pedido.getTotal()));
        total.setTextColor(Color.WHITE);
        total.setTextSize(16);
        total.setTypeface(null, android.graphics.Typeface.BOLD);
        total.setGravity(Gravity.CENTER_VERTICAL);
        total.setPadding(dp(4), 0, dp(4), 0);
        pie.addView(total, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        TextView boton = crearTexto("VER RASTREO");
        boton.setGravity(Gravity.CENTER);
        boton.setTextColor(Color.WHITE);
        boton.setTypeface(null, android.graphics.Typeface.BOLD);
        boton.setBackgroundResource(R.drawable.bg_btn_continuar_pedido);
        pie.addView(boton, new LinearLayout.LayoutParams(dp(148), LinearLayout.LayoutParams.MATCH_PARENT));
        card.addView(pie);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirSeguimiento(pedido.getId());
            }
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirSeguimiento(pedido.getId());
            }
        });

        return card;
    }

    private String crearResumenProductos(ArrayList<PedidoDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return "Sin productos registrados";
        }

        PedidoDetalle primero = detalles.get(0);
        int cantidadTotal = 0;

        for (int i = 0; i < detalles.size(); i++) {
            cantidadTotal += detalles.get(i).getCantidad();
        }

        String resumen = primero.getNombreProducto() + " x" + primero.getCantidad();
        if (detalles.size() > 1) {
            resumen += " y " + (detalles.size() - 1) + " producto(s) más";
        }

        return resumen + " · " + cantidadTotal + " unidad(es)";
    }

    private View crearVistaImagen(String imagen) {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundResource(R.drawable.bg_card_admin_moderno);
        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                imagen,
                R.drawable.ic_tag
        );
        return imageView;
    }

    private int colorEstado(String estado) {
        if ("Entregado".equals(estado)) {
            return 0xFF00E88A;
        }

        if ("En camino".equals(estado)) {
            return 0xFF00AFFF;
        }

        if ("Cancelado".equals(estado)) {
            return 0xFFFF5555;
        }

        return 0xFFFFB000;
    }

    private String textoSeguro(String texto, String valorDefecto) {
        return texto == null || texto.trim().isEmpty() ? valorDefecto : texto.trim();
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    private TextView crearTexto(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFC8D3E0);
        textView.setTextSize(12);
        textView.setPadding(0, 6, 0, 6);
        return textView;
    }

    private void abrirSeguimiento(int pedidoId) {
        Intent intent = new Intent(this, SeguimientoPedidoActivity.class);
        intent.putExtra("pedidoId", pedidoId);
        startActivity(intent);
    }
}
