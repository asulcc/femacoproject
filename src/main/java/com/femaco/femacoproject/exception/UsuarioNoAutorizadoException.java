package com.femaco.femacoproject.exception;

public class UsuarioNoAutorizadoException extends InventarioException {
    private final String usuarioId;
    private final String operacion;
    private final String recurso;
    private final String rolRequerido;
    
    // Constructor básico para operación no autorizada.
    public UsuarioNoAutorizadoException(String usuarioId, String operacion) {
        super(crearMensaje(usuarioId, operacion),
              "USUARIO_NO_AUTORIZADO", 
              "SEGURIDAD", 
              "VALIDAR_PERMISOS");
        this.usuarioId = usuarioId;
        this.operacion = operacion;
        this.recurso = "SISTEMA";
        this.rolRequerido = "DESCONOCIDO";
    }
    
    // Constructor con recurso específico.
    public UsuarioNoAutorizadoException(String usuarioId, String operacion, String recurso) {
        super(crearMensajeDetallado(usuarioId, operacion, recurso),
              "USUARIO_NO_AUTORIZADO", 
              "SEGURIDAD", 
              "VALIDAR_PERMISOS");
        this.usuarioId = usuarioId;
        this.operacion = operacion;
        this.recurso = recurso;
        this.rolRequerido = "DESCONOCIDO";
    }
    
    // Constructor con rol requerido.
    public UsuarioNoAutorizadoException(String usuarioId, String operacion, String recurso, String rolRequerido) {
        super(crearMensajeConRol(usuarioId, operacion, recurso, rolRequerido),
              "USUARIO_NO_AUTORIZADO", 
              "SEGURIDAD", 
              "VALIDAR_PERMISOS");
        this.usuarioId = usuarioId;
        this.operacion = operacion;
        this.recurso = recurso;
        this.rolRequerido = rolRequerido;
    }
    
    // Constructor para autenticación fallida.
    public UsuarioNoAutorizadoException(String mensaje) {
        super(mensaje, "USUARIO_NO_AUTORIZADO", "AUTENTICACION", "VALIDAR_CREDENCIALES");
        this.usuarioId = "DESCONOCIDO";
        this.operacion = "AUTENTICAR";
        this.recurso = "SISTEMA";
        this.rolRequerido = "DESCONOCIDO";
    }
    
    // Constructor con causa.
    public UsuarioNoAutorizadoException(String mensaje, Throwable causa) {
        super(mensaje, "USUARIO_NO_AUTORIZADO", "SEGURIDAD", "VALIDAR_PERMISOS", causa);
        this.usuarioId = "DESCONOCIDO";
        this.operacion = "DESCONOCIDA";
        this.recurso = "SISTEMA";
        this.rolRequerido = "DESCONOCIDO";
    }
    
    private static String crearMensaje(String usuarioId, String operacion) {
        return String.format("Usuario '%s' no autorizado para realizar la operación: %s", usuarioId, operacion);
    }
    
    private static String crearMensajeDetallado(String usuarioId, String operacion, String recurso) {
        return String.format("Usuario '%s' no autorizado para '%s' en el recurso '%s'", usuarioId, operacion, recurso);
    }
    
    private static String crearMensajeConRol(String usuarioId, String operacion, String recurso, String rolRequerido) {
        return String.format("Usuario '%s' no autorizado para '%s' en '%s'. Rol requerido: %s", 
                           usuarioId, operacion, recurso, rolRequerido);
    }
    
    // Getters
    public String getUsuarioId() {
        return usuarioId;
    }
    
    public String getOperacion() {
        return operacion;
    }
    
    public String getRecurso() {
        return recurso;
    }
    
    public String getRolRequerido() {
        return rolRequerido;
    }
    
    // Indica si es un error de autenticación.
    public boolean esErrorAutenticacion() {
        return "AUTENTICACION".equals(getModulo());
    }
    
    // Indica si es un error de autorización.
    public boolean esErrorAutorizacion() {
        return "SEGURIDAD".equals(getModulo());
    }
}
