package com.example.certificacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class menu extends AppCompatActivity {

    Switch switchTema;
    SharedPreferences prefs;

    ImageView imgPerfil;
    TextView tvNombrePerfil, tvCorreoPerfil;
    MaterialButton btnCerrarSesion;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cargar tema antes de crear vista
        prefs = getSharedPreferences("tema", MODE_PRIVATE);
        boolean oscuro = prefs.getBoolean("oscuro", false);
        AppCompatDelegate.setDefaultNightMode(
                oscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);

        Toast.makeText(this, "Vista Menu", Toast.LENGTH_SHORT).show();

        // TEMA OSCURO
        switchTema = findViewById(R.id.switchTema);
        switchTema.setChecked(oscuro);
        switchTema.setOnCheckedChangeListener((v, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            prefs.edit().putBoolean("oscuro", isChecked).apply();
        });

        // ===== PERFIL DE USUARIO GOOGLE =====
        imgPerfil = findViewById(R.id.imgPerfil);
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (user != null) {
            tvNombrePerfil.setText(user.getDisplayName());
            tvCorreoPerfil.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(imgPerfil);
            }
        }

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(menu.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        // BOTONES EXISTENTES
        findViewById(R.id.Bt_regresar1).setOnClickListener(v -> {
            startActivity(new Intent(menu.this, MainActivity.class));
            finish();
        });

        findViewById(R.id.Bt_SubirArchivos).setOnClickListener(v -> {
            startActivity(new Intent(menu.this, subida_archivos.class));
        });

        findViewById(R.id.Bt_VisualizarArchivos).setOnClickListener(v -> {
            Toast.makeText(this, "Vista Visualización", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.Bt_Siguiente).setOnClickListener(v -> {
            Toast.makeText(this, "Siguiente", Toast.LENGTH_SHORT).show();
        });
    }
}