package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Promocion;
import com.example.camerinosports.repository.PromocionRepository;

import java.util.ArrayList;

public class PromocionViewModel {

    public interface ResultadoLista {
        void alCompletar(ArrayList<Promocion> promociones);
        void alFallar(String mensaje);
    }

    public interface ResultadoOperacion {
        void alCompletar(Promocion promocion);
        void alFallar(String mensaje);
    }

    private PromocionRepository promocionRepository;

    // constructor del viewmodel
    public PromocionViewModel(Context context) {
        promocionRepository = new PromocionRepository(context);
    }

    // listar promociones
    public ArrayList<Promocion> listarPromociones() {
        return promocionRepository.listarPromociones();
    }

    // listar activas
    public ArrayList<Promocion> listarPromocionesActivas() {
        return promocionRepository.listarPromocionesActivas();
    }

    public int contarPromocionesActivas() {
        return promocionRepository.contarPromocionesActivas();
    }

    // obtener por producto
    public Promocion obtenerPromocionPorProducto(int productoId) {
        return promocionRepository.obtenerPromocionPorProducto(productoId);
    }

    public void listarPromocionesApi(boolean soloActivas, final ResultadoLista resultado) {
        promocionRepository.listarPromocionesServicio(soloActivas, new PromocionRepository.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Promocion> promociones) {
                resultado.alCompletar(promociones);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void guardarPromocionApi(Promocion promocion, final ResultadoOperacion resultado) {
        if (!promocionValida(promocion)) {
            resultado.alFallar("Revisa los datos de la promoción");
            return;
        }

        PromocionRepository.ResultadoOperacion adaptador = adaptar(resultado);
        if (promocion.getId() > 0) {
            promocionRepository.actualizarPromocionServicio(promocion, adaptador);
        } else {
            promocionRepository.registrarPromocionServicio(promocion, adaptador);
        }
    }

    public void eliminarPromocionApi(Promocion promocion, final ResultadoOperacion resultado) {
        if (promocion == null || promocion.getId() <= 0) {
            resultado.alFallar("Promoción no válida");
            return;
        }
        promocionRepository.eliminarPromocionServicio(promocion, adaptar(resultado));
    }

    public void obtenerPromocionApi(int productoId, final ResultadoOperacion resultado) {
        if (productoId < 0) {
            resultado.alCompletar(null);
            return;
        }
        promocionRepository.obtenerPromocionServicio(productoId, adaptar(resultado));
    }

    private PromocionRepository.ResultadoOperacion adaptar(final ResultadoOperacion resultado) {
        return new PromocionRepository.ResultadoOperacion() {
            @Override
            public void alCompletar(Promocion promocion) {
                resultado.alCompletar(promocion);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        };
    }

    private boolean promocionValida(Promocion promocion) {
        if (promocion == null) {
            return false;
        }

        // productos usa identificadores manuales; el id 0 es valido.
        if (promocion.getProductoId() < 0) {
            return false;
        }

        if (promocion.getPrecioOriginal() <= 0 || promocion.getPrecioOferta() <= 0) {
            return false;
        }

        if (promocion.getPrecioOferta() >= promocion.getPrecioOriginal()) {
            return false;
        }

        if (textoVacio(promocion.getFechaInicio()) || textoVacio(promocion.getFechaFin())) {
            return false;
        }

        return promocion.getFechaFin().compareTo(promocion.getFechaInicio()) >= 0;
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
