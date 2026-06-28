package com.example.camerinosports.model;

import java.util.ArrayList;

public class Pedido {
    private int id;
    private int usuarioId;
    private String cliente;
    private String telefono;
    private String correo;
    private String direccion;
    private String referencia;
    private String fecha;
    private double total;
    private String estado;
    private String comentario;
    private double latitud;
    private double longitud;
    private String metodoPago;
    private String estadoPago;
    private String codigoOperacionPago;
    private String culqiToken;
    private ArrayList<PedidoDetalle> detalles;

    public Pedido() {
        detalles = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getCodigoOperacionPago() {
        return codigoOperacionPago;
    }

    public void setCodigoOperacionPago(String codigoOperacionPago) {
        this.codigoOperacionPago = codigoOperacionPago;
    }

    public String getCulqiToken() {
        return culqiToken;
    }

    public void setCulqiToken(String culqiToken) {
        this.culqiToken = culqiToken;
    }

    public ArrayList<PedidoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<PedidoDetalle> detalles) {
        this.detalles = detalles;
    }
}
