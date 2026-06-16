package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Producto;
import com.example.camerinosports.repository.ProductoRepository;

import java.util.ArrayList;

public class ProductoViewModel {

    private ProductoRepository productoRepository;

    // constructor del viewmodel
    public ProductoViewModel(Context context) {
        productoRepository = new ProductoRepository(context);
    }

    // registrar un nuevo producto
    public boolean registrarProducto(Producto producto) {

        // validar nombre
        if (producto.getNombre().trim().isEmpty()) {
            return false;
        }

        // validar categoria
        if (producto.getCategoria().trim().isEmpty()) {
            return false;
        }

        // validar precio
        if (producto.getPrecio() <= 0) {
            return false;
        }

        // validar stock
        if (producto.getStock() < 0) {
            return false;
        }

        return productoRepository.registrarProducto(producto);
    }

    // obtener todos los productos
    public ArrayList<Producto> listarProductos() {
        return productoRepository.listarProductos();
    }

    // actualizar un producto existente
    public boolean actualizarProducto(Producto producto) {
        return productoRepository.actualizarProducto(producto);
    }

    // eliminar un producto por id
    public boolean eliminarProducto(int id) {
        return productoRepository.eliminarProducto(id);
    }
}