package com.example.camerinosports.network;

import com.example.camerinosports.model.Pedido;
import com.example.camerinosports.model.PedidoDetalle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PedidoApi {

    @GET("Pedidos")
    Call<List<Pedido>> listarPedidos();

    @GET("Pedidos/{id}")
    Call<Pedido> obtenerPedido(@Path("id") int id);

    @GET("Pedidos/correo/{correo}")
    Call<List<Pedido>> listarPedidosPorCorreo(@Path("correo") String correo);

    @GET("Pedidos/{id}/detalles")
    Call<List<PedidoDetalle>> listarDetalles(@Path("id") int id);

    @POST("Pedidos")
    Call<Pedido> registrarPedido(@Body Pedido pedido);

    @POST("Pedidos/culqi")
    Call<Pedido> registrarPedidoCulqi(@Body Pedido pedido);

    @PUT("Pedidos/{id}/estado")
    Call<Void> cambiarEstado(@Path("id") int id, @Body EstadoPedidoRequest estado);

    @PUT("Pedidos/{id}/cancelar")
    Call<Void> cancelarPedido(@Path("id") int id);

    @DELETE("Pedidos/{id}")
    Call<Void> eliminarPedido(@Path("id") int id);

    class EstadoPedidoRequest {
        private final String estado;

        public EstadoPedidoRequest(String estado) {
            this.estado = estado;
        }

        public String getEstado() {
            return estado;
        }
    }
}
