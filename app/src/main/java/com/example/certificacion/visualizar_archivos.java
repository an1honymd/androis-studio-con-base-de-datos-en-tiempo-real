package com.example.certificacion;

import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class visualizar_archivos extends AppCompatActivity {
    RecyclerView recyclerArchivos;
    SearchView searchView;
    ArchivoAdapter adapter;
    List<Archivo> listaArchivos = new ArrayList<>();
    DatabaseReference refContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizar_archivos);

        // BOTÓN DE REGRESAR - AQUÍ DENTRO VA
        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());

        recyclerArchivos = findViewById(R.id.recyclerArchivos);
        searchView = findViewById(R.id.searchView);
        recyclerArchivos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArchivoAdapter(listaArchivos);
        recyclerArchivos.setAdapter(adapter);
        refContactos = FirebaseDatabase.getInstance().getReference("contactos");
        cargarContactosReales();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    private void cargarContactosReales() {
        refContactos.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaArchivos.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Archivo contacto = data.getValue(Archivo.class);
                    if (contacto != null) {
                        listaArchivos.add(contacto);
                    }
                }
                adapter = new ArchivoAdapter(listaArchivos);
                recyclerArchivos.setAdapter(adapter);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}