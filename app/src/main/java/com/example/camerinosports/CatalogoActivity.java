package com.example.camerinosports;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camerinosports.adapter.ProductoAdapter;
import com.example.camerinosports.model.CarritoMemoria;
import com.example.camerinosports.model.Producto;
import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.model.UsuarioSesion;
import com.example.camerinosports.viewmodel.ProductoViewModel;
import com.example.camerinosports.viewmodel.PromocionViewModel;
import com.example.camerinosports.viewmodel.UsuarioViewModel;

import java.util.ArrayList;

public class CatalogoActivity extends AppCompatActivity {

    private ProductoViewModel productoViewModel;
    private PromocionViewModel promocionViewModel;
    private UsuarioViewModel usuarioViewModel;
    private ProductoAdapter productoAdapter;
    private RecyclerView recyclerProductos;
    private EditText edtBuscar;
    private TextView txtSaludo;
    private TextView txtNavInicio;
    private TextView txtNavCategorias;
    private TextView txtTituloCatalogo;
    private TextView badgeCarritoCatalogo;
    private NestedScrollView scrollCatalogo;
    private View tituloCategorias;
    private View panelModoCategorias;
    private View bannerInicio;
    private View indicadoresBanner;
    private TextView txtBannerEtiqueta;
    private TextView txtBannerTitulo;
    private TextView txtBannerDescripcion;
    private ImageView imgBanner;
    private View[] puntosBanner;
    private int bannerActual;
    private final Handler handlerBanner = new Handler(Looper.getMainLooper());
    private final Runnable rotadorBanner = new Runnable() {
        @Override
        public void run() {
            avanzarBanner();
            handlerBanner.postDelayed(this, 3500);
        }
    };

    private final String[] bannerEtiquetas = {
            "NUEVA COLECCION",
            "ENTRENA MEJOR",
            "EQUIPO COMPLETO",
            "OFERTAS ACTIVAS"
    };
    private final String[] bannerTitulos = {
            "VELOCIDAD\nSIN LIMITES",
            "FUERZA\nEN CADA JUGADA",
            "TU PACK\nDEPORTIVO",
            "PRECIO\nDE CAMPEON"
    };
    private final String[] bannerDescripciones = {
            "Rendimiento extremo en cada desafio.",
            "Prendas y accesorios listos para competir.",
            "Balon, ropa y accesorios para tu rutina.",
            "Aprovecha productos seleccionados en oferta."
    };
    private final int[] bannerImagenes = {
            R.drawable.img_shoe_login,
            R.drawable.banner_deportes_generado,
            R.drawable.img_admin_consejos_balon,
            R.drawable.producto_botin_destacado_negro_azul
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        productoViewModel = new ProductoViewModel(this);
        promocionViewModel = new PromocionViewModel(this);
        productoViewModel.sincronizarProductos(new ProductoViewModel.ResultadoSincronizacion() {
            @Override
            public void alCompletar() {
                cargarProductos();
            }

            @Override
            public void alFallar() {
                Toast.makeText(CatalogoActivity.this,
                        "No se pudieron cargar los productos desde el servidor",
                        Toast.LENGTH_SHORT).show();
            }
        });
        promocionViewModel.listarPromocionesApi(true, new PromocionViewModel.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<com.example.camerinosports.model.Promocion> promociones) {
                cargarProductos();
            }

            @Override
            public void alFallar(String mensaje) {
                // el catalogo puede mostrarse sin ofertas si la consulta falla.
            }
        });
        usuarioViewModel = new UsuarioViewModel(this);
        recyclerProductos = findViewById(R.id.recyclerProductos);
        edtBuscar = findViewById(R.id.edtBuscar);
        txtSaludo = findViewById(R.id.txtSaludo);
        txtNavInicio = findViewById(R.id.txtNavInicioCatalogo);
        txtNavCategorias = findViewById(R.id.txtNavCategoriasCatalogo);
        txtTituloCatalogo = findViewById(R.id.txtTituloCatalogo);
        badgeCarritoCatalogo = findViewById(R.id.badgeCarritoCatalogo);
        scrollCatalogo = findViewById(R.id.scrollCatalogo);
        tituloCategorias = findViewById(R.id.tituloCategoriasCatalogo);
        panelModoCategorias = findViewById(R.id.panelModoCategorias);
        bannerInicio = findViewById(R.id.bannerInicioCatalogo);
        indicadoresBanner = findViewById(R.id.indicadoresBannerCatalogo);
        txtBannerEtiqueta = findViewById(R.id.txtBannerEtiquetaCatalogo);
        txtBannerTitulo = findViewById(R.id.txtBannerTituloCatalogo);
        txtBannerDescripcion = findViewById(R.id.txtBannerDescripcionCatalogo);
        imgBanner = findViewById(R.id.imgBannerCatalogo);
        puntosBanner = new View[]{
                findViewById(R.id.indicadorBannerUno),
                findViewById(R.id.indicadorBannerDos),
                findViewById(R.id.indicadorBannerTres),
                findViewById(R.id.indicadorBannerCuatro)
        };

        configurarRecycler();
        configurarEventos();
        configurarBannerRotativo();
        cargarSaludoUsuario();
        actualizarBadgeCarrito();
        cargarProductos();
        revisarEntradaCategorias();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSaludoUsuario();
        actualizarBadgeCarrito();
        iniciarBannerRotativo();
    }

    @Override
    protected void onPause() {
        detenerBannerRotativo();
        super.onPause();
    }

    private void configurarRecycler() {
        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 1));

        productoAdapter = new ProductoAdapter(this, new ArrayList<Producto>(), new ProductoAdapter.OnProductoClickListener() {
            @Override
            public void onProductoClick(Producto producto) {
                Intent intent = new Intent(CatalogoActivity.this, DetalleProductoActivity.class);
                intent.putExtra("productoId", producto.getId());
                startActivity(intent);
            }
        });

        recyclerProductos.setAdapter(productoAdapter);
    }

    private void configurarEventos() {
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int inicio, int cantidad, int despues) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int inicio, int antes, int cantidad) {
                productoAdapter.actualizarLista(filtrarProductosConImagen(
                        productoViewModel.buscarProductosPorNombre(charSequence.toString())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        findViewById(R.id.catCalzado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtrar("Calzado");
            }
        });

        findViewById(R.id.catRopa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtrar("Ropa");
            }
        });

        findViewById(R.id.catAccesorios).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtrar("Accesorios");
            }
        });

        findViewById(R.id.catOfertas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CatalogoActivity.this, PromocionesActivity.class));
            }
        });

        findViewById(R.id.btnVerTodo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aplicarModoInicio();
                cargarProductos();
            }
        });

        findViewById(R.id.navInicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aplicarModoInicio();
            }
        });

        findViewById(R.id.navCarrito).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CatalogoActivity.this, CarritoActivity.class));
            }
        });

        findViewById(R.id.navCategorias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCategorias();
            }
        });

        findViewById(R.id.navPerfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CatalogoActivity.this, PerfilUsuarioActivity.class));
            }
        });
    }

    private void cargarProductos() {
        ArrayList<Producto> productos = filtrarProductosConImagen(productoViewModel.listarProductos());
        productoAdapter.actualizarLista(productos);

        if (productos.isEmpty()) {
            Toast.makeText(this, "no hay productos disponibles", Toast.LENGTH_SHORT).show();
        }
    }

    private void filtrar(String categoria) {
        ArrayList<Producto> productos = filtrarProductosConImagen(
                productoViewModel.filtrarProductosPorCategoria(categoria));
        productoAdapter.actualizarLista(productos);

        if (productos.isEmpty()) {
            Toast.makeText(this, "sin resultados", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Producto> filtrarProductosConImagen(ArrayList<Producto> productos) {
        ArrayList<Producto> visibles = new ArrayList<>();

        for (Producto producto : productos) {
            // Un producto debe aparecer aunque su imagen no esté disponible.
            // Imagenes.mostrar se encarga de usar el icono predeterminado.
            if (producto != null) {
                visibles.add(producto);
            }
        }

        return visibles;
    }

    private void cargarSaludoUsuario() {
        usuarioViewModel.obtenerUsuarioApi(
                UsuarioSesion.getCorreoActual(),
                new UsuarioViewModel.ResultadoUsuario() {
                    @Override
                    public void alCompletar(Usuario usuario) {
                        if (usuario.getNombre() != null && !usuario.getNombre().trim().isEmpty()) {
                            txtSaludo.setText("Hola, " + usuario.getNombre().trim());
                        } else {
                            txtSaludo.setText("Hola, Usuario");
                        }
                    }

                    @Override
                    public void alFallar(String mensaje) {
                        txtSaludo.setText("Hola, Usuario");
                    }
                });
    }

    private void revisarEntradaCategorias() {
        if (getIntent().getBooleanExtra("mostrarCategorias", false)) {
            scrollCatalogo.post(new Runnable() {
                @Override
                public void run() {
                    mostrarCategorias();
                }
            });
        } else {
            aplicarModoInicio();
        }
    }

    private void mostrarCategorias() {
        panelModoCategorias.setVisibility(View.VISIBLE);
        bannerInicio.setVisibility(View.GONE);
        indicadoresBanner.setVisibility(View.GONE);
        txtTituloCatalogo.setText("PRODUCTOS POR CATEGORIA");
        txtNavInicio.setTextColor(Color.rgb(90, 99, 112));
        txtNavCategorias.setTextColor(Color.rgb(0, 123, 255));
        scrollCatalogo.smoothScrollTo(0, tituloCategorias.getTop());
        Toast.makeText(this, "elige calzado, ropa, accesorios u ofertas", Toast.LENGTH_SHORT).show();
    }

    private void aplicarModoInicio() {
        panelModoCategorias.setVisibility(View.GONE);
        bannerInicio.setVisibility(View.VISIBLE);
        indicadoresBanner.setVisibility(View.VISIBLE);
        txtTituloCatalogo.setText("CATÁLOGO");
        txtNavInicio.setTextColor(Color.rgb(0, 123, 255));
        txtNavCategorias.setTextColor(Color.rgb(90, 99, 112));
        scrollCatalogo.smoothScrollTo(0, 0);
    }

    private void configurarBannerRotativo() {
        bannerActual = 0;
        mostrarBanner(bannerActual, false);
    }

    private void iniciarBannerRotativo() {
        detenerBannerRotativo();
        handlerBanner.postDelayed(rotadorBanner, 3500);
    }

    private void detenerBannerRotativo() {
        handlerBanner.removeCallbacks(rotadorBanner);
    }

    private void avanzarBanner() {
        if (bannerInicio.getVisibility() != View.VISIBLE) {
            return;
        }

        bannerActual = (bannerActual + 1) % bannerTitulos.length;
        mostrarBanner(bannerActual, true);
    }

    private void mostrarBanner(int posicion, boolean animar) {
        txtBannerEtiqueta.setText(bannerEtiquetas[posicion]);
        txtBannerTitulo.setText(bannerTitulos[posicion]);
        txtBannerDescripcion.setText(bannerDescripciones[posicion]);
        imgBanner.setImageResource(bannerImagenes[posicion]);
        actualizarIndicadoresBanner(posicion);

        if (animar) {
            bannerInicio.setAlpha(0.45f);
            bannerInicio.animate().alpha(1f).setDuration(260).start();
        }
    }

    private void actualizarIndicadoresBanner(int activo) {
        for (int i = 0; i < puntosBanner.length; i++) {
            puntosBanner[i].setBackgroundColor(i == activo
                    ? Color.rgb(0, 123, 255)
                    : Color.rgb(42, 47, 56));

            android.view.ViewGroup.LayoutParams params = puntosBanner[i].getLayoutParams();
            params.width = dp(i == activo ? 18 : 6);
            puntosBanner[i].setLayoutParams(params);
        }
    }

    private void actualizarBadgeCarrito() {
        int cantidad = CarritoMemoria.contarProductos();

        if (cantidad <= 0) {
            badgeCarritoCatalogo.setVisibility(View.GONE);
            return;
        }

        badgeCarritoCatalogo.setText(String.valueOf(cantidad));
        badgeCarritoCatalogo.setVisibility(View.VISIBLE);
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
