package com.femaco.femacoproject.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private int tamaño;
    
    public ListaEnlazada() {
        this.cabeza = null;
        this.tamaño = 0;
    }
    
    // Agregar al final
    public void agregar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamaño++;
    }
    
    // Agregar al inicio
    public void agregarAlInicio(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato, cabeza);
        cabeza = nuevoNodo;
        tamaño++;
    }
    
    // Eliminar por dato
    public boolean eliminar(T dato) {
        if (cabeza == null) return false;
        
        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            tamaño--;
            return true;
        }
        
        Nodo<T> actual = cabeza;
        while (actual.siguiente != null && !actual.siguiente.dato.equals(dato)) {
            actual = actual.siguiente;
        }
        
        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            tamaño--;
            return true;
        }
        return false;
    }
    
    // Buscar dato
    public boolean contiene(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }
    
    // Obtener por índice
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }
    
    // Filtrar lista
    public ListaEnlazada<T> filtrar(Predicate<T> predicado) {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        for (T elemento : this) {
            if (predicado.test(elemento)) {
                resultado.agregar(elemento);
            }
        }
        return resultado;
    }
    
    // Convertir a array
    @SuppressWarnings("unchecked")
    public T[] toArray(T[] arreglo) {
        if (arreglo.length < tamaño) {
            arreglo = (T[]) java.lang.reflect.Array.newInstance(
                arreglo.getClass().getComponentType(), tamaño);
        }
        
        int i = 0;
        for (T elemento : this) {
            arreglo[i++] = elemento;
        }
        
        if (arreglo.length > tamaño) {
            arreglo[tamaño] = null;
        }
        
        return arreglo;
    }
    
    // Métodos de utilidad
    public int tamaño() {
        return tamaño;
    }
    
    public boolean estaVacia() {
        return cabeza == null;
    }
    
    public void limpiar() {
        cabeza = null;
        tamaño = 0;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new IteradorListaEnlazada(cabeza);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = cabeza;
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
    
    // Clase interna Iterador
    private class IteradorListaEnlazada implements Iterator<T> {
        private Nodo<T> actual;
        
        public IteradorListaEnlazada(Nodo<T> cabeza) {
            this.actual = cabeza;
        }
        
        @Override
        public boolean hasNext() {
            return actual != null;
        }
        
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T dato = actual.dato;
            actual = actual.siguiente;
            return dato;
        }
    }
}
