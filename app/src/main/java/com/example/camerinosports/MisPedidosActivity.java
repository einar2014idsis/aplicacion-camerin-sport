package com.example.camerinosports;

import android.content.Intent;
import android.app.Dialog;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

    private LinearLayout crearTarjetaPedidoModerna(final Pedido pedido) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.fondo_item_pedido);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(18));
        card.setLayoutParams(cardParams);

        ArrayList<PedidoDetalle> detalles = pedido.getDetalles() == null
                ? new ArrayList<>()
                : pedido.getDetalles();
        String imagenProducto = detalles.isEmpty() ? null : detalles.get(0).getImagenProducto();

        LinearLayout cabecera = new LinearLayout(this);
        cabecera.setOrientation(LinearLayout.HORIZONTAL);
        cabecera.setGravity(Gravity.TOP);

        cabecera.addView(crearVistaImagen(imagenProducto), new LinearLayout.LayoutParams(dp(118), dp(118)));

        LinearLayout datos = new LinearLayout(this);
        datos.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams datosParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        datosParams.setMargins(dp(16), 0, 0, 0);

        TextView titulo = crearTexto("Pedido #CS-" + String.format(Locale.getDefault(), "%05d", pedido.getId()));
        titulo.setTextColor(Color.WHITE);
        titulo.setTextSize(22);
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView productos = crearTexto(crearResumenPrincipal(detalles));
        productos.setTextSize(15);
        productos.setPadding(0, dp(8), 0, dp(4));

        TextView unidades = crearTexto("Caja: " + contarUnidades(detalles) + " unidades en total");
        unidades.setTextSize(14);
        unidades.setPadding(0, dp(4), 0, dp(4));

        TextView fecha = crearTexto(textoSeguro(pedido.getFecha(), "Fecha no disponible"));
        fecha.setTextSize(14);
        fecha.setPadding(0, dp(4), 0, 0);

        datos.addView(titulo);
        datos.addView(productos);
        datos.addView(unidades);
        datos.addView(fecha);
        cabecera.addView(datos, datosParams);
        card.addView(cabecera);

        card.addView(crearSeparador(dp(18), dp(16)));

        TextView estado = crearTexto("Estado: " + textoSeguro(pedido.getEstado(), "Pedido confirmado"));
        estado.setTextColor(colorEstado(pedido.getEstado()));
        estado.setTextSize(15);
        estado.setTypeface(null, android.graphics.Typeface.BOLD);
        estado.setBackgroundResource(R.drawable.bg_estado_camion_redondo);
        estado.setPadding(dp(14), dp(9), dp(14), dp(9));
        LinearLayout.LayoutParams estadoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        estadoParams.setMargins(0, 0, 0, dp(12));
        card.addView(estado, estadoParams);

        TextView entrega = crearTexto("Entrega: " + textoSeguro(pedido.getDireccion(), "Sin direccion"));
        entrega.setTextColor(Color.WHITE);
        entrega.setTextSize(14);
        entrega.setTypeface(null, android.graphics.Typeface.BOLD);
        entrega.setPadding(0, dp(4), 0, dp(4));
        card.addView(entrega);

        TextView pago = crearTexto("Pago: " + textoSeguro(pedido.getMetodoPago(), "No especificado"));
        pago.setTextColor(Color.WHITE);
        pago.setTextSize(14);
        pago.setTypeface(null, android.graphics.Typeface.BOLD);
        pago.setPadding(0, dp(4), 0, dp(4));
        card.addView(pago);

        card.addView(crearSeparador(dp(18), dp(14)));

        LinearLayout pie = new LinearLayout(this);
        pie.setOrientation(LinearLayout.HORIZONTAL);
        pie.setGravity(Gravity.CENTER_VERTICAL);

        TextView total = crearTexto("Total: S/ " + String.format(Locale.getDefault(), "%.2f", pedido.getTotal()));
        total.setTextColor(Color.WHITE);
        total.setTextSize(20);
        total.setTypeface(null, android.graphics.Typeface.BOLD);
        total.setGravity(Gravity.CENTER_VERTICAL);
        pie.addView(total, new LinearLayout.LayoutParams(0, dp(58), 1));

        TextView contenido = crearBotonTarjeta("Ver contenido", false);
        TextView rastreo = crearBotonTarjeta("Ver rastreo", true);
        LinearLayout.LayoutParams contenidoParams = new LinearLayout.LayoutParams(dp(132), dp(58));
        contenidoParams.setMargins(0, 0, dp(8), 0);
        pie.addView(contenido, contenidoParams);
        pie.addView(rastreo, new LinearLayout.LayoutParams(dp(132), dp(58)));
        card.addView(pie);

        card.setOnClickListener(view -> mostrarDetallePedido(pedido));
        contenido.setOnClickListener(view -> mostrarDetallePedido(pedido));
        rastreo.setOnClickListener(view -> abrirSeguimiento(pedido.getId()));

        return card;
    }

    private View crearSeparador(int margenArriba, int margenAbajo) {
        View separador = new View(this);
        separador.setBackgroundColor(0xFF29445C);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
        );
        params.setMargins(0, margenArriba, 0, margenAbajo);
        separador.setLayoutParams(params);
        return separador;
    }

    private TextView crearBotonTarjeta(String texto, boolean principal) {
        TextView boton = crearTexto(texto);
        boton.setGravity(Gravity.CENTER);
        boton.setTextColor(Color.WHITE);
        boton.setTextSize(12);
        boton.setTypeface(null, android.graphics.Typeface.BOLD);
        boton.setBackgroundResource(principal ? R.drawable.bg_btn_continuar_pedido : R.drawable.bg_card_admin_moderno);
        return boton;
    }

    private String crearResumenPrincipal(ArrayList<PedidoDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return "Sin productos registrados";
        }

        String nombre = textoSeguro(detalles.get(0).getNombreProducto(), "Producto");
        int adicionales = detalles.size() - 1;
        return adicionales > 0
                ? nombre + " + " + adicionales + " productos mas"
                : nombre;
    }

    private int contarUnidades(ArrayList<PedidoDetalle> detalles) {
        int total = 0;
        if (detalles == null) {
            return total;
        }

        for (int i = 0; i < detalles.size(); i++) {
            total += detalles.get(i).getCantidad();
        }

        return total;
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

        ImageButton eliminar = new ImageButton(this);
        eliminar.setImageResource(R.drawable.ic_delete_red);
        eliminar.setBackgroundColor(Color.TRANSPARENT);
        eliminar.setContentDescription("Eliminar pedido");
        eliminar.setPadding(dp(10), dp(10), dp(10), dp(10));

        TextView boton = crearTexto("VER RASTREO");
        boton.setGravity(Gravity.CENTER);
        boton.setTextColor(Color.WHITE);
        boton.setTypeface(null, android.graphics.Typeface.BOLD);
        boton.setBackgroundResource(R.drawable.bg_btn_continuar_pedido);

        LinearLayout.LayoutParams eliminarParams = new LinearLayout.LayoutParams(dp(50), LinearLayout.LayoutParams.MATCH_PARENT);
        eliminarParams.setMargins(0, 0, dp(8), 0);
        pie.addView(eliminar, eliminarParams);
        pie.addView(boton, new LinearLayout.LayoutParams(dp(148), LinearLayout.LayoutParams.MATCH_PARENT));
        card.addView(pie);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDetallePedido(pedido);
            }
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirSeguimiento(pedido.getId());
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarEliminarPedido(pedido);
            }
        });

        return card;
    }

    private void confirmarEliminarPedido(final Pedido pedido) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar pedido")
                .setMessage("Deseas eliminar este pedido de tu historial?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarPedido(pedido))
                .show();
    }

    private void eliminarPedido(final Pedido pedido) {
        pedidoViewModel.eliminarPedidoApi(pedido.getId(), new PedidoViewModel.ResultadoOperacion() {
            @Override
            public void alCompletar() {
                Toast.makeText(MisPedidosActivity.this, "Pedido eliminado", Toast.LENGTH_SHORT).show();
                cargarPedidos();
            }

            @Override
            public void alFallar(String mensaje) {
                Toast.makeText(MisPedidosActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDetallePedido(final Pedido pedido) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout modal = new LinearLayout(this);
        modal.setOrientation(LinearLayout.VERTICAL);
        modal.setBackgroundResource(R.drawable.fondo_resumen_pedido);
        modal.setPadding(dp(20), dp(20), dp(20), dp(20));

        LinearLayout cabecera = new LinearLayout(this);
        cabecera.setOrientation(LinearLayout.HORIZONTAL);
        cabecera.setGravity(Gravity.CENTER_VERTICAL);

        TextView titulo = crearTexto("Contenido del pedido");
        titulo.setTextColor(Color.WHITE);
        titulo.setTextSize(24);
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);
        cabecera.addView(titulo, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView cerrarIcono = crearTexto("X");
        cerrarIcono.setGravity(Gravity.CENTER);
        cerrarIcono.setTextColor(Color.WHITE);
        cerrarIcono.setTextSize(22);
        cerrarIcono.setTypeface(null, android.graphics.Typeface.BOLD);
        cerrarIcono.setBackgroundResource(R.drawable.bg_card_admin_moderno);
        cabecera.addView(cerrarIcono, new LinearLayout.LayoutParams(dp(54), dp(54)));
        modal.addView(cabecera);

        View separador = new View(this);
        separador.setBackgroundColor(0xFF29445C);
        LinearLayout.LayoutParams separadorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
        );
        separadorParams.setMargins(0, dp(18), 0, dp(14));
        modal.addView(separador, separadorParams);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(contenido);
        modal.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        ArrayList<PedidoDetalle> detalles = pedido.getDetalles() == null
                ? new ArrayList<>()
                : pedido.getDetalles();

        if (detalles.isEmpty()) {
            contenido.addView(crearTexto("No hay productos registrados en este pedido"));
        } else {
            for (int i = 0; i < detalles.size(); i++) {
                contenido.addView(crearFilaDetalle(detalles.get(i)));
            }
        }

        LinearLayout totalBox = new LinearLayout(this);
        totalBox.setGravity(Gravity.CENTER_VERTICAL);
        totalBox.setOrientation(LinearLayout.HORIZONTAL);
        totalBox.setBackgroundResource(R.drawable.fondo_item_pedido);
        totalBox.setPadding(dp(16), dp(12), dp(16), dp(12));
        LinearLayout.LayoutParams totalParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        totalParams.setMargins(0, dp(16), 0, dp(18));

        TextView totalLabel = crearTexto("Total del pedido");
        totalLabel.setTextColor(Color.WHITE);
        totalLabel.setTextSize(17);
        totalLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        totalBox.addView(totalLabel, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView total = crearTexto("S/ " + String.format(Locale.getDefault(), "%.2f", pedido.getTotal()));
        total.setTextColor(Color.WHITE);
        total.setTextSize(22);
        total.setTypeface(null, android.graphics.Typeface.BOLD);
        total.setTextColor(0xFF1AB7FF);
        totalBox.addView(total);
        modal.addView(totalBox, totalParams);

        LinearLayout acciones = new LinearLayout(this);
        acciones.setOrientation(LinearLayout.HORIZONTAL);

        TextView cerrar = crearBotonDialog("Cerrar", false);
        TextView rastreo = crearBotonDialog("Ver rastreo  > ", true);
        acciones.addView(cerrar, crearParamsBotonDialog(0, dp(8)));
        acciones.addView(rastreo, crearParamsBotonDialog(dp(8), 0));
        modal.addView(acciones);

        cerrarIcono.setOnClickListener(view -> dialog.dismiss());
        cerrar.setOnClickListener(view -> dialog.dismiss());
        rastreo.setOnClickListener(view -> {
            dialog.dismiss();
            abrirSeguimiento(pedido.getId());
        });

        dialog.setContentView(modal);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.78f);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialog.setOnShowListener(dialogInterface -> {
            Window dialogWindow = dialog.getWindow();
            if (dialogWindow != null) {
                int ancho = (int) (getResources().getDisplayMetrics().widthPixels * 0.88f);
                int altoMaximo = (int) (getResources().getDisplayMetrics().heightPixels * 0.82f);
                dialogWindow.setLayout(ancho, altoMaximo);
            }
        });
        dialog.show();
    }

    private View crearFilaDetalle(PedidoDetalle detalle) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setBackgroundResource(R.drawable.fondo_resumen_pedido);
        fila.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams filaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        filaParams.setMargins(0, 0, 0, dp(10));
        fila.setLayoutParams(filaParams);

        fila.addView(crearVistaImagen(detalle.getImagenProducto()), new LinearLayout.LayoutParams(dp(72), dp(72)));

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textosParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textosParams.setMargins(dp(12), 0, 0, 0);

        TextView nombre = crearTexto(textoSeguro(detalle.getNombreProducto(), "Producto"));
        nombre.setTextColor(Color.WHITE);
        nombre.setTextSize(14);
        nombre.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView cantidad = crearTexto("Cantidad: " + detalle.getCantidad() +
                "  |  Precio: S/ " + String.format(Locale.getDefault(), "%.2f", detalle.getPrecio()));
        cantidad.setTextSize(12);

        TextView subtotal = crearTexto("Subtotal: S/ " +
                String.format(Locale.getDefault(), "%.2f", detalle.getSubtotal()));
        subtotal.setTextColor(0xFF00AFFF);
        subtotal.setTextSize(13);
        subtotal.setTypeface(null, android.graphics.Typeface.BOLD);

        textos.addView(nombre);
        textos.addView(cantidad);
        fila.addView(textos, textosParams);

        LinearLayout bloqueSubtotal = new LinearLayout(this);
        bloqueSubtotal.setOrientation(LinearLayout.VERTICAL);
        bloqueSubtotal.setGravity(Gravity.END);
        TextView etiquetaSubtotal = crearTexto("Subtotal");
        etiquetaSubtotal.setGravity(Gravity.END);
        etiquetaSubtotal.setTextSize(12);
        subtotal.setGravity(Gravity.END);
        bloqueSubtotal.addView(etiquetaSubtotal);
        bloqueSubtotal.addView(subtotal);
        fila.addView(bloqueSubtotal, new LinearLayout.LayoutParams(dp(104), LinearLayout.LayoutParams.WRAP_CONTENT));

        return fila;
    }

    private TextView crearBotonDialog(String texto, boolean principal) {
        TextView boton = crearTexto(texto);
        boton.setGravity(Gravity.CENTER);
        boton.setTextColor(Color.WHITE);
        boton.setTextSize(15);
        boton.setTypeface(null, android.graphics.Typeface.BOLD);
        boton.setBackgroundResource(principal ? R.drawable.bg_btn_continuar_pedido : R.drawable.bg_card_admin_moderno);
        return boton;
    }

    private LinearLayout.LayoutParams crearParamsBotonDialog(int margenInicio, int margenFin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(58), 1);
        params.setMargins(margenInicio, 0, margenFin, 0);
        return params;
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
