package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.repository.UsuarioRepository;

public class UsuarioViewModel {

    private UsuarioRepository usuarioRepository;

    // constructor del viewmodel
    public UsuarioViewModel(Context context) {
        usuarioRepository = new UsuarioRepository(context);
    }

    // registrar un nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {

        if (usuario.getNombre().trim().isEmpty()) {
            return false;
        }

        if (usuario.getApellidos().trim().isEmpty()) {
            return false;
        }

        if (usuario.getCorreo().trim().isEmpty()) {
            return false;
        }

        if (usuario.getPassword().trim().isEmpty()) {
            return false;
        }

        if (usuarioRepository.existeCorreo(usuario.getCorreo())) {
            return false;
        }

        return usuarioRepository.registrarUsuario(usuario);
    }

    // validar inicio de sesión
    public boolean validarLogin(String correo, String password) {
        return usuarioRepository.validarLogin(correo, password);
    }

    // obtener rol del usuario
    public String obtenerRol(String correo) {
        return usuarioRepository.obtenerRol(correo);
    }

    // verificar si el correo ya existe
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existeCorreo(correo);
    }
}