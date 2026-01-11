package ec.edu.sistemalicencias.service;

import ec.edu.sistemalicencias.dao.UsuarioDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.AutenticacionException;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String username, String password) throws AutenticacionException {
        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username);

            if (usuario == null) {
                throw new AutenticacionException("El nombre de usuario no existe.");
            }

            if (!usuario.getPasswordHash().equals(password)) {
                throw new AutenticacionException("La contraseña ingresada es incorrecta.");
            }

            return usuario;
        } catch (BaseDatosException e) {
            throw new AutenticacionException("Error de conexión con el servidor remoto.");
        }
    }
}