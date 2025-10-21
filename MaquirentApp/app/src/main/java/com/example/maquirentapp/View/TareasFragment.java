package com.example.maquirentapp.View;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquirentapp.R;

import com.example.maquirentapp.ViewModel.TareasViewModel;
import com.example.maquirentapp.adaptadores.TareasAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TareasFragment extends Fragment {
    private TareasViewModel viewModel;
    private TareasAdapter tareasAdapter;

    public TareasFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TareasViewModel.class);
    }
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tareas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTareas);
        FloatingActionButton fabAgregar = view.findViewById(R.id.fabAgregarTarea);

        tareasAdapter = new TareasAdapter((tarea, isChecked) -> viewModel.actualizarEstado(tarea, isChecked));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(tareasAdapter);

        viewModel.obtenerTareas().observe(getViewLifecycleOwner(), tareasAdapter::submitList);

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarTarea());
    }

    private void mostrarDialogoAgregarTarea() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null, false);

        TextInputLayout textInputLayout = dialogView.findViewById(R.id.textInputLayoutTaskName);
        TextInputEditText editTextNombre = dialogView.findViewById(R.id.editTextNombreTarea);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_dialogo_agregar_tarea)
                .setView(dialogView)
                .setPositiveButton(R.string.accion_guardar, null)
                .setNegativeButton(android.R.string.cancel, (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String nombreTarea = editTextNombre.getText() != null
                        ? editTextNombre.getText().toString().trim()
                        : "";

                if (TextUtils.isEmpty(nombreTarea)) {
                    textInputLayout.setError(getString(R.string.mensaje_error_nombre_tarea));
                    return;
                }

                textInputLayout.setError(null);
                viewModel.agregarTarea(nombreTarea);
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}

