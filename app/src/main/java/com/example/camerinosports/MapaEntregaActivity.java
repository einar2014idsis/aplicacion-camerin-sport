package com.example.camerinosports;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapaEntregaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 200;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marcador;
    private LatLng ubicacionSeleccionada;
    private TextView txtUbicacionMapa;
    private TextView txtAvisoMapa;
    private CancellationTokenSource tokenUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_entrega);

        txtUbicacionMapa = findViewById(R.id.txtUbicacionMapa);
        txtAvisoMapa = findViewById(R.id.txtAvisoMapa);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        revisarApiKeyMaps();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaEntrega);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        configurarBotones();
    }

    private void revisarApiKeyMaps() {
        String apiKey = getString(R.string.google_maps_key);
        boolean faltaClave = apiKey == null ||
                apiKey.trim().isEmpty() ||
                apiKey.contains("COLOCA_AQUI");

        if (faltaClave) {
            txtAvisoMapa.setVisibility(View.VISIBLE);
            Toast.makeText(this, "falta configurar la API key de Google Maps", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                seleccionarUbicacion(latLng);
            }
        });

        solicitarPermisosSiHaceFalta();
    }

    private void configurarBotones() {
        findViewById(R.id.btnUsarMiUbicacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ubicacionActiva()) {
                    Toast.makeText(MapaEntregaActivity.this,
                            "Activa la ubicacion del telefono o toca el mapa para elegir",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    return;
                }

                obtenerUbicacionActual();
            }
        });

        findViewById(R.id.btnConfirmarUbicacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarUbicacion();
            }
        });
    }

    private void solicitarPermisosSiHaceFalta() {
        if (tienePermisoUbicacion()) {
            activarMiUbicacion();
            obtenerUbicacionActual();
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION
        );
    }

    private boolean tienePermisoUbicacion() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void activarMiUbicacion() {
        if (googleMap == null || !tienePermisoUbicacion()) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
    }

    private void obtenerUbicacionActual() {
        if (!tienePermisoUbicacion()) {
            solicitarPermisosSiHaceFalta();
            return;
        }

        activarMiUbicacion();
        txtUbicacionMapa.setText("Buscando tu ubicacion actual...");

        if (!ubicacionActiva()) {
            txtUbicacionMapa.setText("GPS desactivado. Activa la ubicacion o toca el mapa.");
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    seleccionarUbicacion(new LatLng(location.getLatitude(), location.getLongitude()));
                    return;
                }

                pedirUbicacionPrecisa();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pedirUbicacionPrecisa();
            }
        });
    }

    private void pedirUbicacionPrecisa() {
        if (!tienePermisoUbicacion()) {
            return;
        }

        tokenUbicacion = new CancellationTokenSource();
        fusedLocationProviderClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenUbicacion.getToken())
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Toast.makeText(MapaEntregaActivity.this, "no se pudo obtener ubicacion, toca el mapa", Toast.LENGTH_LONG).show();
                            txtUbicacionMapa.setText("No se pudo obtener ubicacion. Toca el mapa para elegir.");
                            return;
                        }

                        seleccionarUbicacion(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapaEntregaActivity.this, "no se pudo obtener ubicacion, toca el mapa", Toast.LENGTH_LONG).show();
                        txtUbicacionMapa.setText("No se pudo obtener ubicacion. Toca el mapa para elegir.");
                    }
                });
    }

    private boolean ubicacionActiva() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager == null) {
            return false;
        }

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void seleccionarUbicacion(LatLng latLng) {
        if (googleMap == null) {
            return;
        }

        ubicacionSeleccionada = latLng;

        if (marcador != null) {
            marcador.remove();
        }

        marcador = googleMap.addMarker(new MarkerOptions().position(latLng).title("entrega"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        txtUbicacionMapa.setText("latitud: " + latLng.latitude + "\nlongitud: " + latLng.longitude);
    }

    private void confirmarUbicacion() {
        if (ubicacionSeleccionada == null) {
            Toast.makeText(this, "selecciona una ubicación", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra("latitud", ubicacionSeleccionada.latitude);
        data.putExtra("longitud", ubicacionSeleccionada.longitude);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (tokenUbicacion != null) {
            tokenUbicacion.cancel();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (googleMap != null && tienePermisoUbicacion() && ubicacionActiva() && ubicacionSeleccionada == null) {
            obtenerUbicacionActual();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (tienePermisoUbicacion()) {
                activarMiUbicacion();
                obtenerUbicacionActual();
            } else {
                Toast.makeText(this, "permiso rechazado, toca el mapa para elegir ubicación", Toast.LENGTH_LONG).show();
            }
        }
    }
}
