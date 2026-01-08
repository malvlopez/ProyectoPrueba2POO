package ec.edu.sistemalicencias.config;

import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static DatabaseConfig instancia;
    private final String url;
    private final String usuario;
    private final String password;
    private final String driver;

    private DatabaseConfig() {
        this.driver = "org.postgresql.Driver";
        this.url = "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres";
        this.usuario = "postgres.smyfckywiqznbdirnerh";
        this.password = "RootAdmin1234*";

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver PostgreSQL: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConfig();
        }
        return instancia;
    }

    public Connection obtenerConexion() throws BaseDatosException {
        try {
            return DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new BaseDatosException("Error al conectar con PostgreSQL Cloud: " + e.getMessage(), e);
        }
    }

    public void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public boolean verificarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getUrl() { return url; }
    public String getUsuario() { return usuario; }
    public String getDriver() { return driver; }
}