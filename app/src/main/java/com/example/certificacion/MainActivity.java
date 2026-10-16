package com.example.certificacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASE";
    private DatabaseReference referenciaFirebase;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View vistaPrincipal = findViewById(R.id.main);
        if (vistaPrincipal != null) {
            ViewCompat.setOnApplyWindowInsetsListener(vistaPrincipal, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        mAuth = FirebaseAuth.getInstance();
        conectarFirebase();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Log.e(TAG, "Google sign in failed", e);
                            Toast.makeText(this, "Error Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Button btnGoogle = findViewById(R.id.Btgoogle);
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        Button buttonSiguiente = findViewById(R.id.Bt_Siguiente1);
        if (buttonSiguiente != null) {
            buttonSiguiente.setOnClickListener(view -> {
                startActivity(new Intent(MainActivity.this, menu.class));
            });
        }

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, menu.class));
            finish();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Bienvenido " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        guardarPruebaConexion();
                        startActivity(new Intent(MainActivity.this, menu.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Fallo autenticación.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void conectarFirebase() {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://prueba-de-conexion-c09d6-default-rtdb.firebaseio.com");
            referenciaFirebase = firebaseDatabase.getReference();
            Log.d(TAG, "Firebase inicializado correctamente.");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar Firebase.", e);
        }
    }

    private void guardarPruebaConexion() {
        if (referenciaFirebase == null) return;
        Map<String, Object> datos = new HashMap<>();
        datos.put("mensaje", "Conexion correcta con Firebase");
        datos.put("usuario", mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "invitado");
        datos.put("estado", "Conectado");
        referenciaFirebase.child("pruebas_conexion").push().setValue(datos);
    }
}