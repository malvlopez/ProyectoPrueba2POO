package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PruebasPsicometricasView extends JFrame {
    private final LicenciaController controller;
    private Conductor conductorActual;
    private PruebaPsicometrica pruebaExistente;

    private JPanel panelPrincipal;
    private JPanel panelBusqueda;
    private JPanel panelNotas;
    private JPanel panelResultados;
    private JPanel panelBotones;
    private JScrollPane scrollInfo;
    private JTextField txtCedula;
    private JButton btnBuscar;
    private JTextArea txtInfoConductor;
    private JTextField txtNotaReaccion;
    private JTextField txtNotaAtencion;
    private JTextField txtNotaCoordinacion;
    private JTextField txtNotaPercepcion;
    private JTextField txtNotaPsicologica;
    private JButton btnCalcular;
    private JLabel lblPromedio;
    private JLabel lblResultado;
    private JTextArea txtObservaciones;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnCerrar;
    private JButton btnEliminarP;

    public PruebasPsicometricasView(LicenciaController controller) {
        this.controller = controller;
        $$$setupUI$$$();
        setTitle("Pruebas Psicométricas");
        setContentPane(panelPrincipal);
        setSize(700, 600);
        setLocationRelativeTo(null);
        configurarEventos();
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> buscarConductor());
        btnCalcular.addActionListener(e -> calcularPromedio());
        btnGuardar.addActionListener(e -> guardarPrueba());
        btnLimpiar.addActionListener(e -> limpiarFormularioCompleto());
        btnCerrar.addActionListener(e -> dispose());

        btnEliminarP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pruebaExistente == null || pruebaExistente.getId() == null) {
                    controller.mostrarError("No hay una prueba cargada para eliminar.");
                    return;
                }

                String nombreConductor = (conductorActual != null) ? conductorActual.getNombreCompleto() : "este conductor";
                String advertencia = "¿Está seguro de eliminar permanentemente los resultados de " + nombreConductor + "?\n" +
                        "Si tiene una licencia emitida, esta también se eliminará.";

                if (controller.confirmar(advertencia)) {
                    try {
                        controller.eliminarPruebaPsicometrica(pruebaExistente.getId());
                        controller.mostrarExito("La prueba ha sido eliminada.");
                        limpiarFormularioCompleto();
                    } catch (LicenciaException ex) {
                        controller.mostrarError("Error al eliminar: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void buscarConductor() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            controller.mostrarError("Ingrese una cédula");
            return;
        }

        try {
            limpiarFormularioCompleto();
            conductorActual = controller.buscarConductorPorCedula(cedula);

            if (conductorActual != null) {
                txtInfoConductor.setText(String.format("Nombre: %s\nCédula: %s\nEdad: %d años",
                        conductorActual.getNombreCompleto(), conductorActual.getCedula(), conductorActual.calcularEdad()));

                pruebaExistente = controller.buscarPruebaPorConductorId(conductorActual.getId());

                if (pruebaExistente != null) {
                    txtNotaReaccion.setText(String.valueOf(pruebaExistente.getNotaReaccion()));
                    txtNotaAtencion.setText(String.valueOf(pruebaExistente.getNotaAtencion()));
                    txtNotaCoordinacion.setText(String.valueOf(pruebaExistente.getNotaCoordinacion()));
                    txtNotaPercepcion.setText(String.valueOf(pruebaExistente.getNotaPercepcion()));
                    txtNotaPsicologica.setText(String.valueOf(pruebaExistente.getNotaPsicologica()));
                    txtObservaciones.setText(pruebaExistente.getObservaciones());
                    btnGuardar.setText("Guardar Cambios");
                    calcularPromedio();
                } else {
                    btnGuardar.setText("Guardar Resultados");
                }
            } else {
                controller.mostrarError("Conductor no encontrado");
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void calcularPromedio() {
        try {
            double r = Double.parseDouble(txtNotaReaccion.getText().trim());
            double a = Double.parseDouble(txtNotaAtencion.getText().trim());
            double c = Double.parseDouble(txtNotaCoordinacion.getText().trim());
            double p = Double.parseDouble(txtNotaPercepcion.getText().trim());
            double ps = Double.parseDouble(txtNotaPsicologica.getText().trim());

            double[] notas = {r, a, c, p, ps};
            for (double n : notas) {
                if (n < 0 || n > 100) {
                    controller.mostrarError("Notas deben estar entre 0 y 100");
                    return;
                }
            }

            double promedio = (r + a + c + p + ps) / 5.0;
            lblPromedio.setText(String.format("Promedio: %.2f", promedio));

            if (promedio >= 70.0) {
                lblResultado.setText("Estado: APROBADO");
                lblResultado.setForeground(new Color(0, 153, 0));
            } else {
                lblResultado.setText("Estado: REPROBADO");
                lblResultado.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            controller.mostrarError("Ingrese notas válidas");
        }
    }

    private void guardarPrueba() {
        if (conductorActual == null) {
            controller.mostrarError("Busque un conductor primero");
            return;
        }

        try {
            PruebaPsicometrica prueba = (pruebaExistente != null) ? pruebaExistente : new PruebaPsicometrica(conductorActual.getId());

            prueba.setNotaReaccion(Double.parseDouble(txtNotaReaccion.getText().trim()));
            prueba.setNotaAtencion(Double.parseDouble(txtNotaAtencion.getText().trim()));
            prueba.setNotaCoordinacion(Double.parseDouble(txtNotaCoordinacion.getText().trim()));
            prueba.setNotaPercepcion(Double.parseDouble(txtNotaPercepcion.getText().trim()));
            prueba.setNotaPsicologica(Double.parseDouble(txtNotaPsicologica.getText().trim()));
            prueba.setObservaciones(txtObservaciones.getText().trim());

            controller.registrarPruebaPsicometrica(prueba);
            controller.mostrarExito("Datos guardados correctamente");
            limpiarFormularioCompleto();
        } catch (Exception ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void limpiarFormularioCompleto() {
        txtNotaReaccion.setText("");
        txtNotaAtencion.setText("");
        txtNotaCoordinacion.setText("");
        txtNotaPercepcion.setText("");
        txtNotaPsicologica.setText("");
        txtObservaciones.setText("");
        txtInfoConductor.setText("");
        lblPromedio.setText("Promedio: --");
        lblResultado.setText("Estado: --");
        lblResultado.setForeground(Color.BLACK);
        pruebaExistente = null;
        btnGuardar.setText("Guardar Resultados");
    }

    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayoutManager(5, 1, new Insets(15, 15, 15, 15), -1, -1));

        panelBusqueda = new JPanel(new FlowLayout());
        txtCedula = new JTextField(15);
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(new JLabel("Cédula:"));
        panelBusqueda.add(txtCedula);
        panelBusqueda.add(btnBuscar);

        scrollInfo = new JScrollPane();
        txtInfoConductor = new JTextArea(5, 40);
        txtInfoConductor.setEditable(false);
        scrollInfo.setViewportView(txtInfoConductor);

        panelNotas = new JPanel(new GridLayout(3, 4, 10, 10));
        txtNotaReaccion = new JTextField();
        txtNotaAtencion = new JTextField();
        txtNotaCoordinacion = new JTextField();
        txtNotaPercepcion = new JTextField();
        txtNotaPsicologica = new JTextField();
        btnCalcular = new JButton("Calcular");

        panelNotas.add(new JLabel("Reacción:")); panelNotas.add(txtNotaReaccion);
        panelNotas.add(new JLabel("Atención:")); panelNotas.add(txtNotaAtencion);
        panelNotas.add(new JLabel("Coordinación:")); panelNotas.add(txtNotaCoordinacion);
        panelNotas.add(new JLabel("Percepción:")); panelNotas.add(txtNotaPercepcion);
        panelNotas.add(new JLabel("Psicológica:")); panelNotas.add(txtNotaPsicologica);
        panelNotas.add(btnCalcular);

        panelResultados = new JPanel(new BorderLayout());
        lblPromedio = new JLabel("Promedio: --");
        lblResultado = new JLabel("Estado: --");
        txtObservaciones = new JTextArea(3, 20);
        panelResultados.add(lblPromedio, BorderLayout.NORTH);
        panelResultados.add(lblResultado, BorderLayout.CENTER);
        panelResultados.add(new JScrollPane(txtObservaciones), BorderLayout.SOUTH);

        panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar Resultados");
        btnLimpiar = new JButton("Limpiar");
        btnEliminarP = new JButton("Eliminar Prueba");
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEliminarP);
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelBusqueda, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 0, null, null, null, 0));
        panelPrincipal.add(scrollInfo, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0));
        panelPrincipal.add(panelNotas, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0));
        panelPrincipal.add(panelResultados, new GridConstraints(3, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0));
        panelPrincipal.add(panelBotones, new GridConstraints(4, 0, 1, 1, 0, 3, 3, 0, null, null, null, 0));
    }

    public JComponent $$$getRootComponent$$$() { return panelPrincipal; }
}