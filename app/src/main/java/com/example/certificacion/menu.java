package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);

        // Mostrar los mensajes Toast
        Toast.makeText(this, "Vista Menu", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Corrida con éxito", Toast.LENGTH_SHORT).show();

        // Referencia al botón Bt_regresar1
        Button btnRegresar = findViewById(R.id.Bt_regresar1);

        // Configurar el evento para regresar al MainActivity
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar mensaje Toast
                Toast.makeText(menu.this, "Regresando al menú principal...", Toast.LENGTH_SHORT).show();
                // Intent para volver al MainActivity
                Intent intent = new Intent(menu.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finaliza el Activity actual
            }
        });

        // Referencia al botón Bt_Subida_Archivos para ir a la actividad subida_archivos
        Button btnSiguiente = findViewById(R.id.Bt_SubirArchivos);

        // Configurar el evento para ir al Activity 3 (subida_archivos)
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar mensaje Toast
                //Toast.makeText(menu.this, "Vista  Subida de Archivos", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(menu.this,subida_archivos.class);
                startActivity(intent);
            }
        });
    }
}
