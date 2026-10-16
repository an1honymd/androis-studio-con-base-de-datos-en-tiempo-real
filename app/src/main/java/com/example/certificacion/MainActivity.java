package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASE";

    private DatabaseReference referenciaFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View vistaPrincipal = findViewById(R.id.main);

        if (vistaPrincipal != null) {
            ViewCompat.setOnApplyWindowInsetsListener(vistaPrincipal, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );

                return insets;
            });
        }

        // Mensajes originales de tu aplicación
        Toast.makeText(this, "aplicacion", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "vicion artitisca", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "corrida exitosamente", Toast.LENGTH_SHORT).show();

        // Inicializar Firebase Realtime Database
        conectarFirebase();

        // Botón para ir al menú
        Button buttonSigueinte = findViewById(R.id.Bt_Siguiente1);

        if (buttonSigueinte != null) {
            buttonSigueinte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            MainActivity.this,
                            menu.class
                    );

                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Inicializa la conexión con Firebase Realtime Database.
     */
    private void conectarFirebase() {

        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(
                    "https://prueba-de-conexion-c09d6-default-rtdb.firebaseio.com"
            );

            referenciaFirebase = firebaseDatabase.getReference();

            Log.d(TAG, "Firebase inicializado correctamente.");

            guardarPruebaConexion();

        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar Firebase.", e);
        }
    }

    /**
     * Guarda un registro de prueba en Firebase.
     */
    private void guardarPruebaConexion() {

        Map<String, Object> datos = new HashMap<>();

        datos.put("mensaje", "Conexion correcta con Firebase");
        datos.put("estado", "Conectado");
        datos.put("aplicacion", "Certificacion");

        referenciaFirebase.child("pruebas_conexion")
                .push()
                .setValue(datos)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Datos guardados correctamente en Firebase.");

                    Toast.makeText(
                            MainActivity.this,
                            "Conexión con Firebase exitosa",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(error -> {
                    Log.e(TAG, "Error al guardar datos en Firebase.", error);

                    Toast.makeText(
                            MainActivity.this,
                            "Error al conectar con Firebase",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}

