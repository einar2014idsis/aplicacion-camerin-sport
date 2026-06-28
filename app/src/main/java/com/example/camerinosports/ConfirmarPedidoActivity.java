package com.example.camerinosports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.CarritoItem;
import com.example.camerinosports.model.CarritoMemoria;
import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.PedidoDetalle;
import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.util.Validaciones;
import com.example.camerinosports.viewmodel.PedidoViewModel;
import com.example.camerinosports.viewmodel.UsuarioViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class    ConfirmarPedidoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText edtCliente;
    private EditText edtTelefono;
    private EditText edtDireccion;
    private EditText edtReferencia;
    private TextView txtEstadoUbicacion;
    private TextView txtPreviewUbicacion;
    private TextView txtAvisoPreviewMapa;
    private TextView txtTotal;
    private LinearLayout contenedorResumen;
    private MapView mapPreview;
    private GoogleMap googleMapPreview;
    private PedidoViewModel pedidoViewModel;
    private UsuarioViewModel usuarioViewModel;
    private double latitud;
    private double longitud;
    private boolean ubicacionSeleccionada;
    private ActivityResultLauncher<Intent> mapaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_pedido);

        pedidoViewModel = new PedidoViewModel(this);
        usuarioViewModel = new UsuarioViewModel(this);
        prepararResultadoMapa();
        enlazarVistas();
        prepararMapaPreview(savedInstanceState);
        configurarEventos();
        cargarDatosIniciales();
    }

    private void prepararResultadoMapa() {
        mapaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            latitud = data.getDoubleExtra("latitud", 0);
                            longitud = data.getDoubleExtra("longitud", 0);
                            ubicacionSeleccionada = true;
                            actualizarVistaUbicacion();
                        }
                    }
                }
        );
    }

    private void enlazarVistas() {
        edtCliente = findViewById(R.id.edtNombrePedido);
        edtTelefono = findViewById(R.id.edtTelefonoPedido);
        edtDireccion = findViewById(R.id.edtDireccionPedido);
        edtReferencia = findViewById(R.id.edtReferenciaPedido);
        txtEstadoUbicacion = findViewById(R.id.txtEstadoUbicacionPedido);
        txtPreviewUbicacion = findViewById(R.id.txtPreviewUbicacionPedido);
        txtAvisoPreviewMapa = findViewById(R.id.txtAvisoPreviewMapaPedido);
        txtTotal = findViewById(R.id.txtTotalConfirmarPedido);
        contenedorResumen = findViewById(R.id.contenedorResumenProductosPedido);
        mapPreview = findViewById(R.id.mapPreviewUbicacionPedido);
    }

    private void prepararMapaPreview(Bundle savedInstanceState) {
        mapPreview.onCreate(savedInstanceState);
        mapPreview.getMapAsync(this);
        mostrarAvisoSiFaltaApiKey();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapPreview = googleMap;
        googleMapPreview.getUiSettings().setAllGesturesEnabled(false);
        googleMapPreview.getUiSettings().setMapToolbarEnabled(false);
        googleMapPreview.getUiSettings().setZoomControlsEnabled(false);

        LatLng peru = new LatLng(-9.189967, -75.015152);
        googleMapPreview.moveCamera(CameraUpdateFactory.newLatLngZoom(peru, 5));
        actualizarMapaPreview();
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverConfirmarPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnSeleccionarUbicacionPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirMapa();
            }
        });

        findViewById(R.id.previewUbicacionPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirMapa();
            }
        });

        findViewById(R.id.btnRegistrarPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continuarAPago();
            }
        });
    }

    private void abrirMapa() {
        mapaLauncher.launch(new Intent(ConfirmarPedidoActivity.this, MapaEntregaActivity.class));
    }

    private void actualizarVistaUbicacion() {
        String coordenadas = "Latitud: " + String.format("%.6f", latitud) + "\nLongitud: " + String.format("%.6f", longitud);
        txtEstadoUbicacion.setText("Ubicacion seleccionada\n" + coordenadas);
        txtPreviewUbicacion.setText("Punto de entrega seleccionado\n" + coordenadas);
        actualizarMapaPreview();
    }

    private void actualizarMapaPreview() {
        if (googleMapPreview == null || !ubicacionSeleccionada) {
            return;
        }

        LatLng puntoEntrega = new LatLng(latitud, longitud);
        googleMapPreview.clear();
        googleMapPreview.addMarker(new MarkerOptions().position(puntoEntrega).title("Entrega"));
        googleMapPreview.moveCamera(CameraUpdateFactory.newLatLngZoom(puntoEntrega, 16));
    }

    private void mostrarAvisoSiFaltaApiKey() {
        String apiKey = getString(R.string.google_maps_key);
        boolean faltaClave = apiKey == null || apiKey.trim().isEmpty() || apiKey.contains("COLOCA_AQUI");
        txtAvisoPreviewMapa.setVisibility(faltaClave ? View.VISIBLE : View.GONE);
    }

    private void cargarDatosIniciales() {
        usuarioViewModel.obtenerUsuarioApi(
                UsuarioSesion.getCorreoActual(),
                new UsuarioViewModel.ResultadoUsuario() {
                    @Override
                    public void alCompletar(Usuario usuario) {
                        edtCliente.setText(usuario.getNombre() + " " + usuario.getApellidos());
                    }

                    @Override
                    public void alFallar(String mensaje) {
                        Toast.makeText(ConfirmarPedidoActivity.this,
                                mensaje, Toast.LENGTH_SHORT).show();
                    }
                });

        cargarResumenCarrito();
    }

    private void cargarResumenCarrito() {
        contenedorResumen.removeAllViews();
        ArrayList<CarritoItem> items = CarritoMemoria.obtenerItems();
        int i;

        if (items.isEmpty()) {
            contenedorResumen.addView(crearTextoResumen("Carrito vacio"));
        }

        for (i = 0; i < items.size(); i++) {
            CarritoItem item = items.get(i);
            contenedorResumen.addView(crearTextoResumen(item.getProducto().getNombre() +
                    " x" + item.getCantidad() + " - S/ " + String.format("%.2f", item.getSubtotal())));
        }

        txtTotal.setText("Total: S/ " + String.format("%.2f", CarritoMemoria.calcularTotal()));
    }

    private TextView crearTextoResumen(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFC8D3E0);
        textView.setTextSize(12);
        textView.setPadding(0, 5, 0, 5);
        return textView;
    }

    private void continuarAPago() {
        if (CarritoMemoria.obtenerItems().isEmpty()) {
            Toast.makeText(this, "el carrito esta vacio", Toast.LENGTH_SHORT).show();
            return;
        }

        String cliente = edtCliente.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String direccion = edtDireccion.getText().toString().trim();

        if (!Validaciones.nombrePersona(cliente)) {
            edtCliente.setError("Usa solo letras");
            edtCliente.requestFocus();
            return;
        }

        if (!Validaciones.telefonoPeru(telefono)) {
            edtTelefono.setError("Ingresa 9 dígitos y empieza en 9");
            edtTelefono.requestFocus();
            return;
        }

        if (direccion.length() < 5 || direccion.length() > 200) {
            edtDireccion.setError("Ingresa una dirección válida");
            edtDireccion.requestFocus();
            return;
        }

        if (!ubicacionSeleccionada) {
            Toast.makeText(this, "selecciona la ubicacion de entrega", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!stockDisponible()) {
            Toast.makeText(this, "hay productos sin stock suficiente", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PagoPedidoActivity.class);
        intent.putExtra(PagoPedidoActivity.EXTRA_CLIENTE, cliente);
        intent.putExtra(PagoPedidoActivity.EXTRA_TELEFONO, telefono);
        intent.putExtra(PagoPedidoActivity.EXTRA_DIRECCION, direccion);
        intent.putExtra(PagoPedidoActivity.EXTRA_REFERENCIA, edtReferencia.getText().toString().trim());
        intent.putExtra(PagoPedidoActivity.EXTRA_LATITUD, latitud);
        intent.putExtra(PagoPedidoActivity.EXTRA_LONGITUD, longitud);
        startActivity(intent);
    }

    private ArrayList<PedidoDetalle> crearDetalles() {
        ArrayList<PedidoDetalle> detalles = new ArrayList<>();
        ArrayList<CarritoItem> items = CarritoMemoria.obtenerItems();
        int i;

        for (i = 0; i < items.size(); i++) {
            CarritoItem item = items.get(i);
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setProductoId(item.getProducto().getId());
            detalle.setNombreProducto(item.getProducto().getNombre());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(item.getProducto().getPrecio());
            detalle.setSubtotal(item.getSubtotal());
            detalles.add(detalle);
        }

        return detalles;
    }

    private boolean textoVacio(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private boolean stockDisponible() {
        ArrayList<CarritoItem> items = CarritoMemoria.obtenerItems();
        int i;

        for (i = 0; i < items.size(); i++) {
            CarritoItem item = items.get(i);
            if (item.getProducto().getStock() < item.getCantidad()) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapPreview.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapPreview.onResume();
    }

    @Override
    protected void onPause() {
        mapPreview.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mapPreview.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapPreview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapPreview.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapPreview.onSaveInstanceState(outState);
    }
}
