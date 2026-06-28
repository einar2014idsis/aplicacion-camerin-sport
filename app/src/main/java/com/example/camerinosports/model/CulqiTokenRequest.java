package com.example.camerinosports.model;

import com.google.gson.annotations.SerializedName;

public class CulqiTokenRequest {
    @SerializedName("card_number")
    private final String cardNumber;
    private final String cvv;
    @SerializedName("expiration_month")
    private final String expirationMonth;
    @SerializedName("expiration_year")
    private final String expirationYear;
    private final String email;

    public CulqiTokenRequest(String cardNumber, String cvv, String expirationMonth,
                             String expirationYear, String email) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.email = email;
    }
}
