package com.example.camerinosports.network;

import com.example.camerinosports.model.CulqiTokenRequest;
import com.example.camerinosports.model.CulqiTokenResponse;
import com.example.camerinosports.model.CulqiYapeTokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CulqiApi {
    @POST("tokens/")
    Call<CulqiTokenResponse> crearToken(
            @Header("Authorization") String authorization,
            @Body CulqiTokenRequest request);

    @POST("tokens/yape/")
    Call<CulqiTokenResponse> crearTokenYape(
            @Header("Authorization") String authorization,
            @Body CulqiYapeTokenRequest request);

    @POST("tokens/")
    Call<CulqiTokenResponse> crearTokenYapeAlternativo(
            @Header("Authorization") String authorization,
            @Body CulqiYapeTokenRequest request);
}
