package com.proyect.yapp_alpha_00.Model;

public class User {

    private String id;
    private String usuario;
    private String telefono;
    private String nombre;
    private String img;
    private String bio;

    public User(String id, String usuario, String telefono, String nombre, String img, String bio) {
        this.id = id;
        this.usuario = usuario;
        this.telefono = telefono;
        this.nombre = nombre;
        this.img = img;
        this.bio = bio;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
