package ec.edu.sistemalicencias;

import ec.edu.sistemalicencias.config.DatabaseConfig;
import ec.edu.sistemalicencias.view.LoginView;
import javax.swing.*;

public class Main {


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error de Look and Feel: " + e.getMessage());
        }

        DatabaseConfig dbConfig = DatabaseConfig.getInstance();

        SwingUtilities.invokeLater(() -> {
            mostrarPantallaInicio();

            if (!dbConfig.verificarConexion()) {
                mostrarErrorConexion();
                return;
            }

            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }

    private static void mostrarPantallaInicio() {
        JOptionPane.showMessageDialog(
                null,
                "SISTEMA DE LICENCIAS DE CONDUCIR - ECUADOR\n\n" +
                        "Agencia Nacional de Tránsito\n" +
                        "Versión 1.0 (Cloud Edition)\n\n" +
                        "Tecnologías:\n" +
                        "- Java 21 (LTS)\n" +
                        "- Cloud Database: PostgreSQL (Supabase)\n" +
                        "- Seguridad: Roles de Usuario (RBAC)\n" +
                        "- Reportes: Generación iText PDF\n\n" +
                        "Estableciendo conexión con el servidor remoto...",
                "Bienvenido",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void mostrarErrorConexion() {
        String mensaje = "ERROR DE CONEXIÓN AL SERVIDOR REMOTO\n\n" +
                "No se pudo establecer comunicación con la base de datos en Supabase.\n\n" +
                "Por favor, verifique:\n" +
                "1. Su conexión a Internet.\n" +
                "2. Que el Host 'aws-1-us-east-1.pooler.supabase.com' sea accesible.\n" +
                "3. Que el Pooler de la base de datos no haya alcanzado su límite.\n" +
                "4. Que las credenciales en 'DatabaseConfig' sigan vigentes.\n\n" +
                "Si el problema persiste, contacte al administrador de TI.";

        JOptionPane.showMessageDialog(
                null,
                mensaje,
                "Fallo de Infraestructura Cloud",
                JOptionPane.ERROR_MESSAGE
        );

        System.exit(1);
    }
}