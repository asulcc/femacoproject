package com.femaco.femacoproject.util;

public class NodoArbol<T> {
    public T dato;
    public NodoArbol<T> izquierdo;
    public NodoArbol<T> derecho;
    
    public NodoArbol(T dato) {
        this.dato = dato;
        this.izquierdo = null;
        this.derecho = null;
    }
    
    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }
    
    public boolean tieneUnHijo() {
        return (izquierdo != null && derecho == null) || 
               (izquierdo == null && derecho != null);
    }
    
    @Override
    public String toString() {
        return dato != null ? dato.toString() : "null";
    }
}
