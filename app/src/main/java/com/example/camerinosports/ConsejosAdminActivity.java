package com.example.camerinosports;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camerinosports.model.Consejo;
import com.example.camerinosports.viewmodel.ConsejoViewModel;

import java.util.ArrayList;

public class ConsejosAdminActivity extends AppCompatActivity {

    private EditText edtTitulo;
    private EditText edtDescripcion;
    private EditText edtCategoria;
    private ImageView imgConsejo;
    private LinearLayout contenedor;
    private LinearLayout listaConsejos;
    private ConsejoViewModel consejoViewModel;
    private Consejo consejoSeleccionado;
    private String imagenSeleccionada = "";
    private ActivityResultLauncher<String[]> elegirImagenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos_admin);

        consejoViewModel = new ConsejoViewModel(this);
        prepararSelectorImagen();
        prepararContenido();
        cargarConsejos();
    }

    private void prepararSelectorImagen() {
        elegirImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null) {
                        return;
                    }

                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception ignored) {
                    }

                    imagenSeleccionada = uri.toString();
                    imgConsejo.setImageURI(uri);
                }
        );
    }

    private void prepararContenido() {
        findViewById(R.id.btnVolverConsejosAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        contenedor = findViewById(R.id.contenedorConsejosAdmin);
        edtTitulo = findViewById(R.id.edtTituloConsejoAdmin);
        edtDescripcion = findViewById(R.id.edtDescripcionConsejoAdmin);
        edtCategoria = findViewById(R.id.edtCategoriaConsejoAdmin);
        imgConsejo = findViewById(R.id.imgConsejoAdmin);
        listaConsejos = findViewById(R.id.contenedorListaConsejosAdmin);

        View.OnClickListener elegirImagen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirImagenLauncher.launch(new String[]{"image/*"});
            }
        };
        imgConsejo.setOnClickListener(elegirImagen);
        findViewById(R.id.btnElegirImagenConsejoAdmin).setOnClickListener(elegirImagen);

        findViewById(R.id.btnGuardarConsejoAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarConsejo();
            }
        });

        findViewById(R.id.btnLimpiarConsejoAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiar();
            }
        });
    }

    private void guardarConsejo() {
        Consejo consejo = new Consejo();
        consejo.setTitulo(edtTitulo.getText().toString().trim());
        consejo.setDescripcion(edtDescripcion.getText().toString().trim());
        consejo.setCategoria(edtCategoria.getText().toString().trim());
        consejo.setImagen(imagenSeleccionada);
        consejo.setActivo(true);

        if (consejoSeleccionado != null) {
            consejo.setId(consejoSeleccionado.getId());
        }
        findViewById(R.id.btnGuardarConsejoAdmin).setEnabled(false);
        consejoViewModel.guardarConsejoApi(consejo, new ConsejoViewModel.ResultadoOperacion() {
            @Override
            public void alCompletar(Consejo consejoGuardado) {
                findViewById(R.id.btnGuardarConsejoAdmin).setEnabled(true);
                limpiar();
                cargarConsejos();
                Toast.makeText(ConsejosAdminActivity.this,
                        "consejo guardado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void alFallar(String mensaje) {
                findViewById(R.id.btnGuardarConsejoAdmin).setEnabled(true);
                Toast.makeText(ConsejosAdminActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarConsejos() {
        listaConsejos.removeAllViews();
        consejoViewModel.listarConsejosApi(false, new ConsejoViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Consejo> consejos) {
                mostrarListaConsejos(consejos);
            }

            @Override
            public void alFallar(String mensaje) {
                listaConsejos.addView(crearTexto(mensaje));
            }
        });
    }

    private void mostrarListaConsejos(ArrayList<Consejo> consejos) {
        if (consejos.isEmpty()) {
            listaConsejos.addView(crearTexto("No hay consejos registrados"));
            return;
        }

        int i;
        for (i = 0; i < consejos.size(); i++) {
            final Consejo consejo = consejos.get(i);
            listaConsejos.addView(crearItemConsejo(consejo));
        }
    }

    private TextView crearTexto(String texto) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextColor(0xFFFFFFFF);
        textView.setPadding(16, 18, 16, 18);
        return textView;
    }

    private View crearItemConsejo(final Consejo consejo) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackgroundResource(R.drawable.fondo_resumen_pedido);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardParams);

        LinearLayout contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.HORIZONTAL);
        contenido.setGravity(Gravity.CENTER_VERTICAL);

        View icono = crearIconoConsejo(consejo);
        contenido.addView(icono, new LinearLayout.LayoutParams(dp(58), dp(58)));

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setPadding(dp(14), 0, 0, 0);

        TextView titulo = crearTexto(consejo.getTitulo());
        titulo.setTextSize(15);
        titulo.setTypeface(null, 1);

        TextView descripcion = crearTexto(consejo.getDescripcion());
        descripcion.setTextColor(0xFFB8C7D9);
        descripcion.setTextSize(12);

        TextView categoria = crearTexto("Categoria: " + consejo.getCategoria());
        categoria.setTextColor(0xFF00AFFF);
        categoria.setTextSize(11);

        textos.addView(titulo);
        textos.addView(descripcion);
        textos.addView(categoria);
        contenido.addView(textos, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        LinearLayout acciones = new LinearLayout(this);
        acciones.setOrientation(LinearLayout.HORIZONTAL);
        acciones.setGravity(Gravity.CENTER);
        acciones.setPadding(0, dp(10), 0, 0);

        TextView editar = crearBotonAccion("EDITAR", true);
        TextView eliminar = crearBotonAccion("ELIMINAR", false);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarConsejo(consejo);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consejoViewModel.eliminarConsejoApi(consejo, new ConsejoViewModel.ResultadoOperacion() {
                    @Override
                    public void alCompletar(Consejo eliminado) {
                        cargarConsejos();
                        Toast.makeText(ConsejosAdminActivity.this,
                                "consejo eliminado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void alFallar(String mensaje) {
                        Toast.makeText(ConsejosAdminActivity.this,
                                mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        acciones.addView(editar, new LinearLayout.LayoutParams(0, dp(42), 1));
        LinearLayout.LayoutParams eliminarParams = new LinearLayout.LayoutParams(0, dp(42), 1);
        eliminarParams.setMargins(dp(10), 0, 0, 0);
        acciones.addView(eliminar, eliminarParams);

        card.addView(contenido);
        card.addView(acciones);
        return card;
    }

    private View crearIconoConsejo(Consejo consejo) {
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

        TextView icono = new TextView(this);
        icono.setText(obtenerIcono(consejo));
        icono.setTextSize(28);
        icono.setGravity(Gravity.CENTER);
        return icono;
    }

    private String obtenerIcono(Consejo consejo) {
        String texto = (consejo.getTitulo() + " " + consejo.getCategoria()).toLowerCase();

        if (texto.contains("responsable")) return "♻";
        if (texto.contains("agua") || texto.contains("lavado")) return "💧";
        if (texto.contains("sombra") || texto.contains("secado")) return "☀";
        if (texto.contains("blanqueador") || texto.contains("lejía") || texto.contains("leguia")) return "✕";
        if (texto.contains("zapatilla") || texto.contains("calzado")) return "👟";
        return "•";
    }

    private TextView crearBotonAccion(String texto, boolean principal) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(12);
        textView.setTypeface(null, 1);
        textView.setTextColor(principal ? 0xFFFFFFFF : 0xFFFF4D4D);
        textView.setBackgroundResource(principal ? R.drawable.bg_btn_continuar_pedido : R.drawable.bg_card_admin_moderno);
        return textView;
    }

    private void seleccionarConsejo(Consejo consejo) {
        consejoSeleccionado = consejo;
        edtTitulo.setText(consejo.getTitulo());
        edtDescripcion.setText(consejo.getDescripcion());
        edtCategoria.setText(consejo.getCategoria());
        imagenSeleccionada = consejo.getImagen() == null ? "" : consejo.getImagen();
        mostrarImagenFormulario(consejo);
    }

    private void limpiar() {
        consejoSeleccionado = null;
        edtTitulo.setText("");
        edtDescripcion.setText("");
        edtCategoria.setText("");
        imagenSeleccionada = "";
        imgConsejo.setImageResource(R.drawable.ic_tag);
    }

    private void mostrarImagenFormulario(Consejo consejo) {
        com.example.camerinosports.util.Imagenes.mostrar(
                imgConsejo,
                consejo.getImagen(),
                R.drawable.ic_tag
        );
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
