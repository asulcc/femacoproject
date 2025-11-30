package com.femaco.femacoproject.exception;

/**
 * Excepción lanzada cuando un usuario no existe en el sistema.
 * Se utiliza en operaciones de búsqueda, actualización o autenticación de usuarios.
 */
public class UsuarioNoEncontradoException extends InventarioException {
    private final String usuarioId;
    private final String criterioBusqueda;
    
    /**
     * Constructor con ID del usuario.
     */
    public UsuarioNoEncontradoException(String usuarioId) {
        super(crearMensajePorId(usuarioId),
              "USUARIO_NO_ENCONTRADO", 
              "GESTION_USUARIOS", 
              "BUSCAR_USUARIO");
        this.usuarioId = usuarioId;
        this.criterioBusqueda = "ID";
    }
    
    /**
     * Constructor con username.
     */
    public UsuarioNoEncontradoException(String criterioBusqueda, String valor) {
        super(crearMensajePorCriterio(criterioBusqueda, valor),
              "USUARIO_NO_ENCONTRADO", 
              "GESTION_USUARIOS", 
              "BUSCAR_USUARIO");
        this.usuarioId = valor;
        this.criterioBusqueda = criterioBusqueda;
    }
    
    /**
     * Constructor con causa.
     */
    public UsuarioNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, "USUARIO_NO_ENCONTRADO", "GESTION_USUARIOS", "BUSCAR_USUARIO", causa);
        this.usuarioId = "DESCONOCIDO";
        this.criterioBusqueda = "DESCONOCIDO";
    }
    
    private static String crearMensajePorId(String usuarioId) {
        return String.format("Usuario con ID '%s' no encontrado en el sistema", usuarioId);
    }
    
    private static String crearMensajePorCriterio(String criterio, String valor) {
        return String.format("Usuario no encontrado usando criterio '%s' con valor '%s'", criterio, valor);
    }
    
    // Getters
    public String getUsuarioId() {
        return usuarioId;
    }
    
    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }
    
    /**
     * Indica si la búsqueda fue por ID.
     */
    public boolean esBusquedaPorId() {
        return "ID".equals(criterioBusqueda);
    }
    
    /**
     * Indica si la búsqueda fue por username.
     */
    public boolean esBusquedaPorUsername() {
        return "USERNAME".equals(criterioBusqueda);
    }
}
