package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.repository.ProductoRepository;
import com.example.camerinosports.util.Validaciones;

import java.util.ArrayList;

public class ProductoViewModel {

    public interface ResultadoSincronizacion {
        void alCompletar();
        void alFallar();
    }

    public interface ResultadoOperacion {
        void alCompletar();
        void alFallar(String mensaje);
    }

    private ProductoRepository productoRepository;

    // constructor del viewmodel
    public ProductoViewModel(Context context) {
        productoRepository = new ProductoRepository(context);
    }

    // obtener todos los productos
    public ArrayList<Producto> listarProductos() {
        return productoRepository.listarProductos();
    }

    // obtener producto por id
    public Producto obtenerProductoPorId(int id) {
        return productoRepository.obtenerProductoPorId(id);
    }

    public void guardarProductoApi(Producto producto, boolean esNuevo,
                                   final ResultadoOperacion resultado) {
        // la validacion se hace antes de enviar datos incompletos al servidor
        if (!productoValido(producto)) {
            resultado.alFallar("Revisa los datos del producto");
            return;
        }

        ProductoRepository.ResultadoOperacion adaptador = adaptar(resultado);
        // el mismo formulario sirve para registrar y para editar
        if (esNuevo) {
            productoRepository.registrarProductoServicio(producto, adaptador);
        } else {
            productoRepository.actualizarProductoServicio(producto, adaptador);
        }
    }

    public void eliminarProductoApi(int id, final ResultadoOperacion resultado) {
        productoRepository.eliminarProductoServicio(id, new ProductoRepository.ResultadoOperacion() {
            @Override
            public void alCompletar() {
                resultado.alCompletar();
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    // buscar productos por nombre
    public ArrayList<Producto> buscarProductosPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarProductos();
        }

        return productoRepository.buscarProductosPorNombre(nombre.trim());
    }

    // filtrar productos por categoria
    public ArrayList<Producto> filtrarProductosPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return listarProductos();
        }

        return productoRepository.filtrarProductosPorCategoria(categoria.trim());
    }

    // listar productos con poco stock
    public ArrayList<Producto> listarProductosConStockBajo() {
        return productoRepository.listarProductosConStockBajo();
    }

    public void sincronizarProductos() {
        productoRepository.sincronizarProductos();
    }

    public void sincronizarProductos(final ResultadoSincronizacion resultado) {
        productoRepository.sincronizarProductos(new ProductoRepository.ResultadoSincronizacion() {
            @Override
            public void alCompletar() {
                resultado.alCompletar();
            }

            @Override
            public void alFallar() {
                resultado.alFallar();
            }
        });
    }

    private ProductoRepository.ResultadoOperacion adaptar(final ResultadoOperacion resultado) {
        // convierte la respuesta del repositorio al tipo que usa la pantalla
        return new ProductoRepository.ResultadoOperacion() {
            @Override
            public void alCompletar() {
                resultado.alCompletar();
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        };
    }

    private boolean productoValido(Producto producto) {
        // todas las reglas deben cumplirse antes de guardar el producto
        if (producto == null) {
            return false;
        }

        if (!Validaciones.nombreProducto(producto.getNombre())) {
            return false;
        }

        if (!Validaciones.categoriaProducto(producto.getCategoria())) {
            return false;
        }

        if (!Validaciones.tallaProducto(producto.getCategoria(), producto.getTalla())) {
            return false;
        }

        if (!Validaciones.colorProducto(producto.getColor())) {
            return false;
        }

        if (producto.getPrecio() <= 0 || producto.getPrecio() > 100000) {
            return false;
        }

        if (producto.getStock() < 0 || producto.getStock() > 100000) {
            return false;
        }

        if (producto.getImagen() == null || producto.getImagen().trim().isEmpty()) {
            return false;
        }

        return true;
    }
}
