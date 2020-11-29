package com.proyect.yapp_alpha_00.Adapters;

import com.proyect.yapp_alpha_00.Model.Post;

public class ArrayAdapter {

    public int size;
    public int deleted;

    class Nodo {
        Post info;
        Nodo sig;
    }

    private Nodo raiz;

    public ArrayAdapter() {
        raiz=null;
        deleted = 0;
    }

    public void push(Post x) {
        Nodo nuevo;
        nuevo = new Nodo();
        nuevo.info = x;
        size += 1;
        if(raiz==null){
            nuevo.sig = null;
            raiz = nuevo;
        }
        else{
            nuevo.sig = raiz;
            raiz = nuevo;
        }
    }

    public Post pop()
    {
        if (raiz!=null)
        {
            Post informacion = raiz.info;
            raiz = raiz.sig;
            size -= 1;
            deleted += 1;
            return informacion;
        }
        else
        {
            return null;
        }
    }

    public boolean isEmpty(){
        Nodo reco=raiz;
        if(reco == null){
            return true;
        }
        else{
            return false;
        }
    }

    public Post peek(){
        Nodo reco=raiz;
        if(reco==null){
            System.out.print("no puedes sacar elementos de una pila vacía");
        }
        return reco.info;

    }

}
