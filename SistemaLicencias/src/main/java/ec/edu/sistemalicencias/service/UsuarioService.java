package ec.edu.sistemalicencias.service;

import ec.edu.sistemalicencias.dao.LoginDAO;
import ec.edu.sistemalicencias.dao.UsuarioDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.AutenticacionException;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;

import java.util.List;

public class UsuarioService {
    private final LoginDAO loginDAO = new LoginDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String username, String password) throws AutenticacionException {
        try {
            Usuario usuario = loginDAO.buscarPorUsername(username);

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

    public List<Usuario> obtenerTodosLosUsuarios() throws BaseDatosException {
        try {
            return usuarioDAO.listarTodos();
        } catch (BaseDatosException e) {
            throw new BaseDatosException("Error en el servicio al recuperar usuarios: " + e.getMessage());
        }
    }
}