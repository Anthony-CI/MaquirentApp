package com.example.maquirentapp.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquirentapp.Model.Tarea;
import com.example.maquirentapp.R;

import java.util.ArrayList;
import java.util.List;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareaViewHolder> {

    public interface OnTareaCheckedListener {
        void onTareaChecked(Tarea tarea, boolean isChecked);
    }

    private final List<Tarea> tareas = new ArrayList<>();
    private final OnTareaCheckedListener listener;

    public TareasAdapter(OnTareaCheckedListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Tarea> nuevasTareas) {
        tareas.clear();
        if (nuevasTareas != null) {
            tareas.addAll(nuevasTareas);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareas.get(position);
        holder.bind(tarea);
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    class TareaViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox checkBoxTarea;
        private final TextView textViewNombreTarea;

        TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxTarea = itemView.findViewById(R.id.checkBoxTarea);
            textViewNombreTarea = itemView.findViewById(R.id.textViewNombreTarea);
        }

        void bind(Tarea tarea) {
            textViewNombreTarea.setText(tarea.getTitulo());
            checkBoxTarea.setOnCheckedChangeListener(null);
            checkBoxTarea.setChecked(tarea.isCompletada());
            checkBoxTarea.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTareaChecked(tarea, isChecked);
                }
            });
        }
    }
}