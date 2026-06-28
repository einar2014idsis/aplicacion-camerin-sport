package com.example.camerinosports.repository;

import android.content.Context;

import java.util.Locale;

public class CulqiRepository {
    public interface ResultadoToken {
        void alCompletar(String token);
        void alFallar(String mensaje);
    }

    public CulqiRepository(Context context) {
    }

    public void crearTokenTarjeta(String numero, String cvv, String mes, String anio,
                                  String correo, final ResultadoToken resultado) {
        resultado.alCompletar(crearTokenSimulado("tkn_sim"));
    }

    public void crearTokenYape(String telefono, String codigoAprobacion, int montoCentimos,
                               final ResultadoToken resultado) {
        resultado.alCompletar(crearTokenSimulado("ype_sim"));
    }

    private String crearTokenSimulado(String prefijo) {
        return String.format(Locale.ROOT, "%s_%d", prefijo, System.currentTimeMillis());
    }
}
