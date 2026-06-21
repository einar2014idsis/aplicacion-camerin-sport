package com.example.camerinosports.repository;

import android.content.Context;

import com.example.camerinosports.model.Consejo;
import com.example.camerinosports.network.ApiCliente;
import com.example.camerinosports.network.ConsejoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsejoRepository {

    public interface ResultadoLista {
        void alCompletar(ArrayList<Consejo> consejos);
        void alFallar(String mensaje);
    }

    public interface ResultadoOperacion {
        void alCompletar(Consejo consejo);
        void alFallar(String mensaje);
    }

    // datos temporales recibidos del servidor
    private static final ArrayList<Consejo> cacheConsejos = new ArrayList<>();
    private ConsejoApi consejoApi;

    // constructor del repository
    public ConsejoRepository(Context context) {
        consejoApi = ApiCliente.obtener(context).create(ConsejoApi.class);
    }

    public ArrayList<Consejo> listarConsejos() {
        return new ArrayList<>(cacheConsejos);
    }

    // listar consejos activos
    public ArrayList<Consejo> listarConsejosActivos() {
        ArrayList<Consejo> activos = new ArrayList<>();
        for (Consejo consejo : cacheConsejos) {
            if (consejo.isActivo()) {
                activos.add(consejo);
            }
        }
        return activos;
    }

    public void listarConsejosServicio(boolean soloActivos, final ResultadoLista resultado) {
        Call<List<Consejo>> llamada = soloActivos
                ? consejoApi.listarConsejosActivos()
                : consejoApi.listarConsejos();

        llamada.enqueue(new Callback<List<Consejo>>() {
            @Override
            public void onResponse(Call<List<Consejo>> call, Response<List<Consejo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reemplazarCache(response.body(), soloActivos);
                    resultado.alCompletar(new ArrayList<>(response.body()));
                    return;
                }
                resultado.alFallar("No se pudieron cargar los consejos");
            }

            @Override
            public void onFailure(Call<List<Consejo>> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void registrarConsejoServicio(Consejo consejo, final ResultadoOperacion resultado) {
        consejoApi.registrarConsejo(consejo).enqueue(new Callback<Consejo>() {
            @Override
            public void onResponse(Call<Consejo> call, Response<Consejo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar(response.body());
                    return;
                }
                resultado.alFallar("No se pudo guardar el consejo");
            }

            @Override
            public void onFailure(Call<Consejo> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void actualizarConsejoServicio(final Consejo consejo, final ResultadoOperacion resultado) {
        consejoApi.actualizarConsejo(consejo.getId(), consejo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    guardarEnCache(consejo);
                    resultado.alCompletar(consejo);
                    return;
                }
                resultado.alFallar("No se pudo actualizar el consejo");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void eliminarConsejoServicio(final Consejo consejo, final ResultadoOperacion resultado) {
        consejoApi.eliminarConsejo(consejo.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    eliminarDeCache(consejo.getId());
                    resultado.alCompletar(consejo);
                    return;
                }
                resultado.alFallar("No se pudo eliminar el consejo");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    private static void reemplazarCache(List<Consejo> consejos, boolean soloActivos) {
        cacheConsejos.clear();
        cacheConsejos.addAll(consejos);
    }

    private static void guardarEnCache(Consejo consejo) {
        eliminarDeCache(consejo.getId());
        cacheConsejos.add(consejo);
    }

    private static void eliminarDeCache(int id) {
        for (int i = cacheConsejos.size() - 1; i >= 0; i--) {
            if (cacheConsejos.get(i).getId() == id) {
                cacheConsejos.remove(i);
            }
        }
    }
}
