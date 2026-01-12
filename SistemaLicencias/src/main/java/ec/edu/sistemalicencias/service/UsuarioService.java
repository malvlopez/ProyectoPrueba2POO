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

    public void registrarNuevoUsuario(Usuario usuario) throws BaseDatosException {
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().trim().isEmpty() ||
                usuario.getUsername() == null || usuario.getUsername().trim().isEmpty() ||
                usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty() ||
                usuario.getRol() == null) {
            throw new BaseDatosException("Todos los campos son obligatorios.");
        }

        if (usuarioDAO.existeUsername(usuario.getUsername())) {
            throw new BaseDatosException("El nombre de usuario '" + usuario.getUsername() + "' ya está registrado.");
        }

        usuarioDAO.insertar(usuario);
    }

    public Usuario obtenerUsuarioPorId(long id) throws BaseDatosException {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new BaseDatosException("No se encontró ningún usuario con el ID: " + id);
        }
        return usuario;
    }

    public void modificarUsuario(Usuario usuario) throws BaseDatosException {
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().trim().isEmpty()) {
            throw new BaseDatosException("El nombre completo es obligatorio.");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new BaseDatosException("El nombre de usuario no puede quedar vacío.");
        }
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) {
            throw new BaseDatosException("La contraseña no puede quedar vacía.");
        }

        usuarioDAO.actualizar(usuario);
    }

    public void alternarEstadoUsuario(long id, boolean estadoActual) throws BaseDatosException {
        boolean nuevoEstado = !estadoActual;
        usuarioDAO.actualizarEstado(id, nuevoEstado);
    }
}