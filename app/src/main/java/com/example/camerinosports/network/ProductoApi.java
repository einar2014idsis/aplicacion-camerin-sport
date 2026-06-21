package com.example.camerinosports.network;

import com.example.camerinosports.model.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductoApi {

    @GET("Productos")
    Call<List<Producto>> listarProductos();

    @POST("Productos")
    Call<Producto> registrarProducto(@Body Producto producto);

    @PUT("Productos/{id}")
    Call<Void> actualizarProducto(@Path("id") int id, @Body Producto producto);

    @DELETE("Productos/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);
}
