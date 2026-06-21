package com.example.camerinosports.repository;

import android.content.Context;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.network.ApiCliente;
import com.example.camerinosports.network.ProductoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoRepository {

    public interface ResultadoSincronizacion {
        void alCompletar();
        void alFallar();
    }

    public interface ResultadoOperacion {
        void alCompletar();
        void alFallar(String mensaje);
    }

    // mantiene una copia local para que las pantallas puedan consultar los productos
    private static final ArrayList<Producto> cacheProductos = new ArrayList<>();
    private ProductoApi productoApi;

    // constructor del repository
    public ProductoRepository(Context context) {
        productoApi = ApiCliente.obtener(context).create(ProductoApi.class);
    }

    // consulta en memoria de datos previamente recibidos desde sql server.
    public ArrayList<Producto> listarProductos() {
        return new ArrayList<>(cacheProductos);
    }

    public Producto obtenerProductoPorId(int id) {
        for (Producto producto : cacheProductos) {
            if (producto.getId() == id) {
                return producto;
            }
        }
        return null;
    }

    public void eliminarProductoServicio(final int id, final ResultadoOperacion resultado) {
        productoApi.eliminarProducto(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() || response.code() == 404) {
                    eliminarDeCache(id);
                    resultado.alCompletar();
                    return;
                }

                resultado.alFallar("No se pudo eliminar el producto del servidor");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    // buscar productos por nombre
    public ArrayList<Producto> buscarProductosPorNombre(String nombre) {
        ArrayList<Producto> encontrados = new ArrayList<>();
        String texto = nombre == null ? "" : nombre.trim().toLowerCase();
        for (Producto producto : cacheProductos) {
            if (producto.getNombre() != null &&
                    producto.getNombre().toLowerCase().contains(texto)) {
                encontrados.add(producto);
            }
        }
        return encontrados;
    }

    // filtrar productos por categoria
    public ArrayList<Producto> filtrarProductosPorCategoria(String categoria) {
        ArrayList<Producto> filtrados = new ArrayList<>();
        for (Producto producto : cacheProductos) {
            if (producto.getCategoria() != null &&
                    producto.getCategoria().equalsIgnoreCase(categoria)) {
                filtrados.add(producto);
            }
        }
        return filtrados;
    }

    // listar productos con poco stock
    public ArrayList<Producto> listarProductosConStockBajo() {
        ArrayList<Producto> stockBajo = new ArrayList<>();
        for (Producto producto : cacheProductos) {
            if (producto.getStock() <= 5) {
                stockBajo.add(producto);
            }
        }
        return stockBajo;
    }

    public void sincronizarProductos() {
        sincronizarProductos(null);
    }

    public void sincronizarProductos(final ResultadoSincronizacion resultado) {
        // reemplaza la copia local solo cuando el servidor responde correctamente
        productoApi.listarProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    if (resultado != null) {
                        resultado.alFallar();
                    }
                    return;
                }

                reemplazarCache(response.body());
                if (resultado != null) {
                    resultado.alCompletar();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                if (resultado != null) {
                    resultado.alFallar();
                }
            }
        });
    }

    public void registrarProductoServicio(final Producto producto, final ResultadoOperacion resultado) {
        // la respuesta del servidor contiene el producto con su identificador definitivo
        productoApi.registrarProducto(producto).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar();
                    return;
                }
                resultado.alFallar(response.code() == 409
                        ? "Ya existe un producto con ese identificador"
                        : "No se pudo registrar el producto");
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void actualizarProductoServicio(final Producto producto, final ResultadoOperacion resultado) {
        productoApi.actualizarProducto(producto.getId(), producto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    guardarEnCache(producto);
                    resultado.alCompletar();
                    return;
                }
                resultado.alFallar("No se pudo actualizar el producto");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    private static void reemplazarCache(List<Producto> productos) {
        cacheProductos.clear();
        cacheProductos.addAll(productos);
    }

    private static void guardarEnCache(Producto producto) {
        // se elimina la version anterior para no guardar el mismo producto dos veces
        eliminarDeCache(producto.getId());
        cacheProductos.add(producto);
    }

    private static void eliminarDeCache(int id) {
        for (int i = cacheProductos.size() - 1; i >= 0; i--) {
            if (cacheProductos.get(i).getId() == id) {
                cacheProductos.remove(i);
            }
        }
    }
}
