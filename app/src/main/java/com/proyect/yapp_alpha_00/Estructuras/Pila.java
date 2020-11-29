package com.proyect.yapp_alpha_00.Estructuras;

public class Pila<T> {

    public int size;

    class Nodo {
        T info;
        Nodo sig;
    }

    private Nodo raiz;

    public Pila () {
        raiz=null;
        size = 0;
    }

    public void push(T x) {
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

    public T pop(){
        if (raiz!=null){
            T informacion = raiz.info;
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

    public T peek(){
        Nodo reco=raiz;
        if(reco==null){
            System.out.print("no puedes sacar elementos de una pila vacía");
        }
        return reco.info;

    }

}
