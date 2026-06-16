package com.example.camerinosports;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cargar pantalla del administrador
        setContentView(R.layout.activity_admin);
    }
}