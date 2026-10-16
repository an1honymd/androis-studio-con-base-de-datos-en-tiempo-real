package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class subida_archivos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Toast.makeText(
                subida_archivos.this,
                "Vista Subida de Archivos",
                Toast.LENGTH_SHORT
        ).show();

        EdgeToEdge.enable(this);

        setContentView(R.layout.subida_archivos);

        // ==========================================
        // BOTÓN REGRESAR
        // ==========================================

        Button btnRegresar2 = findViewById(R.id.Btregresar2);

        btnRegresar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(
                        subida_archivos.this,
                        "Regresando al menú principal...",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
                        subida_archivos.this,
                        menu.class
                );

                startActivity(intent);

                finish();
            }
        });

        // ==========================================
        // BOTÓN CÁMARA
        // ==========================================

        Button btnCamara = findViewById(R.id.Btcamara);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        subida_archivos.this,
                        camara.class
                );

                startActivity(intent);
            }
        });

        // ==========================================
        // BOTÓN INFORMACIÓN
        // ==========================================

        Button btnInformacion = findViewById(R.id.Bt_infomacion);

        btnInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        subida_archivos.this,
                        informacion.class
                );

                startActivity(intent);
            }
        });

        // ==========================================
        // BOTÓN ARCHIVOS PDF
        // ==========================================

        Button btnPdf = findViewById(R.id.Btsubidaarchivospdf);

        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        subida_archivos.this,
                        archivo_pdf.class
                );

                startActivity(intent);
            }
        });

        // ==========================================
        // BOTÓN ARCHIVOS WORD
        // ==========================================

        Button btnword = findViewById(R.id.Btsubidaarchivosword);

        btnword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        subida_archivos.this,
                        archivo_word.class
                );

                startActivity(intent);
            }
        });

        // ==========================================
        // BOTÓN DATOS FIREBASE
        // ==========================================

        Button btnDatosFirebase = findViewById(R.id.Bt_datosf);

        btnDatosFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        subida_archivos.this,
                        datos_firebase.class
                );

                startActivity(intent);
            }
        });
    }
}