package com.example.certificacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class menu extends AppCompatActivity {

    Switch switchTema;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cargar tema ANTES
        prefs = getSharedPreferences("tema", MODE_PRIVATE);
        boolean oscuro = prefs.getBoolean("oscuro", false);
        AppCompatDelegate.setDefaultNightMode(
                oscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);

        Toast.makeText(this, "Vista Menu", Toast.LENGTH_SHORT).show();

        switchTema = findViewById(R.id.switchTema);
        switchTema.setChecked(oscuro);
        switchTema.setOnCheckedChangeListener((v, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            prefs.edit().putBoolean("oscuro", isChecked).apply();
        });

        findViewById(R.id.Bt_regresar1).setOnClickListener(v -> {
            startActivity(new Intent(menu.this, MainActivity.class));
            finish();
        });

        findViewById(R.id.Bt_SubirArchivos).setOnClickListener(v -> {
            startActivity(new Intent(menu.this, subida_archivos.class));
        });

        findViewById(R.id.Bt_VisualizarArchivos).setOnClickListener(v -> {
            Toast.makeText(this, "Vista Visualización", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, tu_clase_visualizar.class);
            // startActivity(intent);
        });

        findViewById(R.id.Bt_Siguiente).setOnClickListener(v -> {
            Toast.makeText(this, "Siguiente", Toast.LENGTH_SHORT).show();
        });
    }
}