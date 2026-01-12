package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.controller.UsuarioSesion;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.entities.Usuario;
import ec.edu.sistemalicencias.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainView extends JFrame {

    private final LicenciaController controller;
    private final Usuario usuarioActual;

    private JPanel mainPanel;
    private JButton btnGestionConductores;
    private JButton btnValidarDocumentos;
    private JButton btnPruebasPsicometricas;
    private JButton btnEmitirLicencia;
    private JButton btnConsultarLicencias;
    private JButton btnGenerarDocumento;
    private JButton btnSalir;
    private JLabel lblVersion;
    private JButton btnAdmin;

    public MainView() {
        this.controller = new LicenciaController();
        this.usuarioActual = UsuarioSesion.getUsuario();

        if (!UsuarioSesion.estaLogueado()) {
            JOptionPane.showMessageDialog(null, "Acceso no autorizado. Por favor inicie sesión.");
            System.exit(0);
        }

        $$$setupUI$$$();
        setTitle("Sistema de Licencias - Bienvenido: " + usuarioActual.getNombreCompleto());
        setContentPane(mainPanel);
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        aplicarPermisosPorRol();
        configurarEventos();
        configurarEstilos();
    }

    private void aplicarPermisosPorRol() {
        boolean isAdmin = "ADMINISTRADOR".equalsIgnoreCase(usuarioActual.getRol());

        btnGestionConductores.setEnabled(true);
        btnValidarDocumentos.setEnabled(true);
        btnPruebasPsicometricas.setEnabled(true);
        btnConsultarLicencias.setEnabled(true);
        btnGenerarDocumento.setEnabled(true);

        // Restricciones de seguridad
        btnEmitirLicencia.setEnabled(isAdmin);
        btnAdmin.setEnabled(isAdmin);

        if (lblVersion != null) {
            lblVersion.setText("Sesión activa: " + usuarioActual.getNombreCompleto() +
                    " | Rol: " + usuarioActual.getRol());
        }
    }

    private void configurarEventos() {
        btnGestionConductores.addActionListener(e -> abrirGestionConductores());
        btnValidarDocumentos.addActionListener(e -> abrirValidarDocumentos());
        btnPruebasPsicometricas.addActionListener(e -> abrirPruebasPsicometricas());
        btnEmitirLicencia.addActionListener(e -> abrirEmitirLicencia());
        btnConsultarLicencias.addActionListener(e -> abrirConsultarLicencias());
        btnGenerarDocumento.addActionListener(e -> generarDocumentoPDF());
        btnSalir.addActionListener(e -> salirAplicacion());

        btnAdmin.addActionListener(e -> {
            if ("ADMINISTRADOR".equalsIgnoreCase(usuarioActual.getRol())) {
                new GestionUsuariosView().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Acceso no autorizado.");
            }
        });
    }

    private void configurarEstilos() {
        btnSalir.setBackground(new Color(220, 53, 69));
        btnSalir.setForeground(Color.WHITE);
        btnAdmin.setBackground(new Color(53, 120, 220));

        Cursor mano = new Cursor(Cursor.HAND_CURSOR);
        btnGestionConductores.setCursor(mano);
        btnValidarDocumentos.setCursor(mano);
        btnPruebasPsicometricas.setCursor(mano);
        btnEmitirLicencia.setCursor(mano);
        btnConsultarLicencias.setCursor(mano);
        btnGenerarDocumento.setCursor(mano);
        btnSalir.setCursor(mano);
    }

    private void abrirGestionConductores() {
        new GestionConductoresView(controller).setVisible(true);
    }

    private void abrirValidarDocumentos() {
        new ValidarDocumentosView(controller).setVisible(true);
    }

    private void abrirPruebasPsicometricas() {
        new PruebasPsicometricasView(controller).setVisible(true);
    }

    private void abrirEmitirLicencia() {
        new EmitirLicenciaView(controller).setVisible(true);
    }

    private void abrirConsultarLicencias() {
        new ConsultarLicenciasView(controller).setVisible(true);
    }

    private void generarDocumentoPDF() {
        try {
            String cedula = JOptionPane.showInputDialog(this, "Ingrese la cédula del conductor:");
            if (cedula != null && !cedula.trim().isEmpty()) {
                Conductor conductor = controller.buscarConductorPorCedula(cedula.trim());
                if (conductor == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró el conductor.");
                    return;
                }

                List<Licencia> licencias = controller.obtenerLicenciasConductor(conductor.getId());
                if (licencias.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No existen licencias.");
                    return;
                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("Licencia_" + cedula + ".pdf"));

                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String ruta = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!ruta.toLowerCase().endsWith(".pdf")) ruta += ".pdf";

                    PruebaPsicometrica prueba = controller.obtenerUltimaPruebaAprobada(conductor.getId());
                    PDFGenerator.generarLicenciaPDF(licencias.get(0), conductor, prueba, ruta);
                    Desktop.getDesktop().open(new File(ruta));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void salirAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            UsuarioSesion.cerrarSesion();
            new LoginView().setVisible(true);
            this.dispose();
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, -1));
        mainPanel.setMinimumSize(new Dimension(800, 600));
        mainPanel.setPreferredSize(new Dimension(800, 600));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 20, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Arial", -1, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Agencia Nacional de Tránsito");
        panel1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$("Arial", Font.BOLD, 24, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setForeground(new Color(-14121484));
        label2.setText("SISTEMA DE LICENCIAS DE CONDUCIR - ECUADOR");
        panel1.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Módulos del Sistema", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        btnGestionConductores = new JButton();
        Font btnGestionConductoresFont = this.$$$getFont$$$("Arial", -1, 14, btnGestionConductores.getFont());
        if (btnGestionConductoresFont != null) btnGestionConductores.setFont(btnGestionConductoresFont);
        btnGestionConductores.setText("Gestión de Conductores");
        panel2.add(btnGestionConductores, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnValidarDocumentos = new JButton();
        Font btnValidarDocumentosFont = this.$$$getFont$$$("Arial", -1, 14, btnValidarDocumentos.getFont());
        if (btnValidarDocumentosFont != null) btnValidarDocumentos.setFont(btnValidarDocumentosFont);
        btnValidarDocumentos.setText("Validar Documentos");
        panel2.add(btnValidarDocumentos, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnPruebasPsicometricas = new JButton();
        Font btnPruebasPsicometricasFont = this.$$$getFont$$$("Arial", -1, 14, btnPruebasPsicometricas.getFont());
        if (btnPruebasPsicometricasFont != null) btnPruebasPsicometricas.setFont(btnPruebasPsicometricasFont);
        btnPruebasPsicometricas.setText("Pruebas Psicométricas");
        panel2.add(btnPruebasPsicometricas, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnEmitirLicencia = new JButton();
        Font btnEmitirLicenciaFont = this.$$$getFont$$$("Arial", -1, 14, btnEmitirLicencia.getFont());
        if (btnEmitirLicenciaFont != null) btnEmitirLicencia.setFont(btnEmitirLicenciaFont);
        btnEmitirLicencia.setText("Emitir Licencia");
        panel2.add(btnEmitirLicencia, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnConsultarLicencias = new JButton();
        Font btnConsultarLicenciasFont = this.$$$getFont$$$("Arial", -1, 14, btnConsultarLicencias.getFont());
        if (btnConsultarLicenciasFont != null) btnConsultarLicencias.setFont(btnConsultarLicenciasFont);
        btnConsultarLicencias.setText("Consultar Licencias");
        panel2.add(btnConsultarLicencias, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnGenerarDocumento = new JButton();
        Font btnGenerarDocumentoFont = this.$$$getFont$$$("Arial", -1, 14, btnGenerarDocumento.getFont());
        if (btnGenerarDocumentoFont != null) btnGenerarDocumento.setFont(btnGenerarDocumentoFont);
        btnGenerarDocumento.setText("Generar Documento PDF");
        panel2.add(btnGenerarDocumento, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 80), null, 0, false));
        btnSalir = new JButton();
        btnSalir.setBackground(new Color(-2104859));
        Font btnSalirFont = this.$$$getFont$$$("Arial", Font.BOLD, 14, btnSalir.getFont());
        if (btnSalirFont != null) btnSalir.setFont(btnSalirFont);
        btnSalir.setForeground(new Color(-4971218));
        btnSalir.setText("Salir");
        panel2.add(btnSalir, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 50), null, 0, false));
        btnAdmin = new JButton();
        btnAdmin.setBackground(new Color(-2104859));
        Font btnAdminFont = this.$$$getFont$$$("Arial", Font.BOLD, 14, btnAdmin.getFont());
        if (btnAdminFont != null) btnAdmin.setFont(btnAdminFont);
        btnAdmin.setForeground(new Color(-13284865));
        btnAdmin.setText("Funciones de Administrador");
        panel2.add(btnAdmin, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 0, 0), -1, -1));
        mainPanel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$("Arial", -1, 10, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setForeground(new Color(-6710887));
        label3.setText("Sistema de Licencias v1.0 - Desarrollado con Java y MySQL");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblVersion = new JLabel();
        Font lblVersionFont = this.$$$getFont$$$("Arial", -1, 14, lblVersion.getFont());
        if (lblVersionFont != null) lblVersion.setFont(lblVersionFont);
        lblVersion.setText("Label nombre");
        mainPanel.add(lblVersion, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}