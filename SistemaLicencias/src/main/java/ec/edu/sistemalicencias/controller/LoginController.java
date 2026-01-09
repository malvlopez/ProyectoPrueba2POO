package ec.edu.sistemalicencias.controller;

import ec.edu.sistemalicencias.dao.UsuarioDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;

import javax.swing.*;

/**
 * Controlador para el login del sistema.
 * Se encarga de autenticar usuarios y devolver su información de rol.
 * No maneja sesión persistente, solo valida credenciales.
 */
public class LoginController {

    private final UsuarioDAO usuarioDAO;

    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Intenta hacer login con el username y password ingresados.
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Usuario si es correcto, null si no
     */
    public Usuario login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese usuario y contraseña", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Usuario usuario = usuarioDAO.login(username, password);

        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return usuario;
    }

    /**
     * Valida si un usuario es administrador
     * @param usuario Usuario
     * @return true si es admin
     */
    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol());
    }

    /**
     * Valida si un usuario es analista
     * @param usuario Usuario
     * @return true si es analista
     */
    public boolean esAnalista(Usuario usuario) {
        return usuario != null && "ANALISTA".equalsIgnoreCase(usuario.getRol());
    }
}
