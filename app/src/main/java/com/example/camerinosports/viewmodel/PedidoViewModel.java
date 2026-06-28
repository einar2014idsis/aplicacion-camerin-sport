package com.example.camerinosports.viewmodel;

import android.content.Context;

import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.PedidoDetalle;
import com.example.camerinosports.repository.PedidoRepository;
import com.example.camerinosports.util.Validaciones;

import java.util.ArrayList;

public class PedidoViewModel {

    public interface ResultadoPedido {
        void alCompletar(Pedido pedido);
        void alFallar(String mensaje);
    }

    public interface ResultadoLista {
        void alCompletar(ArrayList<Pedido> pedidos);
        void alFallar(String mensaje);
    }

    public interface ResultadoDetalles {
        void alCompletar(ArrayList<PedidoDetalle> detalles);
        void alFallar(String mensaje);
    }

    public interface ResultadoOperacion {
        void alCompletar();
        void alFallar(String mensaje);
    }

    private PedidoRepository pedidoRepository;

    // constructor del viewmodel
    public PedidoViewModel(Context context) {
        pedidoRepository = new PedidoRepository(context);
    }
// listar pedidos
    public ArrayList<Pedido> listarPedidos() {
        return pedidoRepository.listarPedidos();
    }

    // obtener pedido por id
    public Pedido obtenerPedidoPorId(int id) {
        return pedidoRepository.obtenerPedidoPorId(id);
    }

    // listar detalles
    public ArrayList<PedidoDetalle> listarDetallesPedido(int pedidoId) {
        return pedidoRepository.listarDetallesPedido(pedidoId);
    }
    public void registrarPedidoApi(Pedido pedido, final ResultadoPedido resultado) {
        if (!pedidoValido(pedido)) {
            resultado.alFallar("Revisa los datos del pedido");
            return;
        }

        pedidoRepository.registrarPedidoServicio(pedido, new PedidoRepository.ResultadoPedido() {
            @Override
            public void alCompletar(Pedido pedido) {
                resultado.alCompletar(pedido);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void registrarPedidoCulqiApi(Pedido pedido, final ResultadoPedido resultado) {
        if (!pedidoValido(pedido)) {
            resultado.alFallar("Revisa los datos del pedido");
            return;
        }

        pedidoRepository.registrarPedidoCulqiServicio(pedido, new PedidoRepository.ResultadoPedido() {
            @Override
            public void alCompletar(Pedido pedido) {
                resultado.alCompletar(pedido);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void listarPedidosApi(String correo, final ResultadoLista resultado) {
        pedidoRepository.listarPedidosServicio(correo, new PedidoRepository.ResultadoLista() {
            @Override
            public void alCompletar(ArrayList<Pedido> pedidos) {
                resultado.alCompletar(pedidos);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void obtenerPedidoApi(int id, final ResultadoPedido resultado) {
        if (id <= 0) {
            resultado.alFallar("Pedido no válido");
            return;
        }
        pedidoRepository.obtenerPedidoServicio(id, new PedidoRepository.ResultadoPedido() {
            @Override
            public void alCompletar(Pedido pedido) {
                resultado.alCompletar(pedido);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void listarDetallesApi(int pedidoId, final ResultadoDetalles resultado) {
        pedidoRepository.listarDetallesServicio(pedidoId, new PedidoRepository.ResultadoDetalles() {
            @Override
            public void alCompletar(ArrayList<PedidoDetalle> detalles) {
                resultado.alCompletar(detalles);
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        });
    }

    public void cambiarEstadoApi(int pedidoId, String estado, final ResultadoOperacion resultado) {
        if (!estadoPermitido(estado)) {
            resultado.alFallar("Estado no permitido");
            return;
        }
        pedidoRepository.cambiarEstadoServicio(pedidoId, estado, adaptar(resultado));
    }

    public void cancelarPedidoApi(int pedidoId, final ResultadoOperacion resultado) {
        pedidoRepository.cancelarPedidoServicio(pedidoId, adaptar(resultado));
    }

    public void eliminarPedidoApi(int pedidoId, final ResultadoOperacion resultado) {
        pedidoRepository.eliminarPedidoServicio(pedidoId, adaptar(resultado));
    }

    private boolean pedidoValido(Pedido pedido) {
        return pedido != null &&
                Validaciones.nombrePersona(pedido.getCliente()) &&
                Validaciones.telefonoPeru(pedido.getTelefono()) &&
                pedido.getDireccion() != null &&
                pedido.getDireccion().trim().length() >= 5 &&
                pedido.getDetalles() != null &&
                !pedido.getDetalles().isEmpty() &&
                detallesValidos(pedido.getDetalles()) &&
                pedido.getTotal() > 0 &&
                !textoVacio(pedido.getMetodoPago()) &&
                !textoVacio(pedido.getEstadoPago());
    }

    private boolean detallesValidos(ArrayList<PedidoDetalle> detalles) {
        for (int i = 0; i < detalles.size(); i++) {
            PedidoDetalle detalle = detalles.get(i);
            if (detalle.getProductoId() < 0 ||
                    detalle.getCantidad() <= 0 ||
                    detalle.getPrecio() <= 0 ||
                    detalle.getSubtotal() <= 0) {
                return false;
            }
        }

        return true;
    }

    private PedidoRepository.ResultadoOperacion adaptar(final ResultadoOperacion resultado) {
        return new PedidoRepository.ResultadoOperacion() {
            @Override
            public void alCompletar() {
                resultado.alCompletar();
            }

            @Override
            public void alFallar(String mensaje) {
                resultado.alFallar(mensaje);
            }
        };
    }

    private boolean estadoPermitido(String estado) {
        return "Pedido confirmado".equals(estado) ||
                "Preparando pedido".equals(estado) ||
                "En camino".equals(estado) ||
                "Entregado".equals(estado) ||
                "Cancelado".equals(estado);
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
