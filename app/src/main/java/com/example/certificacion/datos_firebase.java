package com.example.certificacion;

import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class datos_firebase extends AppCompatActivity {

    private static final String TAG = "FIREBASE";
    private DatabaseReference referenciaFirebase;

    private EditText editTextItemNombre;
    private EditText editTextDescription;
    private EditText editTextQuantity;

    private TextInputLayout tilNombre, tilApellido, tilTelefono;

    private Button btnSave;
    private Button btnEliminar;
    private Button btnRegresar;
    private Button btnExportarPDF;

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

        // --- PERMISO NOTIFICACIONES ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        FirebaseDatabase firebaseDatabase =
                FirebaseDatabase.getInstance(
                        "https://prueba-de-conexion-c09d6-default-rtdb.firebaseio.com/"
                );
        referenciaFirebase = firebaseDatabase.getReference().child("contactos");

        editTextItemNombre = findViewById(R.id.editTextItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextQuantity = findViewById(R.id.editTextQuantity);

        tilNombre = findViewById(R.id.tilNombre);
        tilApellido = findViewById(R.id.tilApellido);
        tilTelefono = findViewById(R.id.tilTelefono);

        btnSave = findViewById(R.id.btnSave);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnRegresar = findViewById(R.id.btregresar5);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);

        listaContactos = findViewById(R.id.listaContactos);
        vistaSinContactos = findViewById(R.id.txtSinContactos);

        contactos = new ArrayList<>();
        adaptador = new ContactoAdapter(this, contactos);
        listaContactos.setAdapter(adaptador);
        listaContactos.setEmptyView(vistaSinContactos);

        btnEliminar.setEnabled(false);

        btnSave.setOnClickListener(v -> guardarOActualizarContacto());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());
        btnExportarPDF.setOnClickListener(v -> exportarContactosAPDF());

        editTextItemNombre.addTextChangedListener(new SimpleWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilNombre.setError(null);
            }
        });
        editTextQuantity.addTextChangedListener(new SimpleWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilTelefono.setError(null);
            }
        });

        listaContactos.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= contactos.size()) return;
            Contacto contacto = contactos.get(position);
            contactoSeleccionadoKey = contacto.key;

            editTextItemNombre.setText(contacto.nombre);
            editTextDescription.setText(contacto.apellido);
            editTextQuantity.setText(contacto.telefono);

            btnSave.setText("Actualizar");
            btnEliminar.setEnabled(true);
        });

        btnRegresar.setOnClickListener(v -> finish());
        configurarListenerContactos();
    }

    private boolean validarDatos() {
        String nombre = editTextItemNombre.getText().toString().trim();
        String apellido = editTextDescription.getText().toString().trim();
        String telefono = editTextQuantity.getText().toString().trim();

        if (nombre.isEmpty()) {
            tilNombre.setError("El nombre es obligatorio");
            mostrarError("El nombre no puede ir vacío");
            editTextItemNombre.requestFocus();
            return false;
        } else {
            tilNombre.setError(null);
        }

        if (apellido.isEmpty()) {
            tilApellido.setError("El apellido es obligatorio");
            mostrarError("El apellido no puede ir vacío");
            editTextDescription.requestFocus();
            return false;
        } else {
            tilApellido.setError(null);
        }

        if (telefono.isEmpty()) {
            tilTelefono.setError("El teléfono es obligatorio");
            mostrarError("El teléfono no puede ir vacío");
            editTextQuantity.requestFocus();
            return false;
        }

        if (telefono.length() < 8) {
            tilTelefono.setError("Debe tener 8 dígitos mínimo");
            mostrarError("El teléfono debe tener al menos 8 dígitos");
            editTextQuantity.requestFocus();
            return false;
        } else {
            tilTelefono.setError(null);
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.RED)
                .setTextColor(Color.WHITE)
                .show();
    }

    private void mostrarExito(String mensaje) {
        Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.parseColor("#2E7D32"))
                .setTextColor(Color.WHITE)
                .show();
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error al consultar contactos: " + error.getMessage());
                mostrarError("No se pudieron consultar: " + error.getMessage());
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
        if (!validarDatos()) return;

        String nombre = editTextItemNombre.getText().toString().trim();
        String apellido = editTextDescription.getText().toString().trim();
        String telefono = editTextQuantity.getText().toString().trim();

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("apellido", apellido);
        datos.put("telefono", telefono);
        datos.put("fecha", System.currentTimeMillis());

        if (contactoSeleccionadoKey == null) {
            referenciaFirebase.push().setValue(datos)
                    .addOnSuccessListener(unused -> {
                        mostrarExito("Contacto guardado correctamente");
                        MyFirebaseMessagingService.mostrarNotificacionLocal(this, "Contacto nuevo", nombre + " " + apellido + " fue agregado");
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e -> mostrarError("Error al guardar: " + e.getMessage()));
        } else {
            referenciaFirebase.child(contactoSeleccionadoKey).updateChildren(datos)
                    .addOnSuccessListener(unused -> {
                        mostrarExito("Contacto actualizado correctamente");
                        MyFirebaseMessagingService.mostrarNotificacionLocal(this, "Contacto actualizado", nombre + " " + apellido + " fue actualizado");
                        limpiarFormulario();
                    })
                    .addOnFailureListener(e -> mostrarError("Error al actualizar: " + e.getMessage()));
        }
    }

    private void exportarContactosAPDF() {
        if (contactos.isEmpty()) {
            mostrarError("No hay contactos para exportar");
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        Paint headerPaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        canvas.drawText("Lista de Contactos", 40, 50, titlePaint);

        paint.setTextSize(10);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Generado: " + fecha + " | Total: " + contactos.size() + " contactos", 40, 75, paint);

        paint.setStrokeWidth(2);
        paint.setColor(Color.GRAY);
        canvas.drawLine(40, 85, 555, 85, paint);

        headerPaint.setTextSize(12);
        headerPaint.setFakeBoldText(true);
        headerPaint.setColor(Color.WHITE);
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#6200EE"));
        canvas.drawRect(40, 95, 555, 120, bgPaint);
        canvas.drawText("No.", 45, 112, headerPaint);
        canvas.drawText("Nombre Completo", 80, 112, headerPaint);
        canvas.drawText("Telefono", 350, 112, headerPaint);

        int y = 140;
        paint.setTextSize(11);
        paint.setColor(Color.BLACK);

        for (int i = 0; i < contactos.size(); i++) {
            if (y > 800) {
                pdfDocument.finishPage(page);
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
            Contacto c = contactos.get(i);
            String nombreCompleto = c.nombre + " " + c.apellido;

            if (i % 2 == 0) {
                Paint rowBg = new Paint();
                rowBg.setColor(Color.parseColor("#F3F0FF"));
                canvas.drawRect(40, y - 15, 555, y + 5, rowBg);
            }

            canvas.drawText(String.valueOf(i + 1), 45, y, paint);
            canvas.drawText(nombreCompleto, 80, y, paint);
            canvas.drawText(c.telefono, 350, y, paint);
            y += 22;
        }

        pdfDocument.finishPage(page);

        try {
            String fileName = "contactos_" + System.currentTimeMillis() + ".pdf";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                pdfDocument.writeTo(outputStream);
                if (outputStream != null) outputStream.close();
                mostrarExito("PDF guardado en Descargas: " + fileName);
            }
        } catch (Exception e) {
            mostrarError("Error al crear PDF: " + e.getMessage());
            Log.e(TAG, "Error PDF", e);
        } finally {
            pdfDocument.close();
        }
    }

    private void confirmarEliminacion() {
        if (contactoSeleccionadoKey == null) { mostrarError("Selecciona un contacto primero"); return; }
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
                .addOnSuccessListener(unused -> {
                    mostrarExito("Contacto eliminado");
                    MyFirebaseMessagingService.mostrarNotificacionLocal(this, "Contacto eliminado", "Un contacto fue eliminado");
                    limpiarFormulario();
                })
                .addOnFailureListener(e -> mostrarError("Error al eliminar: " + e.getMessage()));
    }

    private void limpiarFormulario() {
        contactoSeleccionadoKey = null;
        editTextItemNombre.setText("");
        editTextDescription.setText("");
        editTextQuantity.setText("");
        tilNombre.setError(null);
        tilApellido.setError(null);
        tilTelefono.setError(null);
        btnSave.setText("Guardar");
        btnEliminar.setEnabled(false);
        listaContactos.clearChoices();
    }

    private abstract static class SimpleWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }

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