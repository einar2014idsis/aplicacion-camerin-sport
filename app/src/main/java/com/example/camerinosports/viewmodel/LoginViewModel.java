package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.repository.UsuarioRepository;

public class LoginViewModel {

    private UsuarioRepository usuarioRepository;

    // constructor del viewmodel
    public LoginViewModel(Context context) {
        usuarioRepository = new UsuarioRepository(context);
    }

    // iniciar sesión
    public boolean iniciarSesion(String correo, String contrasena) {

        // validar correo
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }

        // validar contraseña
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return false;
        }

        // validar usuario en sqlite
        return usuarioRepository.validarLogin(correo, contrasena);
    }

    // obtener rol del usuario
    public String obtenerRol(String correo) {
        return usuarioRepository.obtenerRol(correo);
    }
}