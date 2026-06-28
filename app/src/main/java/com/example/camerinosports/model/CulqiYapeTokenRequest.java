package com.example.camerinosports.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class CulqiYapeTokenRequest {
    @SerializedName("number_phone")
    private final String numberPhone;
    private final String otp;
    private final String amount;
    private final Map<String, String> metadata;

    public CulqiYapeTokenRequest(String numberPhone, String otp, String amount) {
        this.numberPhone = numberPhone;
        this.otp = otp;
        this.amount = amount;
        this.metadata = new HashMap<>();
        this.metadata.put("negocio", "Camerino Sports");
    }
}
