package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class inicio extends AppCompatActivity {

    private static final long TIEMPO_SPLASH = 3000;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable abrirPantallaPrincipal = new Runnable() {
        @Override
        public void run() {
            if (!isFinishing() && !isDestroyed()) {
                Intent intent = new Intent(inicio.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inicio);

        View vistaPrincipal = findViewById(R.id.main);

        if (vistaPrincipal != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    vistaPrincipal,
                    (v, insets) -> {
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
                    }
            );
        }

        ImageView imagenInicio = findViewById(R.id.imagenInicio);

        if (imagenInicio != null) {
            imagenInicio.setImageResource(R.drawable.foto);
        }

        handler.postDelayed(abrirPantallaPrincipal, TIEMPO_SPLASH);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(abrirPantallaPrincipal);
        super.onDestroy();
    }
}

