package com.proyect.yapp_alpha_00.Estructuras;

import android.util.Log;

public class Pila<Post> {

    public int size;

    class Nodo {
        Post info;
        Nodo sig;
    }

    private Nodo raiz;

    public Pila () {
        raiz=null;
        size = 0;
    }

    public void push(Post x) {
        Nodo nuevo;
        nuevo = new Nodo();
        nuevo.info = x;
        size += 1;
        if (raiz==null)
        {
            nuevo.sig = null;
            raiz = nuevo;
        }
        else
        {
            nuevo.sig = raiz;
            raiz = nuevo;
        }
    }

    public Post pop(){
        if (raiz!=null){
            Post informacion = raiz.info;
            raiz = raiz.sig;
            size -= 1;
            return informacion;
        }
        else{
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
            Log.w("Error", "No puedes sacar elementos de una pila vacia");
        }
        return reco.info;

    }

}
