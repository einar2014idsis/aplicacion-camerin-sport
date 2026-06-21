package com.example.camerinosports.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camerinosports.R;
import com.example.camerinosports.model.Producto;
import com.example.camerinosports.model.Promocion;
import com.example.camerinosports.viewmodel.PromocionViewModel;

import java.util.ArrayList;

// adapta los datos para mostrarlos en una lista de la pantalla.
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    private Context context;
    private ArrayList<Producto> productos;
    private OnProductoClickListener listener;
    private PromocionViewModel promocionViewModel;

    public ProductoAdapter(Context context, ArrayList<Producto> productos, OnProductoClickListener listener) {
        this.context = context;
        this.productos = productos;
        this.listener = listener;
        this.promocionViewModel = new PromocionViewModel(context);
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        final Producto producto = productos.get(position);
        Promocion promocion = promocionViewModel.obtenerPromocionPorProducto(producto.getId());

        holder.txtNombreProducto.setText(producto.getNombre());
        holder.txtCategoriaProducto.setText(producto.getCategoria());
        holder.txtStockProducto.setText("Stock: " + producto.getStock());
        holder.txtDisponibleProducto.setText(producto.getStock() > 0 ? "Disponible" : "Agotado");
        holder.txtDisponibleProducto.setTextColor(producto.getStock() > 0 ? 0xFF39FF66 : 0xFFFF5555);
        holder.txtEtiquetaProducto.setText(producto.isSostenible() ? "ODS 12" : "NUEVO");

        if (promocion != null) {
            holder.txtPrecioProducto.setText("S/ " + String.format("%.2f", promocion.getPrecioOferta()) + " oferta");
        } else {
            holder.txtPrecioProducto.setText("S/ " + String.format("%.2f", producto.getPrecio()));
        }

        mostrarImagen(holder.imgProducto, producto.getImagen());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onProductoClick(producto);
                }
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.btnVerDetalleProducto.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void actualizarLista(ArrayList<Producto> nuevaLista) {
        productos = nuevaLista;
        notifyDataSetChanged();
    }

    private int obtenerImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.trim().isEmpty()) {
            return R.drawable.ic_tag;
        }

        int id = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());

        if (id == 0) {
            return R.drawable.ic_tag;
        }

        return id;
    }

    private void mostrarImagen(ImageView imageView, String nombreImagen) {
        com.example.camerinosports.util.Imagenes.mostrar(
                imageView,
                nombreImagen,
                R.drawable.ic_tag
        );
    }

    // adapta los datos para mostrarlos en una lista de la pantalla.
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView txtEtiquetaProducto;
        TextView txtNombreProducto;
        TextView txtCategoriaProducto;
        TextView txtDisponibleProducto;
        TextView txtStockProducto;
        TextView txtPrecioProducto;
        TextView btnVerDetalleProducto;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtEtiquetaProducto = itemView.findViewById(R.id.txtEtiquetaProducto);
            txtNombreProducto = itemView.findViewById(R.id.txtNombreProducto);
            txtCategoriaProducto = itemView.findViewById(R.id.txtCategoriaProducto);
            txtDisponibleProducto = itemView.findViewById(R.id.txtDisponibleProducto);
            txtStockProducto = itemView.findViewById(R.id.txtStockProducto);
            txtPrecioProducto = itemView.findViewById(R.id.txtPrecioProducto);
            btnVerDetalleProducto = itemView.findViewById(R.id.btnVerDetalleProducto);
        }
    }
}
