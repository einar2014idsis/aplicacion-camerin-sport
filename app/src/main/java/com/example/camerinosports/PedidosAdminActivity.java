package com.example.camerinosports;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.PedidoDetalle;
import com.example.camerinosports.viewmodel.PedidoViewModel;
import com.example.camerinosports.viewmodel.ProductoViewModel;

import java.util.ArrayList;

public class PedidosAdminActivity extends AppCompatActivity {

    private PedidoViewModel pedidoViewModel;
    private ArrayList<Pedido> pedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_admin);

        pedidoViewModel = new PedidoViewModel(this);
        configurarEventos();
        cargarPedidos();
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverPedidosAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarPedidos() {
        pedidoViewModel.listarPedidosApi(null, new PedidoViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Pedido> pedidosServidor) {
                pedidos = pedidosServidor;
                mostrarPedidos();
            }

            @Override
            public void alFallar(String mensaje) {
                Toast.makeText(PedidosAdminActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarPedidos() {
        actualizarResumen();
        limpiarTarjetasExtra();
        findViewById(R.id.cardPedidoUno).setVisibility(View.GONE);
        findViewById(R.id.cardPedidoDos).setVisibility(View.GONE);
        findViewById(R.id.cardPedidoTres).setVisibility(View.GONE);
        cargarTarjetasExtra();

        if (pedidos.isEmpty()) {
            Toast.makeText(this, "no hay pedidos registrados", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarResumen() {
        int pendientes = 0;
        int proceso = 0;
        int atendidos = 0;
        int i;

        for (i = 0; i < pedidos.size(); i++) {
            Pedido pedido = pedidos.get(i);
            if ("Pedido confirmado".equals(pedido.getEstado())) {
                pendientes++;
            } else if ("Preparando pedido".equals(pedido.getEstado()) || "En camino".equals(pedido.getEstado())) {
                proceso++;
            } else if ("Entregado".equals(pedido.getEstado())) {
                atendidos++;
            }
        }

        ((TextView) findViewById(R.id.txtCantidadPendientes)).setText(String.valueOf(pendientes));
        ((TextView) findViewById(R.id.txtCantidadProceso)).setText(String.valueOf(proceso));
        ((TextView) findViewById(R.id.txtCantidadCompletados)).setText(String.valueOf(atendidos));
    }

    private void cargarTarjeta(int cardId, int verId, int estadoId, int cancelarId, final int posicion) {
        View card = findViewById(cardId);

        if (posicion >= pedidos.size()) {
            card.setVisibility(View.GONE);
            return;
        }

        card.setVisibility(View.VISIBLE);
        final Pedido pedido = pedidos.get(posicion);
        ArrayList<TextView> textos = new ArrayList<>();
        obtenerTextViews(card, textos);

        if (textos.size() >= 8) {
            textos.get(0).setText("Pedido #" + pedido.getId());
            textos.get(1).setText(pedido.getCliente());
            textos.get(2).setText(pedido.getTelefono() + " | " + pedido.getDireccion());
            textos.get(3).setText(pedido.getFecha());
            textos.get(4).setText(pedido.getEstado().toUpperCase());
            textos.get(5).setText("S/ " + String.format("%.2f", pedido.getTotal()));
        }

        findViewById(verId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDetalle(pedido);
            }
        });

        findViewById(estadoId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarEstado(pedido);
            }
        });

        findViewById(cancelarId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelarPedido(pedido);
            }
        });
    }

    private void limpiarTarjetasExtra() {
        ViewGroup contenedor = (ViewGroup) findViewById(R.id.cardPedidoTres).getParent();
        int i;

        for (i = contenedor.getChildCount() - 1; i >= 0; i--) {
            View hijo = contenedor.getChildAt(i);
            if ("pedido_extra".equals(hijo.getTag())) {
                contenedor.removeViewAt(i);
            }
        }
    }

    private void cargarTarjetasExtra() {
        ViewGroup contenedor = (ViewGroup) findViewById(R.id.cardPedidoTres).getParent();
        int i;

        for (i = 0; i < pedidos.size(); i++) {
            contenedor.addView(crearTarjetaPedido(pedidos.get(i)));
        }
    }

    private View crearTarjetaPedido(final Pedido pedido) {
        LinearLayout card = new LinearLayout(this);
        card.setTag("pedido_extra");
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.fondo_resumen_pedido);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, dp(14), 0, 0);
        card.setLayoutParams(cardParams);

        ArrayList<PedidoDetalle> detalles = pedido.getDetalles() == null
                ? new ArrayList<>()
                : pedido.getDetalles();
        String imagenProducto = detalles.isEmpty() ? null : detalles.get(0).getImagenProducto();

        TextView titulo = crearTexto("Pedido #" + pedido.getId(), 17, "#FFFFFF", true);
        TextView cliente = crearTexto(pedido.getCliente(), 13, "#FFFFFF", true);
        TextView datos = crearTexto(pedido.getTelefono() + " | " + pedido.getDireccion(), 11, "#AFC0D3", false);
        TextView fecha = crearTexto(pedido.getFecha(), 10, "#71869B", false);
        TextView estado = crearTexto(pedido.getEstado().toUpperCase(), 11, obtenerColorEstado(pedido.getEstado()), true);
        TextView total = crearTexto("S/ " + String.format("%.2f", pedido.getTotal()), 19, "#00B7FF", true);

        estado.setGravity(Gravity.CENTER);
        estado.setBackgroundResource(R.drawable.bg_btn_cantidad);
        estado.setPadding(dp(10), dp(5), dp(10), dp(5));
        estado.setMaxLines(2);

        LinearLayout cabecera = new LinearLayout(this);
        cabecera.setOrientation(LinearLayout.HORIZONTAL);
        cabecera.setGravity(Gravity.CENTER_VERTICAL);
        cabecera.addView(crearVistaImagen(imagenProducto), new LinearLayout.LayoutParams(dp(82), dp(76)));

        LinearLayout datosCabecera = new LinearLayout(this);
        datosCabecera.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams datosCabeceraParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        datosCabeceraParams.setMargins(dp(12), 0, 0, 0);
        datosCabecera.addView(titulo);
        datosCabecera.addView(cliente);
        datosCabecera.addView(datos);
        datosCabecera.addView(fecha);
        cabecera.addView(datosCabecera, datosCabeceraParams);
        card.addView(cabecera);

        LinearLayout filaEstado = new LinearLayout(this);
        filaEstado.setGravity(Gravity.CENTER_VERTICAL);
        filaEstado.setOrientation(LinearLayout.HORIZONTAL);
        filaEstado.setPadding(0, dp(10), 0, 0);

        LinearLayout.LayoutParams estadoParams = new LinearLayout.LayoutParams(
                dp(142), ViewGroup.LayoutParams.WRAP_CONTENT);
        estadoParams.setMargins(0, 0, dp(8), 0);
        filaEstado.addView(estado, estadoParams);
        LinearLayout accionesPrecio = new LinearLayout(this);
        accionesPrecio.setOrientation(LinearLayout.VERTICAL);
        accionesPrecio.setGravity(Gravity.END);

        ImageButton eliminar = new ImageButton(this);
        eliminar.setImageResource(R.drawable.ic_delete_red);
        eliminar.setBackgroundColor(Color.TRANSPARENT);
        eliminar.setContentDescription("Eliminar pedido");
        eliminar.setPadding(dp(8), dp(4), dp(8), dp(4));
        accionesPrecio.addView(eliminar, new LinearLayout.LayoutParams(dp(42), dp(42)));

        total.setGravity(Gravity.END);
        accionesPrecio.addView(total, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        filaEstado.addView(accionesPrecio, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        card.addView(filaEstado);

        LinearLayout filaBotones = new LinearLayout(this);
        filaBotones.setOrientation(LinearLayout.HORIZONTAL);
        filaBotones.setPadding(0, dp(12), 0, 0);

        TextView ver = crearBoton("VER DETALLE", false);
        TextView cambiar = crearBoton("CAMBIAR ESTADO", true);
        TextView cancelar = crearBoton("CANCELAR", false);

        filaBotones.addView(ver, crearParametroBoton(0, dp(6)));
        filaBotones.addView(cambiar, crearParametroBoton(dp(6), dp(6)));
        filaBotones.addView(cancelar, crearParametroBoton(dp(6), 0));
        card.addView(filaBotones);

        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDetalle(pedido);
            }
        });

        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarEstado(pedido);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelarPedido(pedido);
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
                .setMessage("¿Deseas eliminar definitivamente el pedido #" + pedido.getId()
                        + "? También desaparecerá del historial del cliente.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) ->
                        pedidoViewModel.eliminarPedidoApi(
                                pedido.getId(),
                                new PedidoViewModel.ResultadoOperacion() {
                                    @Override
                                    public void alCompletar() {
                                        Toast.makeText(PedidosAdminActivity.this,
                                                "Pedido eliminado", Toast.LENGTH_SHORT).show();
                                        cargarPedidos();
                                    }

                                    @Override
                                    public void alFallar(String mensaje) {
                                        Toast.makeText(PedidosAdminActivity.this,
                                                mensaje, Toast.LENGTH_SHORT).show();
                                    }
                                }))
                .show();
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

    private TextView crearTexto(String texto, int tamano, String color, boolean negrita) {
        TextView textView = new TextView(this);
        textView.setText(texto == null ? "" : texto);
        textView.setTextColor(Color.parseColor(color));
        textView.setTextSize(tamano);
        textView.setPadding(0, dp(2), 0, 0);

        if (negrita) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        return textView;
    }

    private TextView crearBoton(String texto, boolean principal) {
        TextView boton = crearTexto(texto, 10, principal ? "#FFFFFF" : "#00B7FF", true);
        boton.setGravity(Gravity.CENTER);
        boton.setClickable(true);
        boton.setFocusable(true);
        boton.setBackgroundResource(principal ? R.drawable.bg_btn_continuar_pedido : R.drawable.bg_card_admin_moderno);
        return boton;
    }

    private LinearLayout.LayoutParams crearParametroBoton(int margenInicio, int margenFin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(44), 1);
        params.setMargins(margenInicio, 0, margenFin, 0);
        return params;
    }

    private String obtenerColorEstado(String estado) {
        if ("Pedido confirmado".equals(estado)) {
            return "#FFB000";
        }

        if ("Entregado".equals(estado)) {
            return "#00E88A";
        }

        if ("Cancelado".equals(estado)) {
            return "#FF4D4D";
        }

        return "#00B7FF";
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    private void obtenerTextViews(View view, ArrayList<TextView> textos) {
        if (view instanceof TextView) {
            textos.add((TextView) view);
            return;
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int i;
            for (i = 0; i < group.getChildCount(); i++) {
                obtenerTextViews(group.getChildAt(i), textos);
            }
        }
    }

    private void mostrarDetalle(Pedido pedido) {
ArrayList<PedidoDetalle> detalles = pedido.getDetalles() == null
                ? new ArrayList<>()
                : pedido.getDetalles();
        String mensaje = "cliente: " + pedido.getCliente() + "\ntotal: S/ " + String.format("%.2f", pedido.getTotal()) + "\n\n";
        int i;

        for (i = 0; i < detalles.size(); i++) {
            PedidoDetalle detalle = detalles.get(i);
            mensaje += detalle.getNombreProducto() + " x" + detalle.getCantidad() + " S/ " + String.format("%.2f", detalle.getSubtotal()) + "\n";
        }

        new AlertDialog.Builder(this).setTitle("detalle pedido").setMessage(mensaje).setPositiveButton("ok", null).show();
    }

    private void cambiarEstado(Pedido pedido) {
        if ("Cancelado".equals(pedido.getEstado())) {
            Toast.makeText(this, "estado final", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] estados = {
                "Pedido confirmado",
                "Preparando pedido",
                "En camino",
                "Entregado"
        };

        int seleccionado = obtenerPosicionEstado(pedido.getEstado());

        new AlertDialog.Builder(this)
                .setTitle("Cambiar estado del pedido")
                .setSingleChoiceItems(estados, seleccionado, null)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    AlertDialog ventana = (AlertDialog) dialog;
                    int posicion = ventana.getListView().getCheckedItemPosition();

                    if (posicion < 0) {
                        Toast.makeText(this, "selecciona un estado", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pedidoViewModel.cambiarEstadoApi(
                            pedido.getId(),
                            estados[posicion],
                            new PedidoViewModel.ResultadoOperacion() {
                                @Override
                                public void alCompletar() {
                                    Toast.makeText(PedidosAdminActivity.this,
                                            "estado actualizado", Toast.LENGTH_SHORT).show();
                                    cargarPedidos();
                                }

                                @Override
                                public void alFallar(String mensaje) {
                                    Toast.makeText(PedidosAdminActivity.this,
                                            mensaje, Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    private int obtenerPosicionEstado(String estado) {
        if ("Preparando pedido".equals(estado)) {
            return 1;
        }

        if ("En camino".equals(estado)) {
            return 2;
        }

        if ("Entregado".equals(estado)) {
            return 3;
        }

        return 0;
    }

    private void cancelarPedido(Pedido pedido) {
        pedidoViewModel.cancelarPedidoApi(pedido.getId(), new PedidoViewModel.ResultadoOperacion() {
            @Override
            public void alCompletar() {
                new ProductoViewModel(PedidosAdminActivity.this).sincronizarProductos();
                Toast.makeText(PedidosAdminActivity.this,
                        "pedido cancelado y stock restaurado", Toast.LENGTH_SHORT).show();
                cargarPedidos();
            }

            @Override
            public void alFallar(String mensaje) {
                Toast.makeText(PedidosAdminActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
