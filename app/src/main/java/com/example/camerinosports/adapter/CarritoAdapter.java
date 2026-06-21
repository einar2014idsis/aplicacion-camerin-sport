package com.example.camerinosports.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camerinosports.R;
import com.example.camerinosports.model.CarritoItem;

import java.util.ArrayList;

// adapta los datos para mostrarlos en una lista de la pantalla.
public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public interface OnCarritoClickListener {
        void onAumentar(CarritoItem item);
        void onDisminuir(CarritoItem item);
        void onEliminar(CarritoItem item);
    }

    private Context context;
    private ArrayList<CarritoItem> items;
    private OnCarritoClickListener listener;

    public CarritoAdapter(Context context, ArrayList<CarritoItem> items, OnCarritoClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        final CarritoItem item = items.get(position);
        holder.txtNombreProductoPedido.setText(item.getProducto().getNombre());
        holder.txtDetalleProductoPedido.setText("Talla: " + item.getProducto().getTalla() + " | Color: " + item.getProducto().getColor());
        holder.txtDisponiblePedido.setText("Stock: " + item.getProducto().getStock());
        holder.txtCantidadPedido.setText(String.valueOf(item.getCantidad()));
        holder.txtPrecioPedido.setText("S/ " + String.format("%.2f", item.getSubtotal()));

        mostrarImagen(holder.imgProductoPedido, item.getProducto().getImagen());

        holder.btnMasPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAumentar(item);
            }
        });

        holder.btnMenosPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDisminuir(item);
            }
        });

        holder.btnEliminarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEliminar(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void actualizar() {
        notifyDataSetChanged();
    }

    private int obtenerImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.trim().isEmpty()) {
            return R.drawable.ic_tag;
        }

        int id = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
        return id == 0 ? R.drawable.ic_tag : id;
    }

    private void mostrarImagen(ImageView imageView, String nombreImagen) {
        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                nombreImagen,
                R.drawable.ic_tag
        );
    }

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductoPedido;
        ImageButton btnEliminarPedido;
        TextView txtNombreProductoPedido;
        TextView txtDetalleProductoPedido;
        TextView txtDisponiblePedido;
        TextView btnMenosPedido;
        TextView txtCantidadPedido;
        TextView btnMasPedido;
        TextView txtPrecioPedido;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductoPedido = itemView.findViewById(R.id.imgProductoPedido);
            btnEliminarPedido = itemView.findViewById(R.id.btnEliminarPedido);
            txtNombreProductoPedido = itemView.findViewById(R.id.txtNombreProductoPedido);
            txtDetalleProductoPedido = itemView.findViewById(R.id.txtDetalleProductoPedido);
            txtDisponiblePedido = itemView.findViewById(R.id.txtDisponiblePedido);
            btnMenosPedido = itemView.findViewById(R.id.btnMenosPedido);
            txtCantidadPedido = itemView.findViewById(R.id.txtCantidadPedido);
            btnMasPedido = itemView.findViewById(R.id.btnMasPedido);
            txtPrecioPedido = itemView.findViewById(R.id.txtPrecioPedido);
        }
    }
}
