package ec.edu.sistemalicencias.controller;

import ec.edu.sistemalicencias.dao.LoginDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.JOptionPane;

public class LoginController {
    private final LoginDAO loginDAO = new LoginDAO();

    public Usuario login(String username, String password) {
        try {
            Usuario usuario = loginDAO.buscarPorUsername(username);

            if (usuario != null && BCrypt.checkpw(password, usuario.getPasswordHash())) {
                loginDAO.insertarRegistro(usuario.getId());
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