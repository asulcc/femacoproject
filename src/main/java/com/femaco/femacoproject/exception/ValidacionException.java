package com.femaco.femacoproject.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidacionException extends InventarioException {
    private final List<String> erroresValidacion;
    private final String entidad;
    
    // Constructor con lista de errores.
    public ValidacionException(String entidad, List<String> errores) {
        super(crearMensaje(entidad, errores),
              "VALIDACION_FALLIDA", 
              "VALIDACION", 
              "VALIDAR_DATOS");
        this.entidad = entidad;
        this.erroresValidacion = new ArrayList<>(errores);
    }
    
    // Constructor con un solo error.
    public ValidacionException(String entidad, String error) {
        super(crearMensaje(entidad, List.of(error)),
              "VALIDACION_FALLIDA", 
              "VALIDACION", 
              "VALIDAR_DATOS");
        this.entidad = entidad;
        this.erroresValidacion = new ArrayList<>();
        this.erroresValidacion.add(error);
    }
    
    // Constructor con causa.
    public ValidacionException(String mensaje, Throwable causa) {
        super(mensaje, "VALIDACION_FALLIDA", "VALIDACION", "VALIDAR_DATOS", causa);
        this.entidad = "DESCONOCIDA";
        this.erroresValidacion = new ArrayList<>();
    }
    
    private static String crearMensaje(String entidad, List<String> errores) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Error de validación para entidad '").append(entidad).append("': ");
        
        for (int i = 0; i < errores.size(); i++) {
            mensaje.append(errores.get(i));
            if (i < errores.size() - 1) {
                mensaje.append("; ");
            }
        }
        
        return mensaje.toString();
    }
    
    // Getters
    public List<String> getErroresValidacion() {
        return new ArrayList<>(erroresValidacion);
    }
    
    public String getEntidad() {
        return entidad;
    }
    
    // Obtiene la cantidad de errores de validación.
    public int getCantidadErrores() {
        return erroresValidacion.size();
    }
    
    // Agrega un error a la lista de errores.
    public void agregarError(String error) {
        erroresValidacion.add(error);
    }
    
    // Indica si hay errores de validación.
    public boolean tieneErrores() {
        return !erroresValidacion.isEmpty();
    }
    
    // Obtiene todos los errores como una cadena concatenada.
    public String getErroresComoCadena() {
        return String.join("; ", erroresValidacion);
    }
}
