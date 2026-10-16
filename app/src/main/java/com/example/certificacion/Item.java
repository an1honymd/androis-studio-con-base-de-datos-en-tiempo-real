package com.example.certificacion;

public class Item {

    private String nombre;
    private String apellido;
    private String numero_telefono;

    // Constructor vacío necesario para Firebase
    public Item() {}

    // Constructor con parámetros
    public Item(String nombre, String apellido, String numero_telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero_telefono = numero_telefono;
    }

    // Métodos getter y setter
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumero_Telefono() {
        return numero_telefono;
    }

    public void setNumero_Telefono(String numero_telefono) {
        this.numero_telefono = numero_telefono;
    }
}