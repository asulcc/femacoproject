package com.femaco.femacoproject.util;

import java.util.EmptyStackException;

public class Pila<T> {
    private Nodo<T> tope;
    private int tamaño;
    
    public Pila() {
        this.tope = null;
        this.tamaño = 0;
    }
    
    // Apilar elemento
    public void push(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato, tope);
        tope = nuevoNodo;
        tamaño++;
    }
    
    // Desapilar elemento
    public T pop() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        
        T dato = tope.dato;
        tope = tope.siguiente;
        tamaño--;
        return dato;
    }
    
    // Ver tope sin desapilar
    public T peek() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        return tope.dato;
    }
    
    // Métodos de utilidad
    public int tamaño() {
        return tamaño;
    }
    
    public boolean estaVacia() {
        return tope == null;
    }
    
    public void limpiar() {
        tope = null;
        tamaño = 0;
    }
    
    // Buscar elemento (desde el tope)
    public boolean contiene(T dato) {
        Nodo<T> actual = tope;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = tope;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(", ");
            }
            actual = actual.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }
}
