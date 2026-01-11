package ec.edu.sistemalicencias.controller;

import ec.edu.sistemalicencias.dao.UsuarioDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import javax.swing.JOptionPane;

public class LoginController {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String username, String password) {
        try {
            return usuarioDAO.buscarPorUsername(username, password);
        } catch (BaseDatosException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión con Supabase: " + e.getMessage(),
                    "Error Crítico", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}