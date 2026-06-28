package com.example.camerinosports.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface HealthApi {
    @GET("Health")
    Call<ResponseBody> verificar();
}
