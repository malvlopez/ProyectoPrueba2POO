package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import java.sql.*;

public class UsuarioDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public Usuario buscarPorUsername(String username, String password) throws BaseDatosException {
        String sql = " SELECT id, username, password_hash, rol, estado, nombre_completo FROM usuarios WHERE username = ? AND estado = true AND password_hash = crypt(?, password_hash)";


        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getBoolean("estado"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    return u;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new BaseDatosException("Error al consultar usuario en la nube: " + e.getMessage());
        }
    }
}