package com.example.camerinosports.model;

public class Usuario {

    private int id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String password;
    private String rol;

    // constructor vacio
    public Usuario() {
    }

    // constructor con parametros
    public Usuario(int id, String nombre, String apellidos,
                   String correo, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }

    // obtener id
    public int getId() {
        return id;
    }

    // asignar id
    public void setId(int id) {
        this.id = id;
    }

    // obtener nombre
    public String getNombre() {
        return nombre;
    }

    // asignar nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // obtener apellidos
    public String getApellidos() {
        return apellidos;
    }

    // asignar apellidos
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    // obtener correo
    public String getCorreo() {
        return correo;
    }

    // asignar correo
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // obtener contrasena
    public String getPassword() {
        return password;
    }

    // asignar contrasena
    public void setPassword(String password) {
        this.password = password;
    }

    // obtener rol
    public String getRol() {
        return rol;
    }

    // asignar rol
    public void setRol(String rol) {
        this.rol = rol;
    }
}