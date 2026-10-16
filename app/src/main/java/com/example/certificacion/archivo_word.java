package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class archivo_word extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.archivos_word);

        MaterialButton btnRegresar = findViewById(R.id.Bt_regresar4);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(archivo_word.this, subida_archivos.class);
                startActivity(intent);

                finish();
            }
        });
    }
}
