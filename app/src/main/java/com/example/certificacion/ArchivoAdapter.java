package com.example.certificacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ArchivoAdapter extends RecyclerView.Adapter<ArchivoAdapter.ViewHolder> implements Filterable {

    private List<Archivo> listaOriginal;
    private List<Archivo> listaFiltrada;

    public ArchivoAdapter(List<Archivo> lista) {
        this.listaOriginal = new ArrayList<>(lista);
        this.listaFiltrada = new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archivo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Archivo archivo = listaFiltrada.get(position);
        holder.tvNombre.setText(archivo.getNombre());
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                List<Archivo> filtrados = new ArrayList<>();
                if (query.isEmpty()) {
                    filtrados = new ArrayList<>(listaOriginal);
                } else {
                    for (Archivo item : listaOriginal) {
                        if (item.getNombre().toLowerCase().contains(query)) {
                            filtrados.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtrados;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listaFiltrada = (List<Archivo>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreArchivo);
        }
    }
}