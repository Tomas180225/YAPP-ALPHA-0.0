package com.proyect.yapp_alpha_00.Model;

public class Post {

    private String postid;
    private String postimg;
    private String usuario;
    private String posttitulo;
    private String descripcion;
    private String categoria;
    private String fecha;

    public Post(String postid, String postimg, String usuario, String posttitulo, String descripcion, String categoria, String fecha) {
        this.postid = postid;
        this.postimg = postimg;
        this.usuario = usuario;
        this.posttitulo = posttitulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fecha = fecha;
    }
    public Post(){

    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimg() {
        return postimg;
    }

    public void setPostimg(String postimg) {
        this.postimg = postimg;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPosttitulo() {
        return posttitulo;
    }

    public void setPosttitulo(String posttitulo) {
        this.posttitulo = posttitulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
