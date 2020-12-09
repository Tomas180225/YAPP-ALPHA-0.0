package com.proyect.yapp_alpha_00.Estructuras;

import android.util.Log;

import com.proyect.yapp_alpha_00.Fragment.CommunityFragment;
import com.proyect.yapp_alpha_00.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class AVLTree{

    public Nodo root;
    public List<Post> listPosts = new ArrayList<>();

    public List<Post> getListPosts() {
        return listPosts;
    }

    public void setListPosts(List<Post> listPosts) {
        this.listPosts = listPosts;
    }

    public int height(Nodo N){
        if(N == null){
            return 0;
        }
        return N.height;
    }

    public int Height(Nodo nodo, String palabra) {
        int altura = -1;
        if (nodo != null) {
            if(palabra.compareTo(nodo.data2) < 0){
                altura = Height(nodo.left, palabra);
            }
            else if(palabra.compareTo(nodo.data2) > 0){
                altura = Height(nodo.right, palabra);
            }
            else{
                altura = nodo.height;
            }

        }
        return altura;
    }

    public int max(int a, int b){
        if(a > b){
            return a;
        }
        else{
            return b;
        }
    }

    public Nodo rotarDerecha(Nodo nodo){
        Nodo aux = nodo.left;
        Nodo aux2 = aux.right;

        aux.right = nodo;
        nodo.left = aux2;

        nodo.height = max(height(nodo.left), height(nodo.right)) + 1;
        aux.height = max(height(aux.left), height(aux.right)) + 1;

        return aux;
    }

    public Nodo rotarIzquierda(Nodo nodo) {
        Nodo aux = nodo.right;
        Nodo aux2 = aux.left;

        aux.left = nodo;
        nodo.right = aux2;

        nodo.height = max(height(nodo.left), height(nodo.right)) + 1;
        aux.height = max(height(aux.left), height(aux.right)) + 1;

        return aux;
    }

    public int Balancear(Nodo N) {
        if(N == null){
            return 0;
        }
        return height(N.left) - height(N.right);
    }

    public Nodo insert(Nodo nodo, Post data, String data2) {

        if(nodo == null){
            return (new Nodo(data, data2));
        }
        else if(data2.compareTo(nodo.data2) < 0){
            nodo.left = insert(nodo.left, data, data2);
        }
        else if (data2.compareTo(nodo.data2) >= 0){
            nodo.right = insert(nodo.right, data, data2);
        }

        nodo.height = 1 + max(height(nodo.left), height(nodo.right));

        int balance = Balancear(nodo);

        if (balance > 1 && data2.compareTo(nodo.left.data2) < 0){
            if(nodo.left.data2 != null) {
            }
            return rotarDerecha(nodo);
        }
        if (balance < -1 && data2.compareTo(nodo.right.data2) > 0){
            return rotarIzquierda(nodo);
        }
        if (balance > 1 && data2.compareTo(nodo.left.data2) > 0) {
            nodo.left = rotarIzquierda(nodo.left);
            return rotarDerecha(nodo);
        }

        if (balance < -1 && data2.compareTo(nodo.right.data2) < 0) {
            nodo.right = rotarDerecha(nodo.right);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }



    public int NumberNodes(Nodo nodo, String palabra){
        Nodo explorador = nodo;
        int cont = 0;
        while(true){
            if(explorador == null){
                break;
            }
            else if(explorador.data2.equals(palabra)){
                cont = contarNodos(explorador);
                break;
            }
            else{
                int comparar = palabra.compareTo(explorador.data2);
                if(comparar < 0){
                    explorador = explorador.left;
                }
                if(comparar > 0){
                    explorador = explorador.right;
                }
            }
        }

        return cont;
    }

    public int contarNodos(Nodo nodo){
        int cont = 0;
        if(nodo != null){
            if(nodo.left != null || nodo.right != null){
                cont += 1 + contarNodos(nodo.left) + contarNodos(nodo.right);
            }
            else{
                cont = 1;
            }
        }
        return cont;
    }

    public void Find(Nodo nodo, String palabra) {
        if (nodo != null) {
            if(nodo.data2.equals(palabra)){
                listPosts.add(nodo.data);
                if(nodo.right != null){
                    Find(nodo.right, palabra);
                }
            }
            else if(palabra.compareTo(nodo.data2) < 0 && nodo.left != null){
                Find(nodo.left, palabra);
            }
            else if(nodo.right != null){
                Find(nodo.right, palabra);
            }

        }
    }

    public String FindMin(Nodo nodo) {
        if(nodo == null){
            return "0";
        }
        String min = nodo.data2;
        if(nodo.left != null){
            min = FindMin(nodo.left);
        }
        else{
            min = nodo.data2;
        }
        return min;
    }

    public String FindMax(Nodo nodo) {
        if(nodo == null){
            return "0";
        }
        String max = nodo.data2;
        if(nodo.right != null){
            max = FindMax(nodo.right);
        }
        else{
            max = nodo.data2;
        }
        return max;
    }
    public void preOrder(Nodo nodo) {
        if (nodo != null) {
            preOrder(nodo.left);
            listPosts.add(nodo.data);
            preOrder(nodo.right);
        }
    }

    public void postOrder(Nodo nodo) {
        if (nodo != null) {
            postOrder(nodo.right);
            listPosts.add(nodo.data);
            postOrder(nodo.left);
        }
    }

}
