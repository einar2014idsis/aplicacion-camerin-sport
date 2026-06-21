package com.example.camerinosports.model;

public class Producto {

    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String talla;
    private String color;
    private double precio;
    private int stock;
    private String imagen;
    private boolean sostenible;

    public Producto() {
    }

    public Producto(int id, String nombre, String descripcion, String categoria,
                    String talla, String color, double precio, int stock,
                    String imagen, boolean sostenible) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.talla = talla;
        this.color = color;
        this.precio = precio;
        this.stock = stock;
        this.imagen = imagen;
        this.sostenible = sostenible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isSostenible() {
        return sostenible;
    }

    public void setSostenible(boolean sostenible) {
        this.sostenible = sostenible;
    }
}
