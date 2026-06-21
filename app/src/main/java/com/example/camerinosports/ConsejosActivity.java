package com.example.camerinosports;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Consejo;
import com.example.camerinosports.viewmodel.ConsejoViewModel;

import java.util.ArrayList;

public class ConsejosActivity extends AppCompatActivity {

    private ConsejoViewModel consejoViewModel;
    private LinearLayout contenedorConsejos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos);

        consejoViewModel = new ConsejoViewModel(this);
        prepararListaDesdeServicio();
        configurarEventos();
        cargarConsejos();
    }

    private void configurarEventos() {
        findViewById(R.id.icMenuConsejos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void prepararListaDesdeServicio() {
        ScrollView scrollView = findViewById(R.id.scrollConsejos);
        LinearLayout contenido = (LinearLayout) scrollView.getChildAt(0);

        // se conservan titulo y subtitulo del diseno; las tarjetas ahora se cargan desde la api.
        if (contenido.getChildCount() > 2) {
            contenido.removeViews(2, contenido.getChildCount() - 2);
        }

        contenedorConsejos = contenido;
    }

    private void cargarConsejos() {
        consejoViewModel.listarConsejosApi(true, new ConsejoViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Consejo> consejos) {
                mostrarConsejos(consejos);
            }

            @Override
            public void alFallar(String mensaje) {
                contenedorConsejos.addView(crearTextoVacio(mensaje));
            }
        });
    }

    private void mostrarConsejos(ArrayList<Consejo> consejos) {
        if (consejos.isEmpty()) {
            contenedorConsejos.addView(crearTextoVacio("No hay consejos disponibles"));
            return;
        }

        Consejo destacado = obtenerDestacado(consejos);
        if (destacado != null) {
            contenedorConsejos.addView(crearTarjetaDestacada(destacado));
        }

        TextView titulo = crearTituloSeccion("Recomendaciones");
        contenedorConsejos.addView(titulo);

        int i;
        for (i = 0; i < consejos.size(); i++) {
            Consejo consejo = consejos.get(i);
            if (destacado == null || consejo.getId() != destacado.getId()) {
                contenedorConsejos.addView(crearTarjetaConsejo(consejo));
            }
        }
    }

    private Consejo obtenerDestacado(ArrayList<Consejo> consejos) {
        int i;

        for (i = 0; i < consejos.size(); i++) {
            Consejo consejo = consejos.get(i);
            if ("Consumo responsable".equalsIgnoreCase(consejo.getTitulo())) {
                return consejo;
            }
        }

        return consejos.get(0);
    }

    private TextView crearTituloSeccion(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(18);
        textView.setTypeface(null, 1);
        textView.setPadding(0, dp(22), 0, dp(2));
        return textView;
    }

    private View crearTarjetaDestacada(Consejo consejo) {
        LinearLayout card = crearBaseTarjeta(dp(110), dp(20));

        View icono = crearIconoConsejo(consejo, true);
        card.addView(icono, new LinearLayout.LayoutParams(dp(54), dp(54)));

        LinearLayout textos = crearContenedorTextos();
        textos.addView(crearTitulo(consejo.getTitulo(), 17));
        textos.addView(crearDescripcion(consejo.getDescripcion()));
        card.addView(textos, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        return card;
    }

    private View crearTarjetaConsejo(Consejo consejo) {
        LinearLayout card = crearBaseTarjeta(dp(105), dp(12));

        View icono = crearIconoConsejo(consejo, false);
        card.addView(icono, new LinearLayout.LayoutParams(dp(42), dp(42)));

        LinearLayout textos = crearContenedorTextos();
        textos.addView(crearTitulo(consejo.getTitulo(), 16));
        textos.addView(crearDescripcion(consejo.getDescripcion()));
        card.addView(textos, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        return card;
    }

    private View crearIconoConsejo(Consejo consejo, boolean destacado) {
        if (consejo.getImagen() != null && consejo.getImagen().startsWith("content://")) {
            ImageView imagen = new ImageView(this);
            imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
            com.example.camerinosports.util.Imagenes.mostrar(
                    imagen,
                    consejo.getImagen(),
                    R.drawable.ic_tag
            );
            return imagen;
        }

        if (destacado) {
            return crearIcono("♻", 36, 0xFF00AFFF);
        }

        return crearIcono(
                obtenerIcono(consejo),
                obtenerIconoTamano(consejo),
                obtenerIconoColor(consejo)
        );
    }

    private LinearLayout crearBaseTarjeta(int alto, int margenTop) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackgroundResource(R.drawable.fondo_item_pedido);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                alto
        );
        cardParams.setMargins(0, margenTop, 0, 0);
        card.setLayoutParams(cardParams);
        return card;
    }

    private LinearLayout crearContenedorTextos() {
        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setPadding(dp(14), 0, 0, 0);
        return textos;
    }

    private TextView crearTitulo(String texto, int tamano) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(tamano);
        textView.setTypeface(null, 1);
        return textView;
    }

    private TextView crearDescripcion(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFB8C7D9);
        textView.setTextSize(12);
        textView.setPadding(0, dp(4), 0, 0);
        return textView;
    }

    private TextView crearIcono(String texto, int tamano, int color) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(tamano);
        textView.setTextColor(color);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    private TextView crearTextoVacio(String mensaje) {
        TextView textView = new TextView(this);
        textView.setText(mensaje);
        textView.setTextColor(0xFFB8C7D9);
        textView.setTextSize(14);
        textView.setPadding(0, dp(22), 0, 0);
        return textView;
    }

    private String obtenerIcono(Consejo consejo) {
        String texto = (consejo.getTitulo() + " " + consejo.getCategoria()).toLowerCase();

        if (texto.contains("agua") || texto.contains("lavado")) {
            return "💧";
        }
        if (texto.contains("sombra") || texto.contains("secado")) {
            return "☀";
        }
        if (texto.contains("blanqueador")) {
            return "✕";
        }
        if (texto.contains("zapatilla") || texto.contains("calzado")) {
            return "👟";
        }

        return "•";
    }

    private int obtenerIconoTamano(Consejo consejo) {
        String texto = consejo.getTitulo().toLowerCase();
        if (texto.contains("blanqueador")) {
            return 28;
        }
        return 26;
    }

    private int obtenerIconoColor(Consejo consejo) {
        String texto = consejo.getTitulo().toLowerCase();
        if (texto.contains("blanqueador")) {
            return 0xFFFF4D4D;
        }
        if (texto.contains("sombra")) {
            return 0xFFFFC107;
        }
        if (texto.contains("agua")) {
            return 0xFF66D9FF;
        }
        return 0xFF00AFFF;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
