package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;

import java.sql.*;

public class UsuarioDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public Usuario buscarPorUsername(String username) {

        String sql = "SELECT * FROM usuarios WHERE username = ? AND estado = true";

        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getBoolean("estado"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                return u;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario", e);
        } catch (BaseDatosException e) {
            throw new RuntimeException(e);
        }
    }

    public Usuario login(String username, String password) {

        String sql = "SELECT * FROM usuarios " +
                "WHERE username = ? AND password_hash = ? AND estado = true";

        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // luego será hash

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getBoolean("estado"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                return u;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error en login", e);
        } catch (BaseDatosException e) {
            throw new RuntimeException(e);
        }
    }

}
