package com.example.camerinosports;

import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.viewmodel.PedidoViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class SeguimientoPedidoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private PedidoViewModel pedidoViewModel;
    private Pedido pedido;
    private MapView mapaSeguimiento;
    private GoogleMap googleMap;
    private TextView txtRutaSeguimiento;
    private TextView txtAvisoMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento_pedido);

        pedidoViewModel = new PedidoViewModel(this);
        mapaSeguimiento = findViewById(R.id.mapaSeguimientoPedido);
        txtRutaSeguimiento = findViewById(R.id.txtRutaSeguimiento);
        txtAvisoMapa = findViewById(R.id.txtAvisoMapaSeguimiento);
        mapaSeguimiento.onCreate(savedInstanceState);
        mapaSeguimiento.getMapAsync(this);
        configurarEventos();
        cargarPedido();
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverSeguimiento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void cargarPedido() {
        int pedidoId = getIntent().getIntExtra("pedidoId", -1);

        if (pedidoId == -1) {
            Toast.makeText(this, "pedido no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pedidoViewModel.obtenerPedidoApi(pedidoId, new PedidoViewModel.ResultadoPedido() {
            @Override
            public void alCompletar(Pedido pedidoServidor) {
                pedido = pedidoServidor;
                mostrarPedido();
            }

            @Override
            public void alFallar(String mensaje) {
                Toast.makeText(SeguimientoPedidoActivity.this,
                        mensaje, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void mostrarPedido() {
        ponerTexto(R.id.txtNumeroPedidoSeguimiento, "Pedido #CS-" + String.format("%05d", pedido.getId()));
        ponerTexto(R.id.txtEstadoPrincipalSeguimiento, pedido.getEstado());
        ponerTexto(R.id.txtFechaPedidoSeguimiento, pedido.getFecha());
        ponerTexto(R.id.txtDireccionSeguimiento, pedido.getDireccion());
        ponerTexto(R.id.txtPagoSeguimiento, obtenerTextoPago());
        marcarEstadoActual();
        pintarMapaSeguimiento();
    }

    private String obtenerTextoPago() {
        String metodo = pedido.getMetodoPago();
        String estadoPago = pedido.getEstadoPago();

        if (metodo == null || metodo.trim().isEmpty()) {
            return "Metodo no registrado";
        }

        if (estadoPago == null || estadoPago.trim().isEmpty()) {
            estadoPago = "Pendiente";
        }

        return metodo + " - " + estadoPago;
    }

    private void marcarEstadoActual() {
        String estado = pedido.getEstado();
        // cada estado se convierte en un numero para activar los pasos anteriores
        int pasoActual = obtenerPasoActual(estado);

        ponerTexto(R.id.txtDetalleConfirmado, "Tu pedido ha sido recibido correctamente.");
        ponerTexto(R.id.txtDetallePreparando, "Aun no hemos empezado la preparacion.");
        ponerTexto(R.id.txtDetalleCamino, "Aun no esta en camino.");
        ponerTexto(R.id.txtDetalleEntregado, "Tu pedido aun no ha sido entregado.");
        pintarPasoTexto(R.id.txtEstadoConfirmado, pasoActual >= 1, pasoActual == 1);
        pintarPasoTexto(R.id.txtEstadoPreparando, pasoActual >= 2, pasoActual == 2);
        pintarPasoTexto(R.id.txtEstadoCamino, pasoActual >= 3, pasoActual == 3);
        pintarPasoTexto(R.id.txtEstadoEntregado, pasoActual >= 4, pasoActual == 4);
        pintarIconoTexto(R.id.txtIconoConfirmado, pasoActual >= 1);
        pintarIconoTexto(R.id.txtIconoPreparando, pasoActual >= 2);
        pintarIconoImagen(R.id.imgIconoCamino, pasoActual >= 3, true);
        pintarIconoImagen(R.id.imgIconoEntregado, pasoActual >= 4, false);
        pintarLinea(R.id.lineaConfirmado, pasoActual >= 2);
        pintarLinea(R.id.lineaPreparando, pasoActual >= 3);
        pintarLinea(R.id.lineaCamino, pasoActual >= 4);

        if ("Pedido confirmado".equals(estado)) {
            ponerTexto(R.id.txtEstadoPrincipalSeguimiento, "Pedido confirmado");
        } else if ("Preparando pedido".equals(estado)) {
            ponerTexto(R.id.txtEstadoPrincipalSeguimiento, "Preparando pedido");
            ponerTexto(R.id.txtDetallePreparando, "Estamos preparando tu pedido.");
            ponerTexto(R.id.txtDetalleCamino, "Aun no esta en camino.");
        } else if ("En camino".equals(estado)) {
            ponerTexto(R.id.txtEstadoPrincipalSeguimiento, "En camino");
            ponerTexto(R.id.txtDetallePreparando, "Tu pedido ya fue preparado.");
            ponerTexto(R.id.txtDetalleCamino, "Tu pedido esta en camino.");
            ponerTexto(R.id.txtDetalleEntregado, "Tu pedido aun no ha sido entregado.");
        } else if ("Entregado".equals(estado)) {
            ponerTexto(R.id.txtEstadoPrincipalSeguimiento, "Entregado");
            ponerTexto(R.id.txtDetallePreparando, "Tu pedido ya fue preparado.");
            ponerTexto(R.id.txtDetalleCamino, "El repartidor llego al punto de entrega.");
            ponerTexto(R.id.txtDetalleEntregado, "Tu pedido ya fue entregado.");
        } else if ("Cancelado".equals(estado)) {
            ponerTexto(R.id.txtEstadoPrincipalSeguimiento, "Cancelado");
            ponerTexto(R.id.txtDetallePreparando, "El pedido fue cancelado.");
            ponerTexto(R.id.txtDetalleCamino, "El pedido no saldra a reparto.");
            ponerTexto(R.id.txtDetalleEntregado, "El pedido fue cancelado.");
        }
    }

    private int obtenerPasoActual(String estado) {
        if ("Preparando pedido".equals(estado)) {
            return 2;
        }
        if ("En camino".equals(estado)) {
            return 3;
        }
        if ("Entregado".equals(estado)) {
            return 4;
        }
        if ("Cancelado".equals(estado)) {
            return 1;
        }
        return 1;
    }

    private void pintarPasoTexto(int id, boolean activo, boolean destacado) {
        TextView textView = findViewById(id);
        if (activo && destacado) {
            textView.setTextColor(id == R.id.txtEstadoEntregado
                    ? Color.rgb(0, 232, 138)
                    : Color.rgb(0, 175, 255));
        } else if (activo) {
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setTextColor(Color.rgb(159, 176, 194));
        }
    }

    private void pintarIconoTexto(int id, boolean activo) {
        TextView textView = findViewById(id);
        textView.setText(activo ? "✓" : "");
        textView.setAlpha(activo ? 1f : 0.65f);
        textView.setBackgroundResource(activo ? R.drawable.bg_btn_continuar_pedido : R.drawable.bg_estado_pendiente_redondo);
    }

    private void pintarIconoImagen(int id, boolean activo, boolean camion) {
        ImageView imageView = findViewById(id);
        imageView.setAlpha(activo ? 1f : 0.55f);
        imageView.setBackgroundResource(activo
                ? (camion ? R.drawable.bg_estado_camion_redondo : R.drawable.bg_btn_continuar_pedido)
                : R.drawable.bg_estado_pendiente_redondo);
    }

    private void pintarLinea(int id, boolean activa) {
        View view = findViewById(id);
        view.setBackgroundColor(activa ? Color.rgb(0, 175, 255) : Color.rgb(52, 72, 95));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        pintarMapaSeguimiento();
    }

    private void pintarMapaSeguimiento() {
        if (googleMap == null || pedido == null) {
            return;
        }

        googleMap.clear();

        if (pedido.getLatitud() == 0 && pedido.getLongitud() == 0) {
            txtAvisoMapa.setVisibility(View.VISIBLE);
            txtAvisoMapa.setText("Este pedido no tiene ubicacion guardada");
            txtRutaSeguimiento.setText("Selecciona una ubicacion al confirmar el pedido");
            return;
        }

        txtAvisoMapa.setVisibility(View.GONE);
        LatLng destino = new LatLng(pedido.getLatitud(), pedido.getLongitud());
        // la ubicacion del repartidor es una representacion visual del recorrido
        LatLng repartidor = calcularUbicacionRepartidor(destino);
        int pasoActual = obtenerPasoActual(pedido.getEstado());

        googleMap.addMarker(new MarkerOptions()
                .position(destino)
                .title("Punto de entrega")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        if (pasoActual >= 3) {
            // la ruta solo aparece cuando el pedido ya salio a reparto
            googleMap.addMarker(new MarkerOptions()
                    .position(repartidor)
                    .title("Repartidor")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            if (repartidor.equals(destino)) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 17));
                txtRutaSeguimiento.setText("Ruta finalizada: pedido entregado en tu direccion");
            } else {
                googleMap.addPolyline(new PolylineOptions()
                        .add(repartidor, destino)
                        .color(Color.rgb(0, 175, 255))
                        .width(8f));
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(repartidor)
                        .include(destino)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                txtRutaSeguimiento.setText("Ruta activa: repartidor hacia tu direccion de entrega");
            }
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 16));
            txtRutaSeguimiento.setText("Ruta disponible cuando el pedido salga a reparto");
        }
    }

    private LatLng calcularUbicacionRepartidor(LatLng destino) {
        // al entregar el pedido ambos marcadores terminan en el mismo punto
        if ("Entregado".equals(pedido.getEstado())) {
            return destino;
        }

        return new LatLng(destino.latitude + 0.006, destino.longitude - 0.006);
    }

    private void ponerTexto(int id, String texto) {
        TextView textView = findViewById(id);
        textView.setText(texto == null ? "" : texto);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapaSeguimiento.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapaSeguimiento.onResume();
    }

    @Override
    protected void onPause() {
        mapaSeguimiento.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mapaSeguimiento.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapaSeguimiento.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapaSeguimiento.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapaSeguimiento.onSaveInstanceState(outState);
    }
}
