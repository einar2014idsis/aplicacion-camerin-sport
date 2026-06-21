package com.example.camerinosports.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camerinosports.model.Promocion;

import java.util.ArrayList;

// adapta los datos para mostrarlos en una lista de la pantalla.
public class PromocionAdapter extends RecyclerView.Adapter<PromocionAdapter.PromocionViewHolder> {

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public interface OnPromocionClickListener {
        void onSeleccionar(Promocion promocion);
        void onEliminar(Promocion promocion);
    }

    private Context context;
    private ArrayList<Promocion> promociones;
    private OnPromocionClickListener listener;

    public PromocionAdapter(Context context, ArrayList<Promocion> promociones, OnPromocionClickListener listener) {
        this.context = context;
        this.promociones = promociones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PromocionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 18, 24, 18);

        TextView titulo = new TextView(context);
        TextView detalle = new TextView(context);
        Button editar = new Button(context);
        Button eliminar = new Button(context);

        editar.setText("editar");
        eliminar.setText("eliminar");

        layout.addView(titulo);
        layout.addView(detalle);
        layout.addView(editar);
        layout.addView(eliminar);

        return new PromocionViewHolder(layout, titulo, detalle, editar, eliminar);
    }

    @Override
    public void onBindViewHolder(@NonNull PromocionViewHolder holder, int position) {
        final Promocion promocion = promociones.get(position);

        holder.titulo.setText(promocion.getNombreProducto());
        holder.detalle.setText("antes S/ " + String.format("%.2f", promocion.getPrecioOriginal()) +
                " | oferta S/ " + String.format("%.2f", promocion.getPrecioOferta()) +
                " | hasta " + promocion.getFechaFin());

        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSeleccionar(promocion);
            }
        });

        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEliminar(promocion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promociones.size();
    }

    public void actualizarLista(ArrayList<Promocion> nuevaLista) {
        promociones = nuevaLista;
        notifyDataSetChanged();
    }

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public static class PromocionViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView detalle;
        Button editar;
        Button eliminar;

        public PromocionViewHolder(@NonNull View itemView, TextView titulo, TextView detalle, Button editar, Button eliminar) {
            super(itemView);
            this.titulo = titulo;
            this.detalle = detalle;
            this.editar = editar;
            this.eliminar = eliminar;
        }
    }
}
