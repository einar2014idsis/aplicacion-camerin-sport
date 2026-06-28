package com.example.camerinosports;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.CarritoItem;
import com.example.camerinosports.model.CarritoMemoria;
import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.PedidoDetalle;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.viewmodel.PedidoViewModel;
import com.example.camerinosports.viewmodel.ProductoViewModel;
import com.example.camerinosports.util.Validaciones;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PagoPedidoActivity extends AppCompatActivity {

    public static final String EXTRA_CLIENTE = "cliente";
    public static final String EXTRA_TELEFONO = "telefono";
    public static final String EXTRA_DIRECCION = "direccion";
    public static final String EXTRA_REFERENCIA = "referencia";
    public static final String EXTRA_LATITUD = "latitud";
    public static final String EXTRA_LONGITUD = "longitud";

    private PedidoViewModel pedidoViewModel;
    private LinearLayout formYape;
    private LinearLayout formTarjeta;
    private EditText edtYapeTelefono;
    private EditText edtYapeCodigo;
    private EditText edtNumeroTarjeta;
    private EditText edtFechaTarjeta;
    private EditText edtCvvTarjeta;
    private EditText edtTitularTarjeta;
    private TextView txtCheckYape;
    private TextView txtCheckTarjeta;
    private String metodoPago = "";
    private boolean formateandoTarjeta;
    private boolean formateandoFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_pago_pedido);

        pedidoViewModel = new PedidoViewModel(this);
        enlazarVistas();
        protegerCamposSensibles();
        configurarEventos();
        configurarFormatoTarjeta();
        cargarResumen();
        seleccionarMetodo("Yape");
    }

    private void enlazarVistas() {
        formYape = findViewById(R.id.formYape);
        formTarjeta = findViewById(R.id.formTarjeta);
        edtYapeTelefono = findViewById(R.id.edtYapeTelefono);
        edtYapeCodigo = findViewById(R.id.edtYapeCodigo);
        edtNumeroTarjeta = findViewById(R.id.edtNumeroTarjeta);
        edtFechaTarjeta = findViewById(R.id.edtFechaTarjeta);
        edtCvvTarjeta = findViewById(R.id.edtCvvTarjeta);
        edtTitularTarjeta = findViewById(R.id.edtTitularTarjeta);
        txtCheckYape = findViewById(R.id.txtCheckYape);
        txtCheckTarjeta = findViewById(R.id.txtCheckTarjeta);
    }

    private void configurarEventos() {
        findViewById(R.id.btnVolverPagoPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.opcionPagoYape).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarMetodo("Yape");
            }
        });

        findViewById(R.id.opcionPagoTarjeta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarMetodo("Tarjeta");
            }
        });

        findViewById(R.id.btnConfirmarPagoPedido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarPagoYRegistrarPedido();
            }
        });
    }

    private void configurarFormatoTarjeta() {
        edtNumeroTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (formateandoTarjeta) {
                    return;
                }

                String digitos = editable.toString().replaceAll("\\D", "");
                if (digitos.length() > 19) {
                    digitos = digitos.substring(0, 19);
                }

                StringBuilder formato = new StringBuilder();
                for (int i = 0; i < digitos.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formato.append(' ');
                    }
                    formato.append(digitos.charAt(i));
                }

                formateandoTarjeta = true;
                edtNumeroTarjeta.setText(formato.toString());
                edtNumeroTarjeta.setSelection(formato.length());
                formateandoTarjeta = false;
            }
        });

        edtFechaTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (formateandoFecha) {
                    return;
                }

                String digitos = editable.toString().replaceAll("\\D", "");
                if (digitos.length() > 4) {
                    digitos = digitos.substring(0, 4);
                }

                String formato = digitos.length() > 2
                        ? digitos.substring(0, 2) + "/" + digitos.substring(2)
                        : digitos;

                formateandoFecha = true;
                edtFechaTarjeta.setText(formato);
                edtFechaTarjeta.setSelection(formato.length());
                formateandoFecha = false;
            }
        });
    }

    private void cargarResumen() {
        TextView total = findViewById(R.id.txtTotalPagoPedido);
        total.setText("Total: S/ " + String.format("%.2f", CarritoMemoria.calcularTotal()));
    }

    private void protegerCamposSensibles() {
        EditText[] camposSensibles = {
                edtYapeTelefono,
                edtYapeCodigo,
                edtNumeroTarjeta,
                edtFechaTarjeta,
                edtCvvTarjeta,
                edtTitularTarjeta
        };

        for (EditText campo : camposSensibles) {
            campo.setSaveEnabled(false);
            campo.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }
    }

    private void seleccionarMetodo(String metodo) {
        metodoPago = metodo;

        boolean esYape = "Yape".equals(metodoPago);
        formYape.setVisibility(esYape ? View.VISIBLE : View.GONE);
        formTarjeta.setVisibility(esYape ? View.GONE : View.VISIBLE);

        txtCheckYape.setText(esYape ? "OK" : "");
        txtCheckTarjeta.setText(esYape ? "" : "OK");
        txtCheckYape.setBackgroundResource(esYape ? R.drawable.bg_btn_continuar_pedido : R.drawable.bg_estado_pendiente_redondo);
        txtCheckTarjeta.setBackgroundResource(esYape ? R.drawable.bg_estado_pendiente_redondo : R.drawable.bg_btn_continuar_pedido);
    }

    private void confirmarPagoYRegistrarPedido() {
        ocultarTeclado();

        // no se permite continuar sin productos ni metodo de pago
        if (CarritoMemoria.obtenerItems().isEmpty()) {
            Toast.makeText(this, "el carrito esta vacio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (metodoPago.isEmpty()) {
            Toast.makeText(this, "selecciona un metodo de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarMetodoPago()) {
            return;
        }

        validarProductosYRegistrarPedido();
    }

    private void validarProductosYRegistrarPedido() {
        // se consulta el catalogo otra vez para evitar comprar productos eliminados
        findViewById(R.id.btnConfirmarPagoPedido).setEnabled(false);
        new ProductoViewModel(this).sincronizarProductos(new ProductoViewModel.ResultadoSincronizacion() {
            @Override
            public void alCompletar() {
                ArrayList<CarritoItem> items = new ArrayList<>(CarritoMemoria.obtenerItems());
                ProductoViewModel productos = new ProductoViewModel(PagoPedidoActivity.this);

                for (CarritoItem item : items) {
                    int cantidadSolicitada = item.getCantidad();
                    com.example.camerinosports.model.Producto productoActual =
                            productos.obtenerProductoPorId(item.getProducto().getId());

                    if (productoActual == null) {
                        CarritoMemoria.eliminarProducto(item.getProducto().getId());
                        findViewById(R.id.btnConfirmarPagoPedido).setEnabled(true);
                        Toast.makeText(
                                PagoPedidoActivity.this,
                                item.getProducto().getNombre() +
                                        " ya no está disponible. Se retiró del carrito",
                                Toast.LENGTH_LONG
                        ).show();
                        Intent carrito = new Intent(PagoPedidoActivity.this, CarritoActivity.class);
                        carrito.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(carrito);
                        finish();
                        return;
                    }

                    if (productoActual.getStock() < cantidadSolicitada) {
                        CarritoMemoria.actualizarProducto(productoActual);
                        findViewById(R.id.btnConfirmarPagoPedido).setEnabled(true);
                        Toast.makeText(
                                PagoPedidoActivity.this,
                                "Stock actualizado para " + productoActual.getNombre(),
                                Toast.LENGTH_LONG
                        ).show();
                        Intent carrito = new Intent(PagoPedidoActivity.this, CarritoActivity.class);
                        carrito.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(carrito);
                        finish();
                        return;
                    }

                    CarritoMemoria.actualizarProducto(productoActual);
                }

                registrarPedidoSimulado();
            }

            @Override
            public void alFallar() {
                findViewById(R.id.btnConfirmarPagoPedido).setEnabled(true);
                Toast.makeText(
                        PagoPedidoActivity.this,
                        "No se pudo validar el catálogo con la API",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void registrarPedidoSimulado() {
        Pedido pedido = crearPedido();

        // el carrito solo se vacia cuando el servidor confirma que guardo el pedido
        pedidoViewModel.registrarPedidoApi(pedido, new PedidoViewModel.ResultadoPedido() {
            @Override
            public void alCompletar(Pedido pedidoRegistrado) {
                findViewById(R.id.btnConfirmarPagoPedido).setEnabled(true);
                new ProductoViewModel(PagoPedidoActivity.this).sincronizarProductos();
                CarritoMemoria.vaciar();
                limpiarDatosSensibles();
                Toast.makeText(PagoPedidoActivity.this,
                        "Pago simulado aprobado", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PagoPedidoActivity.this, SeguimientoPedidoActivity.class);
                intent.putExtra("pedidoId", pedidoRegistrado.getId());
                startActivity(intent);
                finish();
            }

            @Override
            public void alFallar(String mensaje) {
                findViewById(R.id.btnConfirmarPagoPedido).setEnabled(true);
                Toast.makeText(PagoPedidoActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarMetodoPago() {
        // cada metodo de pago tiene campos y reglas diferentes
        if ("Yape".equals(metodoPago)) {
            if (!Validaciones.telefonoPeru(texto(edtYapeTelefono))) {
                edtYapeTelefono.setError("Ingresa 9 digitos y empieza en 9");
                edtYapeTelefono.requestFocus();
                return false;
            }

            if (!texto(edtYapeCodigo).matches("\\d{6}")) {
                edtYapeCodigo.setError("Ingresa 6 digitos para simular la confirmacion");
                edtYapeCodigo.requestFocus();
                return false;
            }

            return true;
        }

        if (!validarTarjetaSimulada()) {
            Toast.makeText(this, "ingresa numero, fecha MM/AA, CVV y titular", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validarTarjetaSimulada() {
        String numero = texto(edtNumeroTarjeta);
        String fecha = texto(edtFechaTarjeta);
        String cvv = texto(edtCvvTarjeta);
        String titular = texto(edtTitularTarjeta);

        if (!Validaciones.numeroTarjeta(numero)) {
            edtNumeroTarjeta.setError("Número de tarjeta inválido");
            edtNumeroTarjeta.requestFocus();
            return false;
        }

        if (!Validaciones.fechaTarjeta(fecha)) {
            edtFechaTarjeta.setError("Usa MM/AA y una fecha vigente");
            edtFechaTarjeta.requestFocus();
            return false;
        }

        if (!cvv.matches("\\d{3}")) {
            edtCvvTarjeta.setError("CVV de 3 dígitos");
            edtCvvTarjeta.requestFocus();
            return false;
        }

        if (!Validaciones.nombrePersona(titular)) {
            edtTitularTarjeta.setError("Usa el nombre del titular");
            edtTitularTarjeta.requestFocus();
            return false;
        }

        return true;
    }

    private Pedido crearPedido() {
        // se reunen los datos de entrega, pago y carrito en un solo objeto
        Intent datos = getIntent();
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(0);
        pedido.setCliente(datos.getStringExtra(EXTRA_CLIENTE));
        pedido.setTelefono(datos.getStringExtra(EXTRA_TELEFONO));
        pedido.setCorreo(UsuarioSesion.getCorreoActual());
        pedido.setDireccion(datos.getStringExtra(EXTRA_DIRECCION));
        pedido.setReferencia(datos.getStringExtra(EXTRA_REFERENCIA));
        pedido.setComentario("");
        pedido.setTotal(CarritoMemoria.calcularTotal());
        pedido.setEstado("Pedido confirmado");
        pedido.setLatitud(datos.getDoubleExtra(EXTRA_LATITUD, 0));
        pedido.setLongitud(datos.getDoubleExtra(EXTRA_LONGITUD, 0));
        pedido.setMetodoPago(metodoPago);
        pedido.setEstadoPago("Pagado");
        pedido.setCodigoOperacionPago(crearCodigoOperacion());
        pedido.setDetalles(crearDetalles());
        return pedido;
    }

    private ArrayList<PedidoDetalle> crearDetalles() {
        // cada producto del carrito se convierte en una linea del pedido
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

    private String crearCodigoOperacion() {
        String fecha = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        return metodoPago.toUpperCase(Locale.ROOT) + "-" + fecha;
    }

    private String texto(EditText editText) {
        return editText.getText().toString().trim();
    }

    private void limpiarDatosSensibles() {
        edtYapeTelefono.setText("");
        edtYapeCodigo.setText("");
        edtNumeroTarjeta.setText("");
        edtFechaTarjeta.setText("");
        edtCvvTarjeta.setText("");
        edtTitularTarjeta.setText("");
    }

    @Override
    protected void onDestroy() {
        limpiarDatosSensibles();
        super.onDestroy();
    }

    private void ocultarTeclado() {
        View vista = getCurrentFocus();
        if (vista == null) {
            vista = findViewById(android.R.id.content);
        }

        InputMethodManager teclado =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (teclado != null) {
            teclado.hideSoftInputFromWindow(vista.getWindowToken(), 0);
        }
    }
}
