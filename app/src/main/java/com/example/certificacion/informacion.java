package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class informacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.informacion);

        // Mostrar mensaje Toast al iniciar la actividad
        Toast.makeText(informacion.this, "Vista información", Toast.LENGTH_SHORT).show();

        // Referencia al botón Bt_regresar3 (asegúrate que R.id.Bt_regresar4 sea correcto)
        Button btnRegresar = findViewById(R.id.Bt_regresar4);

        // Configurar el evento para regresar al Activity 3 (subida_archivos)
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar mensaje Toast antes de regresar
                Toast.makeText(informacion.this, "Regresando a Subida de Archivos...", Toast.LENGTH_SHORT).show();

                // Intent para volver al Activity 3 (subida_archivos)
                Intent intent = new Intent(informacion.this, subida_archivos.class);
                startActivity(intent);
                finish(); // Finaliza el Activity actual para que no quede en la pila
            }
        });
    }
}
