package ec.edu.sistemalicencias.controller;
import ec.edu.sistemalicencias.model.entities.Usuario;

public class UsuarioSesion {

    private static Usuario usuarioActual;

    private UsuarioSesion() {}

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static boolean estaLogueado() {
        return usuarioActual != null;
    }

    public static String getRol() {
        return usuarioActual != null ? usuarioActual.getRol() : null;
    }
}
