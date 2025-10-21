package com.example.maquirentapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.maquirentapp.Model.Tarea;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
public class TareasViewModel extends ViewModel {

    private static final String COLLECTION_TAREAS = "tareas";
    private final MutableLiveData<List<Tarea>> tareas = new MutableLiveData<>(new ArrayList<>());

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseAuth.AuthStateListener authStateListener;
    private ListenerRegistration listenerRegistration;

    public TareasViewModel() {
        authStateListener = firebaseAuth -> iniciarEscucha(firebaseAuth.getCurrentUser());
        auth.addAuthStateListener(authStateListener);
        iniciarEscucha(auth.getCurrentUser());
    }

    public LiveData<List<Tarea>> obtenerTareas() {
        return tareas;
    }

    public void agregarTarea(String titulo) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Tarea nuevaTarea = new Tarea();
        nuevaTarea.setTitulo(titulo);
        nuevaTarea.setCompletada(false);

        nuevaTarea.setFechaCreacion(System.currentTimeMillis());
        nuevaTarea.setCreadoPor(currentUser.getUid());
        String nombreCreador = currentUser.getDisplayName() != null
                ? currentUser.getDisplayName()
                : currentUser.getEmail();
        nuevaTarea.setNombreCreador(nombreCreador);

        firestore.collection(COLLECTION_TAREAS)
                .add(nuevaTarea);
    }

    public void actualizarEstado(Tarea tarea, boolean completada) {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || tarea.getId() == null) {
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("completada", completada);
        updates.put("fechaCompletada", completada ? System.currentTimeMillis() : 0L);
        updates.put("completadaPor", completada ? currentUser.getUid() : null);
        String nombreCompletador = currentUser.getDisplayName() != null
                ? currentUser.getDisplayName()
                : currentUser.getEmail();
        updates.put("nombreCompletador", completada ? nombreCompletador : null);

        firestore.collection(COLLECTION_TAREAS)
                .document(tarea.getId())
                .update(updates);
    }

    private void iniciarEscucha(FirebaseUser currentUser) {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }

        if (currentUser == null) {
            tareas.setValue(new ArrayList<>());
            return;
        }

        listenerRegistration = firestore.collection(COLLECTION_TAREAS)
                .whereEqualTo("creadoPor", currentUser.getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return;
                    }

                    List<Tarea> listaTareas = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Tarea tarea = document.toObject(Tarea.class);
                        if (tarea != null) {
                            tarea.setId(document.getId());
                            listaTareas.add(tarea);
                        }
                    }
                    Collections.sort(listaTareas, (t1, t2) -> Long.compare(t2.getFechaCreacion(), t1.getFechaCreacion()));
                    tareas.setValue(listaTareas);
                });
    }

    @Override
    protected void onCleared() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        auth.removeAuthStateListener(authStateListener);
        super.onCleared();
    }
}
