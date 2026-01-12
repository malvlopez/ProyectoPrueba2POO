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

    public boolean existeUsername(String username) throws BaseDatosException {
        String sql = "SELECT 1 FROM usuarios WHERE username = ?";
        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error al verificar disponibilidad del username: " + e.getMessage());
        }
    }

    public Usuario buscarPorId(long id) throws BaseDatosException {
        String sql = "SELECT id, username, rol, nombre_completo, estado FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setEstado(rs.getBoolean("estado"));
                    return u;
                }
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    public void actualizar(Usuario usuario) throws BaseDatosException {
        String sql = "UPDATE usuarios SET username = ?, rol = ?, nombre_completo = ?, estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getRol());
            pstmt.setString(3, usuario.getNombreCompleto());
            pstmt.setBoolean(4, usuario.isEstado());
            pstmt.setLong(5, usuario.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error al actualizar los datos: " + e.getMessage());
        }
    }

    public void actualizarEstado(long id, boolean nuevoEstado) throws BaseDatosException {
        String sql = "UPDATE usuarios SET estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, nuevoEstado);
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error al cambiar el estado del usuario: " + e.getMessage());
        }
    }
}