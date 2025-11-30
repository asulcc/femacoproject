package com.femaco.femacoproject.util;

public class Nodo<T> {
    public T dato;
    public Nodo<T> siguiente;
    
    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
    
    public Nodo(T dato, Nodo<T> siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }
    
    @Override
    public String toString() {
        return dato != null ? dato.toString() : "null";
    }
}
