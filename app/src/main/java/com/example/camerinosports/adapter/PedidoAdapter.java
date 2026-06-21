package com.example.camerinosports.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camerinosports.model.Pedido;

import java.util.ArrayList;

// adapta los datos para mostrarlos en una lista de la pantalla.
public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public interface OnPedidoClickListener {
        void onVer(Pedido pedido);
        void onCambiarEstado(Pedido pedido);
        void onCancelar(Pedido pedido);
    }

    private Context context;
    private ArrayList<Pedido> pedidos;
    private OnPedidoClickListener listener;

    public PedidoAdapter(Context context, ArrayList<Pedido> pedidos, OnPedidoClickListener listener) {
        this.context = context;
        this.pedidos = pedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 18, 24, 18);

        TextView titulo = new TextView(context);
        TextView detalle = new TextView(context);
        TextView estado = new TextView(context);
        LinearLayout botones = new LinearLayout(context);
        botones.setOrientation(LinearLayout.HORIZONTAL);

        Button ver = new Button(context);
        Button estadoBtn = new Button(context);
        Button cancelar = new Button(context);

        ver.setText("ver");
        estadoBtn.setText("estado");
        cancelar.setText("cancelar");

        botones.addView(ver);
        botones.addView(estadoBtn);
        botones.addView(cancelar);
        layout.addView(titulo);
        layout.addView(detalle);
        layout.addView(estado);
        layout.addView(botones);

        return new PedidoViewHolder(layout, titulo, detalle, estado, ver, estadoBtn, cancelar);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        final Pedido pedido = pedidos.get(position);

        holder.titulo.setText("pedido #" + pedido.getId() + " - " + pedido.getCliente());
        holder.detalle.setText(pedido.getFecha() + " | total S/ " + String.format("%.2f", pedido.getTotal()));
        holder.estado.setText("estado: " + pedido.getEstado());

        holder.ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onVer(pedido);
            }
        });

        holder.estadoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCambiarEstado(pedido);
            }
        });

        holder.cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelar(pedido);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public void actualizarLista(ArrayList<Pedido> nuevaLista) {
        pedidos = nuevaLista;
        notifyDataSetChanged();
    }

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView detalle;
        TextView estado;
        Button ver;
        Button estadoBtn;
        Button cancelar;

        public PedidoViewHolder(@NonNull View itemView, TextView titulo, TextView detalle, TextView estado,
                                Button ver, Button estadoBtn, Button cancelar) {
            super(itemView);
            this.titulo = titulo;
            this.detalle = detalle;
            this.estado = estado;
            this.ver = ver;
            this.estadoBtn = estadoBtn;
            this.cancelar = cancelar;
        }
    }
}
