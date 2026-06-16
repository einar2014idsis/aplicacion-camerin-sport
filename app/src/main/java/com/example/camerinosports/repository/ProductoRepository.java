package com.example.camerinosports.repository;

import android.content.Context;

import com.example.camerinosports.database.DatabaseHelper;
import com.example.camerinosports.model.Producto;

import java.util.ArrayList;

public class ProductoRepository {

    private DatabaseHelper databaseHelper;

    // constructor del repository
    public ProductoRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    // registrar un nuevo producto
    public boolean registrarProducto(Producto producto) {
        return databaseHelper.registrarProducto(producto);
    }

    // obtener todos los productos
    public ArrayList<Producto> listarProductos() {
        return databaseHelper.listarProductos();
    }

    // actualizar un producto existente
    public boolean actualizarProducto(Producto producto) {
        return databaseHelper.actualizarProducto(producto);
    }

    // eliminar un producto por id
    public boolean eliminarProducto(int id) {
        return databaseHelper.eliminarProducto(id);
    }
}