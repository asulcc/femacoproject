package com.femaco.femacoproject.util;

import java.util.ArrayList;
import java.util.List;

public class ArbolBinarioBusqueda<T extends Comparable<T>> {
    private NodoArbol<T> raiz;
    private int tamaño;
    
    public ArbolBinarioBusqueda() {
        this.raiz = null;
        this.tamaño = 0;
    }
    
    // Insertar elemento
    public void insertar(T dato) {
        raiz = insertarRecursivo(raiz, dato);
        tamaño++;
    }
    
    private NodoArbol<T> insertarRecursivo(NodoArbol<T> nodo, T dato) {
        if (nodo == null) {
            return new NodoArbol<>(dato);
        }
        
        if (dato.compareTo(nodo.dato) < 0) {
            nodo.izquierdo = insertarRecursivo(nodo.izquierdo, dato);
        } else if (dato.compareTo(nodo.dato) > 0) {
            nodo.derecho = insertarRecursivo(nodo.derecho, dato);
        }
        // Si son iguales, no insertamos duplicados
        
        return nodo;
    }
    
    // Buscar elemento
    public T buscar(T dato) {
        return buscarRecursivo(raiz, dato);
    }
    
    private T buscarRecursivo(NodoArbol<T> nodo, T dato) {
        if (nodo == null) {
            return null;
        }
        
        if (dato.compareTo(nodo.dato) == 0) {
            return nodo.dato;
        } else if (dato.compareTo(nodo.dato) < 0) {
            return buscarRecursivo(nodo.izquierdo, dato);
        } else {
            return buscarRecursivo(nodo.derecho, dato);
        }
    }
    
    // Eliminar elemento
    public boolean eliminar(T dato) {
        int tamañoAnterior = tamaño;
        raiz = eliminarRecursivo(raiz, dato);
        return tamaño != tamañoAnterior;
    }
    
    private NodoArbol<T> eliminarRecursivo(NodoArbol<T> nodo, T dato) {
        if (nodo == null) return null;
        
        if (dato.compareTo(nodo.dato) < 0) {
            nodo.izquierdo = eliminarRecursivo(nodo.izquierdo, dato);
        } else if (dato.compareTo(nodo.dato) > 0) {
            nodo.derecho = eliminarRecursivo(nodo.derecho, dato);
        } else {
            // Nodo encontrado
            tamaño--;
            
            // Caso 1: Nodo hoja
            if (nodo.esHoja()) {
                return null;
            }
            // Caso 2: Nodo con un hijo
            else if (nodo.tieneUnHijo()) {
                return (nodo.izquierdo != null) ? nodo.izquierdo : nodo.derecho;
            }
            // Caso 3: Nodo con dos hijos
            else {
                T sucesor = encontrarMinimo(nodo.derecho);
                nodo.dato = sucesor;
                nodo.derecho = eliminarRecursivo(nodo.derecho, sucesor);
                tamaño++; // Compensar porque eliminamos el sucesor
            }
        }
        return nodo;
    }
    
    private T encontrarMinimo(NodoArbol<T> nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo.dato;
    }
    
    // Actualizar elemento
    public void actualizar(T datoActualizado) {
        if (eliminar(datoActualizado)) {
            insertar(datoActualizado);
        }
    }
    
    // Recorridos
    public List<T> inOrder() {
        List<T> resultado = new ArrayList<>();
        inOrderRecursivo(raiz, resultado);
        return resultado;
    }
    
    private void inOrderRecursivo(NodoArbol<T> nodo, List<T> resultado) {
        if (nodo != null) {
            inOrderRecursivo(nodo.izquierdo, resultado);
            resultado.add(nodo.dato);
            inOrderRecursivo(nodo.derecho, resultado);
        }
    }
    
    public List<T> preOrder() {
        List<T> resultado = new ArrayList<>();
        preOrderRecursivo(raiz, resultado);
        return resultado;
    }
    
    private void preOrderRecursivo(NodoArbol<T> nodo, List<T> resultado) {
        if (nodo != null) {
            resultado.add(nodo.dato);
            preOrderRecursivo(nodo.izquierdo, resultado);
            preOrderRecursivo(nodo.derecho, resultado);
        }
    }
    
    public List<T> postOrder() {
        List<T> resultado = new ArrayList<>();
        postOrderRecursivo(raiz, resultado);
        return resultado;
    }
    
    private void postOrderRecursivo(NodoArbol<T> nodo, List<T> resultado) {
        if (nodo != null) {
            postOrderRecursivo(nodo.izquierdo, resultado);
            postOrderRecursivo(nodo.derecho, resultado);
            resultado.add(nodo.dato);
        }
    }
    
    // Métodos de utilidad
    public int tamaño() {
        return tamaño;
    }
    
    public boolean estaVacio() {
        return raiz == null;
    }
    
    public int altura() {
        return calcularAltura(raiz);
    }
    
    private int calcularAltura(NodoArbol<T> nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(calcularAltura(nodo.izquierdo), calcularAltura(nodo.derecho));
    }
    
    public boolean contiene(T dato) {
        return buscar(dato) != null;
    }
    
    public void limpiar() {
        raiz = null;
        tamaño = 0;
    }
    
    @Override
    public String toString() {
        return inOrder().toString();
    }
}
