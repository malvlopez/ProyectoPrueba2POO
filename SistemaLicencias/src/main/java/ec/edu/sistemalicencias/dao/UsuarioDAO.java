package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> listarTodos() throws BaseDatosException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, username, rol, estado, nombre_completo FROM usuarios ORDER BY id ASC";

        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getBoolean("estado"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                usuarios.add(u);
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error al consultar la lista de usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public void insertar(Usuario usuario) throws BaseDatosException {
        String sql = "INSERT INTO usuarios (username, password_hash, rol, nombre_completo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPasswordHash());
            pstmt.setString(3, usuario.getRol());
            pstmt.setString(4, usuario.getNombreCompleto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error al registrar el usuario: " + e.getMessage());
        }
    }
}