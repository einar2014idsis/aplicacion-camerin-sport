package com.example.camerinosports;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.model.Promocion;
import com.example.camerinosports.viewmodel.ProductoViewModel;
import com.example.camerinosports.viewmodel.PromocionViewModel;

import java.util.ArrayList;

public class PromocionesActivity extends AppCompatActivity {

    private PromocionViewModel promocionViewModel;
    private ProductoViewModel productoViewModel;
    private LinearLayout listaPromociones;
    private TextView txtPromocionesVacias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);

        promocionViewModel = new PromocionViewModel(this);
        productoViewModel = new ProductoViewModel(this);
        listaPromociones = findViewById(R.id.listaPromocionesUsuario);
        txtPromocionesVacias = findViewById(R.id.txtPromocionesVacias);

        findViewById(R.id.btnVolverPromociones).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargarPromociones();
    }

    private void cargarPromociones() {
        listaPromociones.removeAllViews();
        promocionViewModel.listarPromocionesApi(true, new PromocionViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Promocion> promociones) {
                mostrarPromociones(promociones);
            }

            @Override
            public void alFallar(String mensaje) {
                txtPromocionesVacias.setText(mensaje);
                txtPromocionesVacias.setVisibility(View.VISIBLE);
            }
        });
    }

    private void mostrarPromociones(ArrayList<Promocion> promociones) {
        txtPromocionesVacias.setVisibility(promociones.isEmpty() ? View.VISIBLE : View.GONE);

        int i;
        for (i = 0; i < promociones.size(); i++) {
            listaPromociones.addView(crearTarjetaPromocion(promociones.get(i)));
        }
    }

    private View crearTarjetaPromocion(final Promocion promocion) {
        final Producto producto = productoViewModel.obtenerProductoPorId(promocion.getProductoId());

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setBackgroundResource(R.drawable.fondo_item_pedido);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(132)
        );
        cardParams.setMargins(0, 0, 0, dp(14));
        card.setLayoutParams(cardParams);

        ImageView imagen = new ImageView(this);
        imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mostrarImagen(imagen, producto);
        card.addView(imagen, new LinearLayout.LayoutParams(dp(104), dp(88)));

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setPadding(dp(14), 0, dp(8), 0);

        String nombre = producto == null ? promocion.getNombreProducto() : producto.getNombre();
        textos.addView(crearTexto(nombre, 15, "#FFFFFF", true));
        textos.addView(crearTexto("Antes: S/ " + String.format("%.2f", promocion.getPrecioOriginal()), 12, "#B8C7D9", false));
        textos.addView(crearTexto("Ahora: S/ " + String.format("%.2f", promocion.getPrecioOferta()), 18, "#00AFFF", true));
        textos.addView(crearTexto("Ahorras S/ " + String.format("%.2f", promocion.getPrecioOriginal() - promocion.getPrecioOferta()), 12, "#39FF66", true));
        textos.addView(crearTexto("Valido hasta: " + promocion.getFechaFin(), 11, "#B8C7D9", false));
        card.addView(textos, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        TextView descuento = crearTexto(calcularDescuento(promocion), 12, "#00AFFF", true);
        descuento.setGravity(Gravity.CENTER);
        descuento.setBackgroundResource(R.drawable.bg_btn_cantidad);
        card.addView(descuento, new LinearLayout.LayoutParams(dp(48), dp(32)));

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (producto == null) {
                    return;
                }

                Intent intent = new Intent(PromocionesActivity.this, DetalleProductoActivity.class);
                intent.putExtra("productoId", producto.getId());
                startActivity(intent);
            }
        });

        return card;
    }

    private TextView crearTexto(String texto, int tamano, String color, boolean negrita) {
        TextView textView = new TextView(this);
        textView.setText(texto == null ? "" : texto);
        textView.setTextColor(Color.parseColor(color));
        textView.setTextSize(tamano);
        textView.setMaxLines(1);
        textView.setPadding(0, dp(2), 0, 0);

        if (negrita) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        return textView;
    }

    private String calcularDescuento(Promocion promocion) {
        double descuento = 0;

        if (promocion.getPrecioOriginal() > 0) {
            descuento = ((promocion.getPrecioOriginal() - promocion.getPrecioOferta()) * 100) / promocion.getPrecioOriginal();
        }

        return "-" + Math.round(descuento) + "%";
    }

    private int obtenerImagen(Producto producto) {
        if (producto == null || producto.getImagen() == null || producto.getImagen().trim().isEmpty()) {
            return R.drawable.ic_tag;
        }

        int imagenId = getResources().getIdentifier(producto.getImagen(), "drawable", getPackageName());
        return imagenId == 0 ? R.drawable.ic_tag : imagenId;
    }

    private void mostrarImagen(ImageView imageView, Producto producto) {
        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                producto == null ? null : producto.getImagen(),
                R.drawable.ic_tag
        );
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
