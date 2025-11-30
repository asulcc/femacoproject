package com.femaco.femacoproject.util;

import java.util.NoSuchElementException;

public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamaño;
    
    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamaño = 0;
    }
    
    // Encolar elemento
    public void encolar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        
        if (estaVacia()) {
            frente = nuevoNodo;
        } else {
            fin.siguiente = nuevoNodo;
        }
        fin = nuevoNodo;
        tamaño++;
    }
    
    // Desencolar elemento
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("Cola vacía");
        }
        
        T dato = frente.dato;
        frente = frente.siguiente;
        
        if (frente == null) {
            fin = null;
        }
        
        tamaño--;
        return dato;
    }
    
    // Ver frente sin desencolar
    public T frente() {
        if (estaVacia()) {
            throw new NoSuchElementException("Cola vacía");
        }
        return frente.dato;
    }
    
    // Métodos de utilidad
    public int tamaño() {
        return tamaño;
    }
    
    public boolean estaVacia() {
        return frente == null;
    }
    
    public void limpiar() {
        frente = null;
        fin = null;
        tamaño = 0;
    }
    
    // Buscar elemento
    public boolean contiene(T dato) {
        Nodo<T> actual = frente;
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
        Nodo<T> actual = frente;
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
