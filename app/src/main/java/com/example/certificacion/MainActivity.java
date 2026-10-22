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
import com.google.firebase.auth.FirebaseUser;
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

        // Ajustar la interfaz a las barras del sistema.
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

        // Inicializar Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Inicializar Firebase Realtime Database.
        conectarFirebase();

        // Configurar el inicio de sesión con Google.
        configurarGoogleSignIn();

        // Botón para iniciar sesión con Google.
        Button btnGoogle = findViewById(R.id.Btgoogle);

        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> signInWithGoogle());
        } else {
            Log.e(TAG, "No se encontró el botón Btgoogle.");
        }

        // Botón para entrar como invitado.
        Button buttonSiguiente = findViewById(R.id.Bt_Siguiente1);

        if (buttonSiguiente != null) {
            buttonSiguiente.setOnClickListener(v -> {
                Toast.makeText(
                        MainActivity.this,
                        "Entrando como invitado...",
                        Toast.LENGTH_SHORT
                ).show();

                abrirMenu();
            });
        } else {
            Log.e(TAG, "No se encontró el botón Bt_Siguiente1.");
        }

        Toast.makeText(
                this,
                "Bienvenido",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Configurar Google Sign-In.
    private void configurarGoogleSignIn() {

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(
                                getString(R.string.default_web_client_id)
                        )
                        .requestEmail()
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() != RESULT_OK
                            || result.getData() == null) {

                        Toast.makeText(
                                MainActivity.this,
                                "Inicio de sesión cancelado.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Task<GoogleSignInAccount> task =
                            GoogleSignIn.getSignedInAccountFromIntent(
                                    result.getData()
                            );

                    try {
                        GoogleSignInAccount cuenta =
                                task.getResult(ApiException.class);

                        if (cuenta != null && cuenta.getIdToken() != null) {
                            firebaseAuthWithGoogle(cuenta.getIdToken());
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "No se pudo obtener el token de Google.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (ApiException e) {
                        Log.e(TAG, "Error al iniciar sesión con Google", e);

                        Toast.makeText(
                                MainActivity.this,
                                "Error de Google: " + e.getStatusCode(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Solicitar selección de cuenta de Google.
    private void signInWithGoogle() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {

                    Intent signInIntent =
                            mGoogleSignInClient.getSignInIntent();

                    googleSignInLauncher.launch(signInIntent);
                });
    }

    // Autenticar al usuario con Firebase.
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser usuario = mAuth.getCurrentUser();

                        if (usuario != null) {

                            String nombre = usuario.getDisplayName();

                            if (nombre == null || nombre.trim().isEmpty()) {
                                nombre = usuario.getEmail();
                            }

                            Toast.makeText(
                                    MainActivity.this,
                                    "Bienvenido " + nombre,
                                    Toast.LENGTH_LONG
                            ).show();

                            guardarPruebaConexion();

                            abrirMenu();
                        }

                    } else {

                        Log.e(
                                TAG,
                                "Error de autenticación con Firebase",
                                task.getException()
                        );

                        Toast.makeText(
                                MainActivity.this,
                                "No se pudo iniciar sesión con Firebase.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // Conectar con Firebase Realtime Database.
    private void conectarFirebase() {

        try {
            FirebaseDatabase firebaseDatabase =
                    FirebaseDatabase.getInstance(
                            "https://prueba-de-conexion-c09d6-default-rtdb.firebaseio.com"
                    );

            referenciaFirebase = firebaseDatabase.getReference();

            Log.d(TAG, "Firebase inicializado correctamente.");

        } catch (Exception e) {

            referenciaFirebase = null;

            Log.e(TAG, "Error al inicializar Firebase.", e);

            Toast.makeText(
                    this,
                    "Error al conectar con Firebase.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // Guardar datos de prueba en Realtime Database.
    private void guardarPruebaConexion() {

        if (referenciaFirebase == null) {
            Log.e(TAG, "La referencia de Firebase no está disponible.");
            return;
        }

        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario == null) {
            return;
        }

        Map<String, Object> datos = new HashMap<>();

        datos.put("mensaje", "Conexion correcta con Firebase");
        datos.put("usuario", usuario.getEmail());
        datos.put("nombre", usuario.getDisplayName());
        datos.put("uid", usuario.getUid());
        datos.put("estado", "Conectado");
        datos.put("fecha", System.currentTimeMillis());

        referenciaFirebase
                .child("pruebas_conexion")
                .push()
                .setValue(datos)
                .addOnSuccessListener(unused ->
                        Log.d(TAG, "Datos guardados correctamente.")
                )
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar datos en Firebase.", e);

                    Toast.makeText(
                            MainActivity.this,
                            "No se pudieron guardar los datos en Firebase.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Abrir el menú principal.
    private void abrirMenu() {

        Intent intent = new Intent(
                MainActivity.this,
                menu.class
        );

        startActivity(intent);

        finish();
    }
}

