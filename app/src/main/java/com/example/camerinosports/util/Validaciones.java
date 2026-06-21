package com.example.camerinosports.util;

import android.util.Patterns;

import java.util.Calendar;

public final class Validaciones {

    private Validaciones() {
    }

    public static boolean nombrePersona(String valor) {
        return valor != null &&
                valor.trim().length() >= 2 &&
                valor.trim().matches("[\\p{L}]+(?:[ '\\-][\\p{L}]+)*");
    }

    public static boolean correo(String valor) {
        if (valor == null) {
            return false;
        }

        String correo = valor.trim();
        return correo.length() <= 180 &&
                !correo.contains(" ") &&
                Patterns.EMAIL_ADDRESS.matcher(correo).matches() &&
                correo.substring(correo.lastIndexOf('@') + 1).contains(".");
    }

    public static boolean password(String valor) {
        return valor != null && valor.length() >= 6 && valor.length() <= 100;
    }

    public static boolean telefonoPeru(String valor) {
        return valor != null && valor.trim().matches("9\\d{8}");
    }

    public static boolean categoriaProducto(String valor) {
        return "Calzado".equalsIgnoreCase(valor) ||
                "Ropa".equalsIgnoreCase(valor) ||
                "Accesorios".equalsIgnoreCase(valor);
    }

    public static boolean nombreProducto(String valor) {
        return valor != null &&
                valor.trim().length() >= 2 &&
                valor.trim().length() <= 100 &&
                valor.trim().matches("[\\p{L}\\d]+(?:[ .,'\\-/][\\p{L}\\d]+)*");
    }

    public static boolean colorProducto(String valor) {
        return valor == null || valor.trim().isEmpty() ||
                valor.trim().matches("[\\p{L}]+(?:[ /\\-][\\p{L}]+)*");
    }

    public static boolean tallaProducto(String categoria, String talla) {
        String valor = talla == null ? "" : talla.trim().toUpperCase();

        if ("Accesorios".equalsIgnoreCase(categoria)) {
            return valor.length() <= 30;
        }

        if ("Ropa".equalsIgnoreCase(categoria)) {
            return valor.matches("XXS|XS|S|M|L|XL|XXL|XXXL|ÚNICA|UNICA");
        }

        if ("Calzado".equalsIgnoreCase(categoria)) {
            if (!valor.matches("\\d{2}(\\.5)?")) {
                return false;
            }
            double tallaNumero = Double.parseDouble(valor);
            return tallaNumero >= 20 && tallaNumero <= 50;
        }

        return false;
    }

    public static boolean numeroTarjeta(String valor) {
        if (valor == null) {
            return false;
        }

        String numero = valor.replaceAll("\\s", "");
        if (!numero.matches("\\d{13,19}")) {
            return false;
        }

        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }

        return suma % 10 == 0;
    }

    public static boolean fechaTarjeta(String valor) {
        if (valor == null || !valor.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        int mes = Integer.parseInt(valor.substring(0, 2));
        int anio = 2000 + Integer.parseInt(valor.substring(3, 5));
        if (mes < 1 || mes > 12) {
            return false;
        }

        Calendar ahora = Calendar.getInstance();
        int anioActual = ahora.get(Calendar.YEAR);
        int mesActual = ahora.get(Calendar.MONTH) + 1;
        return anio > anioActual || (anio == anioActual && mes >= mesActual);
    }
}
