package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.repository.ProductoRepository;

import java.util.ArrayList;

public class CatalogoViewModel {

    private ProductoRepository productoRepository;

    // constructor del viewmodel
    public CatalogoViewModel(Context context) {
        productoRepository = new ProductoRepository(context);
    }

    // listar productos del catalogo
    public ArrayList<Producto> listarProductos() {
        return productoRepository.listarProductos();
    }

    // buscar por nombre
    public ArrayList<Producto> buscarProductosPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarProductos();
        }

        return productoRepository.buscarProductosPorNombre(nombre.trim());
    }

    // filtrar por categoria
    public ArrayList<Producto> filtrarProductosPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return listarProductos();
        }

        return productoRepository.filtrarProductosPorCategoria(categoria.trim());
    }
}
