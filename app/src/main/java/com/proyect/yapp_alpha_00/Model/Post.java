package com.proyect.yapp_alpha_00.Model;

public class Post {

    private String postid;
    private String postimg;
    private String usuario;
    private String posttitulo;
    private String descripcion;

    public Post(String postid, String postimg, String usuario, String posttitulo, String descripcion) {
        this.postid = postid;
        this.postimg = postimg;
        this.usuario = usuario;
        this.posttitulo = posttitulo;
        this.descripcion = descripcion;
    }

    public Post(){

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
