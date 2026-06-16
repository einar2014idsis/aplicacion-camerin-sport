package com.example.camerinosports.repository;

import android.content.Context;

import com.example.camerinosports.database.DatabaseHelper;
import com.example.camerinosports.model.Usuario;

public class UsuarioRepository {

    private DatabaseHelper databaseHelper;

    // constructor del repository
    public UsuarioRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    // registrar un nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {

        return databaseHelper.registrarUsuario(
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getPassword()
        );
    }

    // validar inicio de sesión
    public boolean validarLogin(String correo, String password) {
        return databaseHelper.validarLogin(correo, password);
    }

    // obtener rol del usuario
    public String obtenerRol(String correo) {
        return databaseHelper.obtenerRol(correo);
    }

    // verificar si el correo ya existe
    public boolean existeCorreo(String correo) {
        return databaseHelper.existeCorreo(correo);
    }
}