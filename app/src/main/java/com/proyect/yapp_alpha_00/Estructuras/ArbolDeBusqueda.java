package com.proyect.yapp_alpha_00.Estructuras;

import com.proyect.yapp_alpha_00.Model.Post;

public class ArbolDeBusqueda {
    Nodo root;

    class Nodo {
        Post data;
        int height;
        Nodo left;
        Nodo right;

        Nodo(Post publicacion) {
            data = publicacion;
            height = 1;
        }

        //Inserción en el árbol dependiendo del criterio de búsqueda
        public Nodo insercionBusqueda(Nodo root, Post publicacion, String busqueda) {
            if (root == null) {
                return new Nodo(publicacion);
            }
            String Usuario = publicacion.getUsuario();
            String Titulo = publicacion.getPosttitulo();
            String Descripcion = publicacion.getDescripcion();
            if (Usuario.contains(busqueda)) {
                if (Usuario.compareTo(root.data.getUsuario()) < 0)
                    root.left = insercionBusqueda(root.left, publicacion, busqueda);
                else if (Usuario.compareTo(root.data.getUsuario()) > 0)
                    root.right = insercionBusqueda(root.right, publicacion, busqueda);
            } else if (Titulo.contains(busqueda)) {
                if (Titulo.compareTo(root.data.getPosttitulo()) < 0)
                    root.left = insercionBusqueda(root.left, publicacion, busqueda);
                else if (Titulo.compareTo(root.data.getPosttitulo()) > 0)
                    root.right = insercionBusqueda(root.right, publicacion, busqueda);
            } else if (Descripcion.contains(busqueda)) {
                if (Descripcion.compareTo(root.data.getDescripcion()) < 0)
                    root.left = insercionBusqueda(root.left, publicacion, busqueda);
                else if (Descripcion.compareTo(root.data.getDescripcion()) > 0)
                    root.right = insercionBusqueda(root.right, publicacion, busqueda);
            }
            return root;
        }

        public void borrarArbol(Nodo nodo) {
            root = null;
        }


        //Para recorrerlos en orden creo que se necesitará de un for cuando se implemente el árbol y un arreglo en donde se metan los elementos del árbol
        void inorderRec(Nodo root) {
            if (root != null) {
                inorderRec(root.left);
                //System.out.println(root.data);
                inorderRec(root.right);
            }
        }

    }

}