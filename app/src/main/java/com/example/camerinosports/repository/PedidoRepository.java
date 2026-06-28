package com.example.camerinosports.repository;

import android.content.Context;

import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.PedidoDetalle;
import com.example.camerinosports.network.ApiCliente;
import com.example.camerinosports.network.PedidoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class PedidoRepository {

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

    private static final ArrayList<Pedido> cachePedidos = new ArrayList<>();
    private PedidoApi pedidoApi;

    // constructor del repository
    public PedidoRepository(Context context) {
        pedidoApi = ApiCliente.obtener(context).create(PedidoApi.class);
    }
    public ArrayList<Pedido> listarPedidos() {
        return new ArrayList<>(cachePedidos);
    }

    // obtener pedido por id
    public Pedido obtenerPedidoPorId(int id) {
        return buscarEnCache(id);
    }

    // listar detalles de un pedido
    public ArrayList<PedidoDetalle> listarDetallesPedido(int pedidoId) {
        Pedido pedido = buscarEnCache(pedidoId);
        return pedido == null || pedido.getDetalles() == null
                ? new ArrayList<>()
                : new ArrayList<>(pedido.getDetalles());
    }
    public void registrarPedidoServicio(Pedido pedido, final ResultadoPedido resultado) {
        pedidoApi.registrarPedido(pedido).enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar(response.body());
                    return;
                }

                resultado.alFallar(leerError(
                        response.errorBody(),
                        response.code() == 409
                                ? "Hay productos sin stock suficiente"
                                : "No se pudo registrar el pedido"));
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable t) {
                resultado.alFallar("API no disponible. Verifica que el servicio C# esté ejecutándose");
            }
        });
    }

    public void registrarPedidoCulqiServicio(Pedido pedido, final ResultadoPedido resultado) {
        registrarPedidoServicio(pedido, resultado);
    }

    public void listarPedidosServicio(String correo, final ResultadoLista resultado) {
        // sin correo se cargan todos los pedidos; con correo solo los del cliente
        Call<List<Pedido>> llamada = correo == null || correo.trim().isEmpty()
                ? pedidoApi.listarPedidos()
                : pedidoApi.listarPedidosPorCorreo(correo.trim());

        llamada.enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // la copia local se reemplaza para no mezclar datos antiguos
                    cachePedidos.clear();
                    cachePedidos.addAll(response.body());
                    resultado.alCompletar(new ArrayList<>(response.body()));
                    return;
                }
                resultado.alFallar("No se pudieron cargar los pedidos");
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void obtenerPedidoServicio(int id, final ResultadoPedido resultado) {
        pedidoApi.obtenerPedido(id).enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarEnCache(response.body());
                    resultado.alCompletar(response.body());
                    return;
                }
                resultado.alFallar("Pedido no encontrado");
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void listarDetallesServicio(int pedidoId, final ResultadoDetalles resultado) {
        pedidoApi.listarDetalles(pedidoId).enqueue(new Callback<List<PedidoDetalle>>() {
            @Override
            public void onResponse(Call<List<PedidoDetalle>> call, Response<List<PedidoDetalle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultado.alCompletar(new ArrayList<>(response.body()));
                    return;
                }
                resultado.alFallar("No se pudieron cargar los detalles");
            }

            @Override
            public void onFailure(Call<List<PedidoDetalle>> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void cambiarEstadoServicio(int pedidoId, String estado, final ResultadoOperacion resultado) {
        pedidoApi.cambiarEstado(pedidoId, new PedidoApi.EstadoPedidoRequest(estado))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // tambien se actualiza la copia local para reflejar el cambio de inmediato
                            Pedido pedido = buscarEnCache(pedidoId);
                            if (pedido != null) {
                                pedido.setEstado(estado);
                            }
                            resultado.alCompletar();
                            return;
                        }
                        resultado.alFallar("No se pudo cambiar el estado");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        resultado.alFallar("No se pudo conectar con el servicio");
                    }
                });
    }

    public void cancelarPedidoServicio(int pedidoId, final ResultadoOperacion resultado) {
        pedidoApi.cancelarPedido(pedidoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Pedido pedido = buscarEnCache(pedidoId);
                    if (pedido != null) {
                        pedido.setEstado("Cancelado");
                    }
                    resultado.alCompletar();
                    return;
                }
                resultado.alFallar(response.code() == 409
                        ? "Solo se cancela un pedido confirmado"
                        : "No se pudo cancelar el pedido");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    public void eliminarPedidoServicio(int pedidoId, final ResultadoOperacion resultado) {
        pedidoApi.eliminarPedido(pedidoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    eliminarDeCache(pedidoId);
                    resultado.alCompletar();
                    return;
                }
                resultado.alFallar(response.code() == 404
                        ? "El pedido ya no existe"
                        : "No se pudo eliminar el pedido");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultado.alFallar("No se pudo conectar con el servicio");
            }
        });
    }

    private static Pedido buscarEnCache(int id) {
        for (Pedido pedido : cachePedidos) {
            if (pedido.getId() == id) {
                return pedido;
            }
        }
        return null;
    }

    private static void guardarEnCache(Pedido pedido) {
        eliminarDeCache(pedido.getId());
        cachePedidos.add(pedido);
    }

    private static void eliminarDeCache(int id) {
        for (int i = cachePedidos.size() - 1; i >= 0; i--) {
            if (cachePedidos.get(i).getId() == id) {
                cachePedidos.remove(i);
            }
        }
    }

    private String leerError(ResponseBody errorBody, String mensajePredeterminado) {
        // intenta mostrar el mensaje enviado por el servidor
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
