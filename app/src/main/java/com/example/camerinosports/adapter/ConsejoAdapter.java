package com.example.camerinosports.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camerinosports.model.Consejo;

import java.util.ArrayList;

// adapta los datos para mostrarlos en una lista de la pantalla.
public class ConsejoAdapter extends RecyclerView.Adapter<ConsejoAdapter.ConsejoViewHolder> {

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public interface OnConsejoClickListener {
        void onSeleccionar(Consejo consejo);
        void onEliminar(Consejo consejo);
    }

    private Context context;
    private ArrayList<Consejo> consejos;
    private OnConsejoClickListener listener;
    private boolean modoAdmin;

    public ConsejoAdapter(Context context, ArrayList<Consejo> consejos, OnConsejoClickListener listener, boolean modoAdmin) {
        this.context = context;
        this.consejos = consejos;
        this.listener = listener;
        this.modoAdmin = modoAdmin;
    }

    @NonNull
    @Override
    public ConsejoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 18, 24, 18);

        TextView titulo = new TextView(context);
        TextView descripcion = new TextView(context);
        Button editar = new Button(context);
        Button eliminar = new Button(context);

        editar.setText("editar");
        eliminar.setText("eliminar");

        layout.addView(titulo);
        layout.addView(descripcion);
        layout.addView(editar);
        layout.addView(eliminar);

        return new ConsejoViewHolder(layout, titulo, descripcion, editar, eliminar);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsejoViewHolder holder, int position) {
        final Consejo consejo = consejos.get(position);

        holder.titulo.setText(consejo.getTitulo() + " | " + consejo.getCategoria());
        holder.descripcion.setText(consejo.getDescripcion());
        holder.editar.setVisibility(modoAdmin ? View.VISIBLE : View.GONE);
        holder.eliminar.setVisibility(modoAdmin ? View.VISIBLE : View.GONE);

        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onSeleccionar(consejo);
                }
            }
        });

        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onEliminar(consejo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return consejos.size();
    }

    public void actualizarLista(ArrayList<Consejo> nuevaLista) {
        consejos = nuevaLista;
        notifyDataSetChanged();
    }

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public static class ConsejoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView descripcion;
        Button editar;
        Button eliminar;

        public ConsejoViewHolder(@NonNull View itemView, TextView titulo, TextView descripcion, Button editar, Button eliminar) {
            super(itemView);
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.editar = editar;
            this.eliminar = eliminar;
        }
    }
}
