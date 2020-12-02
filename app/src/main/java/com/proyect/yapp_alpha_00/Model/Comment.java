package com.proyect.yapp_alpha_00.Model;

public class Comment {

    private String comentario;
    private String authorID;

    public Comment(String comentario, String authorID) {
        this.comentario = comentario;
        this.authorID = authorID;
    }

    public Comment(){

    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
}

