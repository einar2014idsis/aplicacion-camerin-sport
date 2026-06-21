package com.example.camerinosports.repository;

import android.content.Context;
import android.util.Log;

import com.example.camerinosports.model.Usuario;
import com.example.camerinosports.network.ApiCliente;
import com.example.camerinosports.network.UsuarioApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    private static final String TAG = "UsuarioRepository";

    public interface ResultadoUsuario {
        void alCompletar(Usuario usuario);
        void alFallar(String mensaje);
    }

    public interface ResultadoConteo {
        void alCompletar(int total);
        void alFallar(String mensaje);
    }

    // datos temporales recibidos del servidor
    private static final ArrayList<Usuario> cacheUsuarios = new ArrayList<>();
    private UsuarioApi usuarioApi;

    // constructor del repository
    public UsuarioRepository(Context context) {
        usuarioApi = ApiCliente.obtener(context).create(UsuarioApi.class);
    }

    public boolean existeCorreo(String correo) {
        return obtenerUsuarioPorCorreo(correo) != null;
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        if (correo == null) {
            return null;
        }
        for (Usuario usuario : cacheUsuarios) {
            if (correo.equalsIgnoreCase(usuario.getCorreo())) {
                return usuario;
            }
        }
        return null;
    }

    public int contarClientes() {
        int total = 0;
        for (Usuario usuario : cacheUsuarios) {
            if ("cliente".equalsIgnoreCase(usuario.getRol())) {
                total++;
            }
        }
        return total;
    }

    public void iniciarSesionServicio(String correo, String password, final ResultadoUsuario resultado) {
        Usuario credenciales = new Usuario();
        credenciales.setCorreo(correo);
        credenciales.setPassword(password);

        usuarioApi.iniciarSesion(credenciales).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar(response.body());
                    return;
                }

                resultado.alFallar(response.code() == 401
                        ? "Correo o contraseña incorrectos"
                        : "No se pudo iniciar sesión");
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "Error conectando con " + call.request().url(), t);
                resultado.alFallar(mensajeConexion(t));
            }
        });
    }

    public void registrarUsuarioServicio(final Usuario usuario, final ResultadoUsuario resultado) {
        usuarioApi.registrarUsuario(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar(response.body());
                    return;
                }

                resultado.alFallar(response.code() == 409
                        ? "Este correo ya está registrado"
                        : "No se pudo crear la cuenta");
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "Error conectando con " + call.request().url(), t);
                resultado.alFallar(mensajeConexion(t));
            }
        });
    }

    public void obtenerUsuarioServicio(String correo, final ResultadoUsuario resultado) {
        usuarioApi.obtenerPorCorreo(correo).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar(response.body());
                    return;
                }

                resultado.alFallar("No se encontró el usuario");
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void actualizarUsuarioServicio(final Usuario usuario, final ResultadoUsuario resultado) {
        usuarioApi.actualizarUsuario(usuario.getCorreo(), usuario).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    guardarEnCache(usuario);
                    resultado.alCompletar(usuario);
                    return;
                }

                resultado.alFallar("No se pudo actualizar el perfil");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void contarClientesServicio(final ResultadoConteo resultado) {
        usuarioApi.contarClientes().enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultado.alCompletar(response.body());
                    return;
                }
                resultado.alFallar("No se pudo obtener el total de clientes");
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void listarUsuariosServicio(final ResultadoConteo resultado) {
        usuarioApi.listarUsuarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cacheUsuarios.clear();
                    cacheUsuarios.addAll(response.body());
                    resultado.alCompletar(contarClientes());
                    return;
                }
                resultado.alFallar("No se pudieron cargar los usuarios");
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    private static void guardarEnCache(Usuario usuario) {
        for (int i = cacheUsuarios.size() - 1; i >= 0; i--) {
            if (usuario.getCorreo() != null &&
                    usuario.getCorreo().equalsIgnoreCase(cacheUsuarios.get(i).getCorreo())) {
                cacheUsuarios.remove(i);
            }
        }
        cacheUsuarios.add(usuario);
    }

    private static String mensajeConexion(Throwable error) {
        String detalle = error.getLocalizedMessage();
        if (detalle == null || detalle.trim().isEmpty()) {
            detalle = error.getClass().getSimpleName();
        }
        return "No se pudo conectar con el servidor: " + detalle;
    }
}
