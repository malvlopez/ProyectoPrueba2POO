package ec.edu.sistemalicencias.controller;

import ec.edu.sistemalicencias.dao.UsuarioDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import javax.swing.JOptionPane;

public class LoginController {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String username, String password) {
        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username);

            if (usuario != null && usuario.getPasswordHash().equals(password)) {
                return usuario;
            }
            return null;
        } catch (BaseDatosException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión con Supabase: " + e.getMessage(),
                    "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}