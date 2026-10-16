package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class archivo_pdf extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.archivo_pdf);

        MaterialButton btnRegresar = findViewById(R.id.Bt_regresar4);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(archivo_pdf.this, subida_archivos.class);
                startActivity(intent);

                finish();
            }
        });
    }
}