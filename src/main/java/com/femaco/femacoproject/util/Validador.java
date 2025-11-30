package com.femaco.femacoproject.util;

import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.Proveedor;
import com.femaco.femacoproject.model.Usuario;
import java.util.regex.Pattern;
import java.util.Date;

public class Validador {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern TELEFONO_PATTERN = 
        Pattern.compile("^[0-9]{7,15}$");
    private static final Pattern ID_PATTERN = 
        Pattern.compile("^[A-Z0-9_]{3,20}$");
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    private Validador() {
        // Clase de utilidad, no instanciable
    }
    
    // Validación de Productos
    public static boolean validarProducto(Producto producto) {
        if (producto == null) return false;
        
        return validarId(producto.getId()) &&
               validarTextoNoVacio(producto.getNombre(), 2, 100) &&
               producto.getCategoria() != null &&
               validarStock(producto.getStockActual()) &&
               validarStock(producto.getStockMinimo()) &&
               validarPrecio(producto.getPrecio()) &&
               validarTextoNoVacio(producto.getUbicacion(), 1, 20);
    }
    
    // Validación de Usuarios
    public static boolean validarUsuario(Usuario usuario) {
        if (usuario == null) return false;
        
        return validarId(usuario.getId()) &&
               validarUsername(usuario.getUsername()) &&
               validarPassword(usuario.getPassword()) &&
               validarTextoNoVacio(usuario.getNombreCompleto(), 5, 100) &&
               validarEmailOpcional(usuario.getEmail()) &&
               usuario.getRol() != null;
    }
    
    // Validación de Proveedores
    public static boolean validarProveedor(Proveedor proveedor) {
        if (proveedor == null) return false;
        
        return validarId(proveedor.getId()) &&
               validarTextoNoVacio(proveedor.getNombre(), 3, 100) &&
               validarTelefonoOpcional(proveedor.getTelefono()) &&
               validarEmailOpcional(proveedor.getEmail());
    }
    
    // Validaciones específicas
    public static boolean validarId(String id) {
        return id != null && ID_PATTERN.matcher(id).matches();
    }
    
    public static boolean validarTextoNoVacio(String texto, int min, int max) {
        return texto != null && 
               texto.trim().length() >= min && 
               texto.trim().length() <= max;
    }
    
    public static boolean validarEmail(String email) {
        return email != null && 
               EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean validarEmailOpcional(String email) {
        return email == null || email.trim().isEmpty() || validarEmail(email);
    }
    
    public static boolean validarTelefono(String telefono) {
        return telefono != null && 
               TELEFONO_PATTERN.matcher(telefono).matches();
    }
    
    public static boolean validarTelefonoOpcional(String telefono) {
        return telefono == null || telefono.trim().isEmpty() || validarTelefono(telefono);
    }
    
    public static boolean validarUsername(String username) {
        return username != null && 
               USERNAME_PATTERN.matcher(username).matches();
    }
    
    public static boolean validarPassword(String password) {
        return password != null && 
               password.length() >= 6 && 
               password.length() <= 100;
    }
    
    public static boolean validarStock(int stock) {
        return stock >= 0;
    }
    
    public static boolean validarPrecio(double precio) {
        return precio >= 0 && precio <= 1000000; // Máximo 1 millón
    }
    
    public static boolean validarCantidadMovimiento(int cantidad) {
        return cantidad > 0 && cantidad <= 10000; // Máximo 10,000 por movimiento
    }
    
    public static boolean validarFechaNoFutura(Date fecha) {
        return fecha != null && !fecha.after(new Date());
    }
    
    public static boolean validarRangoFechas(Date fechaInicio, Date fechaFin) {
        return fechaInicio != null && 
               fechaFin != null && 
               !fechaInicio.after(fechaFin);
    }
    
    public static String limpiarTexto(String texto) {
        if (texto == null) return null;
        return texto.trim().replaceAll("\\s+", " ");
    }
    
    public static String formatearId(String id) {
        if (id == null) return null;
        return id.trim().toUpperCase().replaceAll("\\s+", "_");
    }
}
