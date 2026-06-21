package com.example.camerinosports.repository;

import android.content.Context;

import com.example.camerinosports.model.Promocion;
import com.example.camerinosports.network.ApiCliente;
import com.example.camerinosports.network.PromocionApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class PromocionRepository {

    public interface ResultadoLista {
        void alCompletar(ArrayList<Promocion> promociones);
        void alFallar(String mensaje);
    }

    public interface ResultadoOperacion {
        void alCompletar(Promocion promocion);
        void alFallar(String mensaje);
    }

    // datos temporales recibidos del servidor
    private static final ArrayList<Promocion> cachePromociones = new ArrayList<>();
    private PromocionApi promocionApi;

    // constructor del repository
    public PromocionRepository(Context context) {
        promocionApi = ApiCliente.obtener(context).create(PromocionApi.class);
    }

    public ArrayList<Promocion> listarPromociones() {
        return new ArrayList<>(cachePromociones);
    }

    public ArrayList<Promocion> listarPromocionesActivas() {
        ArrayList<Promocion> activas = new ArrayList<>();
        for (Promocion promocion : cachePromociones) {
            if (promocion.isActiva()) {
                activas.add(promocion);
            }
        }
        return activas;
    }

    public int contarPromocionesActivas() {
        return listarPromocionesActivas().size();
    }

    // obtener promocion por producto
    public Promocion obtenerPromocionPorProducto(int productoId) {
        for (Promocion promocion : cachePromociones) {
            if (promocion.getProductoId() == productoId && promocion.isActiva()) {
                return promocion;
            }
        }
        return null;
    }

    public void listarPromocionesServicio(boolean soloActivas, final ResultadoLista resultado) {
        Call<List<Promocion>> llamada = soloActivas
                ? promocionApi.listarPromocionesActivas()
                : promocionApi.listarPromociones();

        llamada.enqueue(new Callback<List<Promocion>>() {
            @Override
            public void onResponse(Call<List<Promocion>> call, Response<List<Promocion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reemplazarCache(response.body(), soloActivas);
                    resultado.alCompletar(new ArrayList<>(response.body()));
                    return;
                }
                resultado.alFallar("No se pudieron cargar las promociones");
            }

            @Override
            public void onFailure(Call<List<Promocion>> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void registrarPromocionServicio(Promocion promocion, final ResultadoOperacion resultado) {
        promocionApi.registrarPromocion(promocion).enqueue(new Callback<Promocion>() {
            @Override
            public void onResponse(Call<Promocion> call, Response<Promocion> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar(response.body());
                    return;
                }
                resultado.alFallar(leerError(response.errorBody(), "No se pudo guardar la promoción"));
            }

            @Override
            public void onFailure(Call<Promocion> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void obtenerPromocionServicio(int productoId, final ResultadoOperacion resultado) {
        promocionApi.obtenerPorProducto(productoId).enqueue(new Callback<Promocion>() {
            @Override
            public void onResponse(Call<Promocion> call, Response<Promocion> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultado.alCompletar(response.body());
                    return;
                }

                if (response.code() == 404) {
                    resultado.alCompletar(null);
                    return;
                }

                resultado.alFallar("No se pudo consultar la promoción");
            }

            @Override
            public void onFailure(Call<Promocion> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void actualizarPromocionServicio(final Promocion promocion, final ResultadoOperacion resultado) {
        promocionApi.actualizarPromocion(promocion.getId(), promocion).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    guardarEnCache(promocion);
                    resultado.alCompletar(promocion);
                    return;
                }
                resultado.alFallar(leerError(response.errorBody(), "No se pudo actualizar la promoción"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void eliminarPromocionServicio(final Promocion promocion, final ResultadoOperacion resultado) {
        promocionApi.eliminarPromocion(promocion.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    eliminarDeCache(promocion.getId());
                    resultado.alCompletar(promocion);
                    return;
                }
                resultado.alFallar("No se pudo eliminar la promoción");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    private static void reemplazarCache(List<Promocion> promociones, boolean soloActivas) {
        cachePromociones.clear();
        cachePromociones.addAll(promociones);
    }

    private static void guardarEnCache(Promocion promocion) {
        eliminarDeCache(promocion.getId());
        cachePromociones.add(promocion);
    }

    private static void eliminarDeCache(int id) {
        for (int i = cachePromociones.size() - 1; i >= 0; i--) {
            if (cachePromociones.get(i).getId() == id) {
                cachePromociones.remove(i);
            }
        }
    }

    private String leerError(ResponseBody errorBody, String mensajePredeterminado) {
        if (errorBody == null) {
            return mensajePredeterminado;
        }

        try {
            String mensaje = errorBody.string().trim();
            return mensaje.isEmpty() ? mensajePredeterminado : mensaje.replace("\"", "");
        } catch (Exception e) {
            return mensajePredeterminado;
        }
    }
}
