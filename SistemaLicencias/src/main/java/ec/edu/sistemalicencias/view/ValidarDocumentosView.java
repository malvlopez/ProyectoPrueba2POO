package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ValidarDocumentosView extends JFrame {
    private final LicenciaController controller;
    private Conductor conductorActual;

    private JPanel panelPrincipal;
    private JPanel panelBusqueda;
    private JPanel panelDocumentos;
    private JPanel panelBotones;
    private JTextField txtCedula;
    private JButton btnBuscar;
    private JTextArea txtInfo;
    private JScrollPane scrollInfo;
    private JCheckBox chkTituloConductor;
    private JCheckBox chkPermisoAprendizaje;
    private JCheckBox chkExamenPsicosensometrico;
    private JCheckBox chkCedulaPapeleta;
    private JCheckBox chkCertificadoSangre;
    private JCheckBox chkComprobantePago;
    private JCheckBox chkTurnoImpreso;
    private JCheckBox chkCertificadoEducacion;
    private JButton btnMarcarTodos;
    private JButton btnDesmarcarTodos;
    private JButton btnGuardar;
    private JButton btnCerrar;
    private JButton btnEliminarValidacion;

    public ValidarDocumentosView(LicenciaController controller) {
        this.controller = controller;
        $$$setupUI$$$();
        setTitle("Validar Documentos - Licencia No Profesional");
        setContentPane(panelPrincipal);
        setSize(750, 700);
        setLocationRelativeTo(null);
        configurarEventos();
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> buscarConductor());
        btnMarcarTodos.addActionListener(e -> marcarTodosDocumentos(true));
        btnDesmarcarTodos.addActionListener(e -> marcarTodosDocumentos(false));
        btnGuardar.addActionListener(e -> guardarValidacion());
        btnCerrar.addActionListener(e -> dispose());

        btnEliminarValidacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (conductorActual == null) {
                    controller.mostrarError("Busque un conductor primero.");
                    return;
                }
                if (controller.confirmar("¿Desea anular la validación de documentos de " + conductorActual.getNombreCompleto() + "?")) {
                    try {
                        conductorActual.setDocumentosValidados(false);
                        conductorActual.setObservaciones("Validación anulada manualmente.");
                        controller.actualizarConductor(conductorActual);
                        controller.mostrarExito("Validación anulada correctamente.");
                        mostrarInfoConductor();
                        limpiarCheckboxes();
                    } catch (LicenciaException ex) {
                        controller.mostrarError("Error: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void marcarTodosDocumentos(boolean seleccionar) {
        chkTituloConductor.setSelected(seleccionar);
        chkPermisoAprendizaje.setSelected(seleccionar);
        chkExamenPsicosensometrico.setSelected(seleccionar);
        chkCedulaPapeleta.setSelected(seleccionar);
        chkCertificadoSangre.setSelected(seleccionar);
        chkComprobantePago.setSelected(seleccionar);
        chkTurnoImpreso.setSelected(seleccionar);
        chkCertificadoEducacion.setSelected(seleccionar);
    }

    private void buscarConductor() {
        try {
            conductorActual = controller.buscarConductorPorCedula(txtCedula.getText().trim());
            if (conductorActual != null) {
                mostrarInfoConductor();
            } else {
                controller.mostrarError("No se encontró el conductor");
                txtInfo.setText("");
                limpiarCheckboxes();
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void mostrarInfoConductor() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cédula: ").append(conductorActual.getCedula()).append("\n");
        sb.append("Nombre: ").append(conductorActual.getNombreCompleto()).append("\n");
        sb.append("Estado actual: ").append(conductorActual.isDocumentosValidados() ? "VALIDADO" : "PENDIENTE");
        txtInfo.setText(sb.toString());

        if (conductorActual.isDocumentosValidados()) {
            marcarTodosDocumentos(true);
        } else {
            limpiarCheckboxes();
        }
    }

    private void limpiarCheckboxes() { marcarTodosDocumentos(false); }

    private void guardarValidacion() {
        if (conductorActual == null) {
            controller.mostrarError("Busque un conductor primero");
            return;
        }

        boolean completa = chkTituloConductor.isSelected() && chkPermisoAprendizaje.isSelected() &&
                chkExamenPsicosensometrico.isSelected() && chkCedulaPapeleta.isSelected() &&
                chkCertificadoSangre.isSelected() && chkComprobantePago.isSelected() &&
                chkTurnoImpreso.isSelected() && chkCertificadoEducacion.isSelected();

        try {
            conductorActual.setDocumentosValidados(completa);
            conductorActual.setObservaciones(completa ? "Todo en regla" : "Documentación incompleta");
            controller.actualizarConductor(conductorActual);

            if (completa) controller.mostrarExito("Validación exitosa");
            else controller.mostrarError("Validación incompleta guardada");

            mostrarInfoConductor();
        } catch (LicenciaException ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayoutManager(4, 1, new Insets(15, 15, 15, 15), -1, -1));

        panelBusqueda = new JPanel(new FlowLayout());
        txtCedula = new JTextField(15);
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(new JLabel("Cédula:"));
        panelBusqueda.add(txtCedula);
        panelBusqueda.add(btnBuscar);

        scrollInfo = new JScrollPane();
        txtInfo = new JTextArea(6, 40);
        txtInfo.setEditable(false);
        scrollInfo.setViewportView(txtInfo);

        panelDocumentos = new JPanel(new GridLayout(8, 1));
        chkTituloConductor = new JCheckBox("1. Título de conductor");
        chkPermisoAprendizaje = new JCheckBox("2. Permiso de aprendizaje");
        chkExamenPsicosensometrico = new JCheckBox("3. Examen psicosensométrico");
        chkCedulaPapeleta = new JCheckBox("4. Cédula y Papeleta");
        chkCertificadoSangre = new JCheckBox("5. Tipo sanguíneo");
        chkComprobantePago = new JCheckBox("6. Comprobante de pago");
        chkTurnoImpreso = new JCheckBox("7. Turno impreso");
        chkCertificadoEducacion = new JCheckBox("8. Certificado Educación");

        panelDocumentos.add(chkTituloConductor); panelDocumentos.add(chkPermisoAprendizaje);
        panelDocumentos.add(chkExamenPsicosensometrico); panelDocumentos.add(chkCedulaPapeleta);
        panelDocumentos.add(chkCertificadoSangre); panelDocumentos.add(chkComprobantePago);
        panelDocumentos.add(chkTurnoImpreso); panelDocumentos.add(chkCertificadoEducacion);

        panelBotones = new JPanel(new FlowLayout());
        btnMarcarTodos = new JButton("Marcar Todos");
        btnDesmarcarTodos = new JButton("Desmarcar");
        btnGuardar = new JButton("Guardar");
        btnEliminarValidacion = new JButton("Anular");
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnMarcarTodos); panelBotones.add(btnDesmarcarTodos);
        panelBotones.add(btnGuardar); panelBotones.add(btnEliminarValidacion);
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelBusqueda, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 0, null, null, null, 0));
        panelPrincipal.add(scrollInfo, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0));
        panelPrincipal.add(panelDocumentos, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0));
        panelPrincipal.add(panelBotones, new GridConstraints(3, 0, 1, 1, 0, 3, 3, 0, null, null, null, 0));
    }

    public JComponent $$$getRootComponent$$$() { return panelPrincipal; }
}