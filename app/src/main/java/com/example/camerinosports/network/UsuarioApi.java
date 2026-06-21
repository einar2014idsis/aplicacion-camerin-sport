package com.example.camerinosports.network;

import com.example.camerinosports.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioApi {

    @GET("Usuarios")
    Call<List<Usuario>> listarUsuarios();

    @GET("Usuarios/correo/{correo}")
    Call<Usuario> obtenerPorCorreo(@Path("correo") String correo);

    @GET("Usuarios/clientes/total")
    Call<Integer> contarClientes();

    @POST("Usuarios/registro")
    Call<Usuario> registrarUsuario(@Body Usuario usuario);

    @POST("Usuarios/login")
    Call<Usuario> iniciarSesion(@Body Usuario usuario);

    @PUT("Usuarios/correo/{correo}")
    Call<Void> actualizarUsuario(@Path("correo") String correo, @Body Usuario usuario);
}
