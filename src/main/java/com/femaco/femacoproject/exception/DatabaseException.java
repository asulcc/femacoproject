package com.femaco.femacoproject.exception;

import java.sql.SQLException;

public class DatabaseException extends InventarioException {
    private final String consultaSQL;
    private final String codigoErrorSQL;
    private final String estadoSQL;
    
    /** Constructor a partir de una SQLException. */
    public DatabaseException(SQLException sqlException, String consultaSQL) {
        super(crearMensaje(sqlException, consultaSQL),
              "ERROR_BASE_DATOS", 
              "PERSISTENCIA", 
              "EJECUTAR_CONSULTA",
              sqlException);
        this.consultaSQL = consultaSQL;
        this.codigoErrorSQL = String.valueOf(sqlException.getErrorCode());
        this.estadoSQL = sqlException.getSQLState();
    }
    
    /** Constructor para errores de conexión. */
    public DatabaseException(String mensaje, String consultaSQL, Throwable causa) {
        super(mensaje, "ERROR_CONEXION_BD", "PERSISTENCIA", "CONECTAR_BD", causa);
        this.consultaSQL = consultaSQL;
        this.codigoErrorSQL = "N/A";
        this.estadoSQL = "N/A";
    }
    
    private static String crearMensaje(SQLException sqlException, String consultaSQL) {
        return String.format("Error de base de datos ejecutando consulta: %s. Error: %s (Código: %d, Estado: %s)",
                           consultaSQL, sqlException.getMessage(), 
                           sqlException.getErrorCode(), sqlException.getSQLState());
    }
    
    // Getters
    public String getConsultaSQL() {
        return consultaSQL;
    }
    
    public String getCodigoErrorSQL() {
        return codigoErrorSQL;
    }
    
    public String getEstadoSQL() {
        return estadoSQL;
    }
    
    /** Indica si es un error de conexión. */
    public boolean esErrorConexion() {
        return "ERROR_CONEXION_BD".equals(getCodigoError());
    }
    
    /** Indica si es un error de integridad referencial. */
    public boolean esErrorIntegridadReferencial() {
        return "23000".equals(estadoSQL); // Código SQLState para violación de restricción
    }
    
    /** Indica si es un error de duplicado. */
    public boolean esErrorDuplicado() {
        return "23505".equals(estadoSQL); // Código SQLState para violación de unique constraint
    }
    
    /** Obtiene la SQLException original si existe. */
    public SQLException getSQLExceptionOriginal() {
        Throwable causa = getCause();
        return (causa instanceof SQLException) ? (SQLException) causa : null;
    }
}
