package com.example.maquirentapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.maquirentapp.Model.Tarea;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TareasViewModel extends ViewModel {

    private final MutableLiveData<List<Tarea>> tareas = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Tarea>> obtenerTareas() {
        return tareas;
    }

    public void agregarTarea(String titulo) {
        List<Tarea> actuales = new ArrayList<>(Objects.requireNonNull(tareas.getValue()));
        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setTitulo(titulo);
        nuevaTarea.setCompletada(false);
        actuales.add(nuevaTarea);
        tareas.setValue(actuales);
    }

    public void actualizarEstado(Tarea tarea, boolean completada) {
        tarea.setCompletada(completada);
        tareas.setValue(new ArrayList<>(Objects.requireNonNull(tareas.getValue())));
    }
}
