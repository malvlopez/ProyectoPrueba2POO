package ec.edu.sistemalicencias.dao;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.model.entities.LoginRegistro;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public Usuario buscarPorUsername(String username) throws BaseDatosException {
        String sql = "SELECT id, username, password_hash, rol, estado, nombre_completo FROM usuarios WHERE username = ? AND estado = true";

        try (Connection conn = dbConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
            return null;
        } catch (SQLException e) {
            throw new BaseDatosException("Error al consultar usuario en la nube: " + e.getMessage());
        }
    }

    public void insertarRegistro(long idUsuario) throws BaseDatosException {
        String sql = "INSERT INTO registro_logins (id_usuario) VALUES (?)";
        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, idUsuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error al registrar el inicio de sesión: " + e.getMessage());
        }
    }

    public List<LoginRegistro> obtenerTodos() throws BaseDatosException {
        List<LoginRegistro> lista = new ArrayList<>();
        String sql = "SELECT l.id_login, l.id_usuario, u.username, l.fecha_ingreso " +
                "FROM registro_logins l " +
                "JOIN usuarios u ON l.id_usuario = u.id " +
                "ORDER BY l.fecha_ingreso DESC";

        try (Connection conn = DatabaseConfig.getInstance().obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LoginRegistro reg = new LoginRegistro();
                reg.setIdLogin(rs.getInt("id_login"));
                reg.setIdUsuario(rs.getLong("id_usuario"));
                reg.setUsername(rs.getString("username"));
                reg.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
                lista.add(reg);
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error al obtener historial de logins: " + e.getMessage());
        }
        return lista;
    }
}