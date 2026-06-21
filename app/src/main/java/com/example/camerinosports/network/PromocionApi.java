package com.example.camerinosports.network;

import com.example.camerinosports.model.Promocion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PromocionApi {

    @GET("Promociones")
    Call<List<Promocion>> listarPromociones();

    @GET("Promociones/activas")
    Call<List<Promocion>> listarPromocionesActivas();

    @GET("Promociones/producto/{productoId}")
    Call<Promocion> obtenerPorProducto(@Path("productoId") int productoId);

    @POST("Promociones")
    Call<Promocion> registrarPromocion(@Body Promocion promocion);

    @PUT("Promociones/{id}")
    Call<Void> actualizarPromocion(@Path("id") int id, @Body Promocion promocion);

    @DELETE("Promociones/{id}")
    Call<Void> eliminarPromocion(@Path("id") int id);
}
