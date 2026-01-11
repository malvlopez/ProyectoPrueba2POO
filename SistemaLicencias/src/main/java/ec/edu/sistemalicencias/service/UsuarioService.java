package ec.edu.sistemalicencias.service;

import ec.edu.sistemalicencias.dao.UsuarioDAO;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.AutenticacionException;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String username, String password)
            throws AutenticacionException {

        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username, password);

            if (usuario == null) {
                throw new AutenticacionException(
                        "Usuario o contraseña incorrectos."
                );
            }

            return usuario;

        } catch (BaseDatosException e) {
            throw new AutenticacionException(
                    "Error de conexión con el servidor remoto."
            );
        }
    }
}
