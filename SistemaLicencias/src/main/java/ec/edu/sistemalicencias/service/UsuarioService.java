package ec.edu.sistemalicencias.service;

import ec.edu.sistemalicencias.dao.UsuarioDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.AutenticacionException;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String username, String password)
            throws AutenticacionException {

        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        if (usuario == null) {
            throw new AutenticacionException("Usuario no encontrado");
        }

        if (!usuario.getPasswordHash().equals(password)) {
            throw new AutenticacionException("Contraseña incorrecta");
        }

        return usuario;
    }
}
