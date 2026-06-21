package com.example.camerinosports.network;

import com.example.camerinosports.model.Consejo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ConsejoApi {

    @GET("Consejos")
    Call<List<Consejo>> listarConsejos();

    @GET("Consejos/activos")
    Call<List<Consejo>> listarConsejosActivos();

    @POST("Consejos")
    Call<Consejo> registrarConsejo(@Body Consejo consejo);

    @PUT("Consejos/{id}")
    Call<Void> actualizarConsejo(@Path("id") int id, @Body Consejo consejo);

    @DELETE("Consejos/{id}")
    Call<Void> eliminarConsejo(@Path("id") int id);
}
