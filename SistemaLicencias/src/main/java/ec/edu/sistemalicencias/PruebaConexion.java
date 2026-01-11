package ec.edu.sistemalicencias;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        try {
            DatabaseConfig config = DatabaseConfig.getInstance();
            Connection conn = config.obtenerConexion();

            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ ¡CONEXIÓN EXITOSA!");
                System.out.println("Tu sistema ya está conectado a PostgreSQL en Supabase.");
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR DE CONEXIÓN:");
            e.printStackTrace();
        }
    }
}