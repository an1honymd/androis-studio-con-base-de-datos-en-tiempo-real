package com.example.certificacion;

public class Archivo {
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;

    public Archivo() {} // Obligatorio para Firebase

    public Archivo(String nombre) {
        this.nombre = nombre;
    }

    // Constructor completo - ESTE ES EL QUE NECESITAS
    public Archivo(String nombre, String apellido, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }

    // Para que te devuelva "Sarita yoj" completo
    public String getNombreCompleto() {
        if (apellido != null && !apellido.isEmpty()) {
            return nombre + " " + apellido;
        }
        return nombre;
    }
}