package com.example.camerinosports.model;

import java.util.ArrayList;

public class CarritoMemoria {
    private static final ArrayList<CarritoItem> items = new ArrayList<>();

    public static ArrayList<CarritoItem> obtenerItems() {
        return items;
    }

    public static boolean agregarProducto(Producto producto) {
        if (producto == null || producto.getStock() <= 0) {
            return false;
        }

        for (CarritoItem item : items) {
            if (item.getProducto().getId() == producto.getId()) {
                if (item.getCantidad() >= producto.getStock()) {
                    return false;
                }
                item.setCantidad(item.getCantidad() + 1);
                return true;
            }
        }

        items.add(new CarritoItem(producto, 1));
        return true;
    }

    public static void eliminarProducto(int productoId) {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).getProducto().getId() == productoId) {
                items.remove(i);
            }
        }
    }

    public static boolean aumentar(int productoId) {
        for (CarritoItem item : items) {
            if (item.getProducto().getId() == productoId) {
                if (item.getCantidad() >= item.getProducto().getStock()) {
                    return false;
                }
                item.setCantidad(item.getCantidad() + 1);
                return true;
            }
        }
        return false;
    }

    public static void disminuir(int productoId) {
        for (int i = items.size() - 1; i >= 0; i--) {
            CarritoItem item = items.get(i);
            if (item.getProducto().getId() == productoId) {
                if (item.getCantidad() <= 1) {
                    items.remove(i);
                } else {
                    item.setCantidad(item.getCantidad() - 1);
                }
            }
        }
    }

    public static double calcularTotal() {
        double total = 0;
        for (CarritoItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public static int contarProductos() {
        int total = 0;
        for (CarritoItem item : items) {
            total += item.getCantidad();
        }
        return total;
    }

    public static void vaciar() {
        items.clear();
    }
}
