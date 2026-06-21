package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Consejo;
import com.example.camerinosports.repository.ConsejoRepository;

import java.util.ArrayList;

public class ConsejoViewModel {

    public interface ResultadoLista {
        void alCompletar(ArrayList<Consejo> consejos);
        void alFallar(String mensaje);
    }

    public interface ResultadoOperacion {
        void alCompletar(Consejo consejo);
        void alFallar(String mensaje);
    }

    private ConsejoRepository consejoRepository;

    // constructor del viewmodel
    public ConsejoViewModel(Context context) {
        consejoRepository = new ConsejoRepository(context);
    }

    // listar todos los consejos
    public ArrayList<Consejo> listarConsejos() {
        return consejoRepository.listarConsejos();
    }

    // listar consejos activos
    public ArrayList<Consejo> listarConsejosActivos() {
        return consejoRepository.listarConsejosActivos();
    }

    public void listarConsejosApi(boolean soloActivos, final ResultadoLista resultado) {
        consejoRepository.listarConsejosServicio(soloActivos, new ConsejoRepository.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Consejo> consejos) {
                resultado.alCompletar(consejos);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void guardarConsejoApi(Consejo consejo, final ResultadoOperacion resultado) {
        if (!consejoValido(consejo)) {
            resultado.alFallar("Completa todos los campos");
            return;
        }

        ConsejoRepository.ResultadoOperacion adaptador = adaptar(resultado);
        if (consejo.getId() > 0) {
            consejoRepository.actualizarConsejoServicio(consejo, adaptador);
        } else {
            consejoRepository.registrarConsejoServicio(consejo, adaptador);
        }
    }

    public void eliminarConsejoApi(Consejo consejo, final ResultadoOperacion resultado) {
        if (consejo == null || consejo.getId() <= 0) {
            resultado.alFallar("Consejo no válido");
            return;
        }
        consejoRepository.eliminarConsejoServicio(consejo, adaptar(resultado));
    }

    private ConsejoRepository.ResultadoOperacion adaptar(final ResultadoOperacion resultado) {
        return new ConsejoRepository.ResultadoOperacion() {
            @Override
            public void alCompletar(Consejo consejo) {
                resultado.alCompletar(consejo);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        };
    }

    private boolean consejoValido(Consejo consejo) {
        if (consejo == null) {
            return false;
        }

        return !textoVacio(consejo.getTitulo()) &&
                !textoVacio(consejo.getDescripcion()) &&
                !textoVacio(consejo.getCategoria());
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
