package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.repository.UsuarioRepository;
import com.example.camerinosports.util.Validaciones;

public class UsuarioViewModel {

    public interface ResultadoUsuario {
        void alCompletar(Usuario usuario);
        void alFallar(String mensaje);
    }

    public interface ResultadoConteo {
        void alCompletar(int total);
        void alFallar(String mensaje);
    }

    private UsuarioRepository usuarioRepository;

    // constructor del viewmodel
    public UsuarioViewModel(Context context) {
        usuarioRepository = new UsuarioRepository(context);
    }

    // verificar si el correo ya existe
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existeCorreo(correo);
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }

        return usuarioRepository.obtenerUsuarioPorCorreo(correo.trim());
    }

    public int contarClientes() {
        return usuarioRepository.contarClientes();
    }

    public void contarClientesApi(final ResultadoConteo resultado) {
        usuarioRepository.contarClientesServicio(new UsuarioRepository.ResultadoConteo() {
            @Override
            public void alCompletar(int total) {
                resultado.alCompletar(total);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void iniciarSesion(String correo, String password, final ResultadoUsuario resultado) {
        if (correo == null || correo.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            resultado.alFallar("Completa correo y contraseña");
            return;
        }
        if (!Validaciones.correo(correo)) {
            resultado.alFallar("El correo no tiene un formato válido");
            return;
        }

        usuarioRepository.iniciarSesionServicio(correo.trim(), password.trim(), adaptar(resultado));
    }

    public void registrarUsuarioApi(Usuario usuario, final ResultadoUsuario resultado) {
        if (!usuarioValido(usuario) || !Validaciones.password(usuario.getPassword())) {
            resultado.alFallar("Revisa los datos ingresados");
            return;
        }

        usuarioRepository.registrarUsuarioServicio(usuario, adaptar(resultado));
    }

    public void obtenerUsuarioApi(String correo, final ResultadoUsuario resultado) {
        if (correo == null || correo.trim().isEmpty()) {
            resultado.alFallar("No hay una sesión activa");
            return;
        }

        usuarioRepository.obtenerUsuarioServicio(correo.trim(), adaptar(resultado));
    }

    public void actualizarUsuarioApi(Usuario usuario, final ResultadoUsuario resultado) {
        if (!usuarioValido(usuario)) {
            resultado.alFallar("Revisa los datos ingresados");
            return;
        }

        usuarioRepository.actualizarUsuarioServicio(usuario, adaptar(resultado));
    }

    private boolean usuarioValido(Usuario usuario) {
        return usuario != null &&
                Validaciones.nombrePersona(usuario.getNombre()) &&
                Validaciones.nombrePersona(usuario.getApellidos()) &&
                Validaciones.correo(usuario.getCorreo()) &&
                (usuario.getPassword() == null || usuario.getPassword().isEmpty() ||
                        Validaciones.password(usuario.getPassword()));
    }

    private UsuarioRepository.ResultadoUsuario adaptar(final ResultadoUsuario resultado) {
        return new UsuarioRepository.ResultadoUsuario() {
            @Override
            public void alCompletar(Usuario usuario) {
                resultado.alCompletar(usuario);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        };
    }
}
