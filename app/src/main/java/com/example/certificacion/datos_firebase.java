package com.example.certificacion;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class datos_firebase extends AppCompatActivity {

    private static final String TAG = "FIREBASE";

    private DatabaseReference referenciaFirebase;

    private EditText editTextItemNombre;
    private EditText editTextDescription;
    private EditText editTextQuantity;

    private Button btnSave;
    private Button btnEliminar;
    private Button btnRegresar;

    private ListView listaContactos;
    private View vistaSinContactos;

    private ArrayList<Contacto> contactos;
    private ContactoAdapter adaptador;

    private String contactoSeleccionadoKey = null;
    private ValueEventListener listenerContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_firebase);

        FirebaseDatabase firebaseDatabase =
                FirebaseDatabase.getInstance(
                        "https://prueba-de-conexion-c09d6-default-rtdb.firebaseio.com/"
                );
        referenciaFirebase = firebaseDatabase.getReference().child("contactos");

        editTextItemNombre = findViewById(R.id.editTextItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextQuantity = findViewById(R.id.editTextQuantity);

        btnSave = findViewById(R.id.btnSave);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnRegresar = findViewById(R.id.btregresar5);

        listaContactos = findViewById(R.id.listaContactos);
        vistaSinContactos = findViewById(R.id.txtSinContactos);

        contactos = new ArrayList<>();

        // NUEVO ADAPTADOR BONITO
        adaptador = new ContactoAdapter(this, contactos);
        listaContactos.setAdapter(adaptador);
        listaContactos.setEmptyView(vistaSinContactos);

        btnEliminar.setEnabled(false);

        btnSave.setOnClickListener(v -> guardarOActualizarContacto());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());

        listaContactos.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= contactos.size()) return;
            Contacto contacto = contactos.get(position);
            contactoSeleccionadoKey = contacto.key;

            editTextItemNombre.setText(contacto.nombre);
            editTextDescription.setText(contacto.apellido);
            editTextQuantity.setText(contacto.telefono);

            btnSave.setText("Actualizar");
            btnEliminar.setEnabled(true);

            Toast.makeText(datos_firebase.this, "Contacto seleccionado para editar", Toast.LENGTH_SHORT).show();
        });

        btnRegresar.setOnClickListener(v -> finish());
        configurarListenerContactos();
    }

    private void configurarListenerContactos() {
        listenerContactos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                contactos.clear();
                for (DataSnapshot registro : snapshot.getChildren()) {
                    String key = registro.getKey();
                    String nombre = registro.child("nombre").getValue(String.class);
                    String apellido = registro.child("apellido").getValue(String.class);
                    String telefono = registro.child("telefono").getValue(String.class);

                    if (key == null) continue;
                    if (nombre == null) nombre = "";
                    if (apellido == null) apellido = "";
                    if (telefono == null) telefono = "";

                    contactos.add(new Contacto(key, nombre, apellido, telefono));
                }
                adaptador.notifyDataSetChanged();

                if (contactoSeleccionadoKey != null) {
                    boolean existe = false;
                    for (Contacto c : contactos) {
                        if (c.key.equals(contactoSeleccionadoKey)) { existe = true; break; }
                    }
                    if (!existe) limpiarFormulario();
                }
                Log.d(TAG, "Contactos recibidos: " + contactos.size());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error al consultar contactos: " + error.getMessage());
                Toast.makeText(datos_firebase.this, "No se pudieron consultar: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (referenciaFirebase != null && listenerContactos != null) {
            referenciaFirebase.addValueEventListener(listenerContactos);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (referenciaFirebase != null && listenerContactos != null) {
            referenciaFirebase.removeEventListener(listenerContactos);
        }
    }

    private void guardarOActualizarContacto() {
        String nombre = editTextItemNombre.getText().toString().trim();
        String apellido = editTextDescription.getText().toString().trim();
        String telefono = editTextQuantity.getText().toString().trim();

        if (nombre.isEmpty()) { editTextItemNombre.setError("Ingresa el nombre"); editTextItemNombre.requestFocus(); return; }
        if (apellido.isEmpty()) { editTextDescription.setError("Ingresa el apellido"); editTextDescription.requestFocus(); return; }
        if (telefono.isEmpty()) { editTextQuantity.setError("Ingresa el teléfono"); editTextQuantity.requestFocus(); return; }

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("apellido", apellido);
        datos.put("telefono", telefono);
        datos.put("fecha", System.currentTimeMillis());

        if (contactoSeleccionadoKey == null) {
            referenciaFirebase.push().setValue(datos)
                    .addOnSuccessListener(unused -> { Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show(); limpiarFormulario(); })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            referenciaFirebase.child(contactoSeleccionadoKey).updateChildren(datos)
                    .addOnSuccessListener(unused -> { Toast.makeText(this, "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show(); limpiarFormulario(); })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private void confirmarEliminacion() {
        if (contactoSeleccionadoKey == null) { Toast.makeText(this, "Selecciona un contacto primero", Toast.LENGTH_SHORT).show(); return; }
        new AlertDialog.Builder(this)
                .setTitle("Eliminar contacto")
                .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarContacto())
                .show();
    }

    private void eliminarContacto() {
        if (contactoSeleccionadoKey == null) return;
        referenciaFirebase.child(contactoSeleccionadoKey).removeValue()
                .addOnSuccessListener(unused -> { Toast.makeText(this, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show(); limpiarFormulario(); })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void limpiarFormulario() {
        contactoSeleccionadoKey = null;
        editTextItemNombre.setText("");
        editTextDescription.setText("");
        editTextQuantity.setText("");
        btnSave.setText("Guardar");
        btnEliminar.setEnabled(false);
        listaContactos.clearChoices();
    }

    // =====================================================
    // ADAPTADOR NUEVO PARA EL DISEÑO BONITO
    // =====================================================
    private static class ContactoAdapter extends ArrayAdapter<Contacto> {

        ContactoAdapter(datos_firebase context, ArrayList<Contacto> contactos) {
            super(context, 0, contactos);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contacto, parent, false);
            }

            Contacto contacto = getItem(position);
            if (contacto == null) return convertView;

            TextView tvIniciales = convertView.findViewById(R.id.tvIniciales);
            TextView tvNombre = convertView.findViewById(R.id.tvNombre);
            TextView tvTelefono = convertView.findViewById(R.id.tvTelefono);

            String nombreCompleto = (contacto.nombre + " " + contacto.apellido).trim();
            tvNombre.setText(nombreCompleto);
            tvTelefono.setText(contacto.telefono);

            // Iniciales para el circulo morado
            String iniciales = "";
            if (!contacto.nombre.isEmpty()) iniciales += contacto.nombre.substring(0, 1).toUpperCase();
            if (!contacto.apellido.isEmpty()) iniciales += contacto.apellido.substring(0, 1).toUpperCase();
            if (iniciales.isEmpty()) iniciales = "C";
            tvIniciales.setText(iniciales);

            return convertView;
        }
    }

    private static class Contacto {
        String key;
        String nombre;
        String apellido;
        String telefono;
        Contacto(String key, String nombre, String apellido, String telefono) {
            this.key = key;
            this.nombre = nombre;
            this.apellido = apellido;
            this.telefono = telefono;
        }
    }
}