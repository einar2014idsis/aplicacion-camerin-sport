package com.example.camerinosports.model;

public class UsuarioSesion {
    private static String correoActual = "";
    private static String rolActual = "";

    private UsuarioSesion() {
    }

    public static void iniciarSesion(String correo, String rol) {
        correoActual = correo;
        rolActual = rol;
    }

    public static void cerrarSesion() {
        correoActual = "";
        rolActual = "";
        CarritoMemoria.vaciar();
    }

    public static String getCorreoActual() {
        return correoActual;
    }

    public static String getRolActual() {
        return rolActual;
    }

    public static boolean haySesion() {
        return correoActual != null && !correoActual.trim().isEmpty();
    }
}
