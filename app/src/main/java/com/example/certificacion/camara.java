package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class camara extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.camara);

        // Mostrar mensaje Toast al iniciar la actividad
        Toast.makeText(camara.this, "Vista Cámara", Toast.LENGTH_SHORT).show();

        // Referencia al botón Bt_regresar3
        Button btnRegresar = findViewById(R.id.Bt_regresar3);

        // Configurar el evento para regresar al Activity 3 (subida_archivos)
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar mensaje Toast antes de regresar
                Toast.makeText(camara.this, "Regresando a Subida de Archivos...", Toast.LENGTH_SHORT).show();

                // Intent para volver al Activity 3 (subida_archivos)
                Intent intent = new Intent(camara.this, subida_archivos.class);
                startActivity(intent);
                finish(); // Finaliza el Activity actual para que no quede en la pila
            }
        });
    }
}
