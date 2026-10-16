package com.example.certificacion;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class datos_firebase extends AppCompatActivity {

    private static final String TAG = "FIREBASE";
    private DatabaseReference referenciaFirebase;
    private EditText editTextItemNombre, editTextDescription, editTextQuantity;
    private Button btnSave, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_firebase);

        // 1. Conectar a tu base prueba-de-conexion-c09d6
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(
                "https://prueba-de-conexion-c09d6-default-rtdb.firebaseio.com/"
        );
        referenciaFirebase = firebaseDatabase.getReference();

        // 2. Inicializar vistas
        editTextItemNombre = findViewById(R.id.editTextItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        btnSave = findViewById(R.id.btnSave);
        btnRegresar = findViewById(R.id.btregresar5);

        // 3. Botón Guardar
        btnSave.setOnClickListener(v -> {
            String nombre = editTextItemNombre.getText().toString().trim();
            String apellido = editTextDescription.getText().toString().trim();
            String telefono = editTextQuantity.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombre);
            datos.put("apellido", apellido);
            datos.put("telefono", telefono);
            datos.put("fecha", System.currentTimeMillis());

            referenciaFirebase.child("contactos").push().setValue(datos)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                        editTextItemNombre.setText("");
                        editTextDescription.setText("");
                        editTextQuantity.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Falló: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error al guardar", e);
                    });
        });

        // 4. Botón Regresar
        btnRegresar.setOnClickListener(v -> finish());
    }
}