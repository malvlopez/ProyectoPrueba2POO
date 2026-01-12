package ec.edu.sistemalicencias.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import ec.edu.sistemalicencias.controller.LicenciaController;
import ec.edu.sistemalicencias.model.TipoSangreConstantes;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;
import ec.edu.sistemalicencias.controller.UsuarioSesion;
import javax.swing.table.TableRowSorter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@SuppressWarnings("unused")
public class GestionConductoresView extends JFrame {
    private final String TEXT_BUSCAR = "Buscar...";
    private final LicenciaController controller;
    private DefaultTableModel modeloTabla;
    private Conductor conductorSeleccionado;

    private JPanel panelPrincipal;
    private JPanel panelFormulario;
    private JPanel panelBotones;
    private JScrollPane scrollTabla;
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtFechaNacimiento;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JComboBox<String> cmbTipoSangre;
    private JTable tablaConductores;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnActualizar;
    private JButton btnCerrar;
    private JButton btnEliminar;
    private JButton btnActualizarR;
    private JTextField txtBuscar;
    private TableRowSorter<DefaultTableModel> sorter;

    public GestionConductoresView(LicenciaController controller) {
        this.controller = controller;
        $$$setupUI$$$();
        setTitle("Gestión de Conductores");
        setContentPane(panelPrincipal);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarDatos();
        inicializarTabla();
        configurarEventos();
        cargarConductores();

        txtBuscar.setText(TEXT_BUSCAR);
        txtBuscar.setForeground(Color.GRAY);
    }

    private void inicializarDatos() {
        cmbTipoSangre.setModel(new DefaultComboBoxModel<>(TipoSangreConstantes.TIPOS_SANGRE));
    }

    private void inicializarTabla() {
        String[] columnas = {"#", "ID", "Cédula", "Nombres", "Apellidos", "Fecha Nac.", "Teléfono", "Docs. Validados"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sorter = new TableRowSorter<>(modeloTabla);
        tablaConductores.setRowSorter(sorter);
        tablaConductores.setModel(modeloTabla);
        tablaConductores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(e -> guardarConductor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnActualizar.addActionListener(e -> cargarConductores());
        btnCerrar.addActionListener(e -> dispose());

        btnActualizarR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!"ADMINISTRADOR".equalsIgnoreCase(UsuarioSesion.getUsuario().getRol())) {
                    controller.mostrarError("Acceso denegado: Solo administradores pueden actualizar datos.");
                    return;
                }
                if (conductorSeleccionado == null) {
                    controller.mostrarError("Por favor, seleccione un conductor de la tabla para actualizar.");
                    return;
                }
                actualizarRegistroConductor();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!"ADMINISTRADOR".equalsIgnoreCase(UsuarioSesion.getUsuario().getRol())) {
                    controller.mostrarError("Acceso denegado: Solo administradores pueden eliminar datos.");
                    return;
                }
                if (conductorSeleccionado == null) {
                    controller.mostrarError("Debe seleccionar un conductor de la tabla primero.");
                    return;
                }

                if (controller.confirmar("¿Está seguro de eliminar permanentemente a " + conductorSeleccionado.getNombres() + "?")) {
                    try {
                        controller.eliminarConductor(conductorSeleccionado.getId());
                        controller.mostrarExito("Conductor eliminado correctamente.");
                        limpiarFormulario();
                        cargarConductores();
                    } catch (LicenciaException ex) {
                        controller.mostrarError(ex.getMessage());
                    }
                }
            }
        });

        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().equals(TEXT_BUSCAR)) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtBuscar.getText().isEmpty()) {
                    txtBuscar.setText(TEXT_BUSCAR);
                    txtBuscar.setForeground(Color.GRAY);
                }
            }
        });

        txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String texto = txtBuscar.getText().trim();
                if (texto.equals(TEXT_BUSCAR) || texto.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        tablaConductores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaConductores.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    cargarConductorEnFormulario(filaSeleccionada);
                }
            }
        });
    }

    private void actualizarRegistroConductor() {
        try {
            if (txtCedula.getText().trim().isEmpty() || txtNombres.getText().trim().isEmpty()) {
                controller.mostrarError("La cédula y los nombres no pueden estar vacíos.");
                return;
            }

            conductorSeleccionado.setCedula(txtCedula.getText().trim());
            conductorSeleccionado.setNombres(txtNombres.getText().trim().toUpperCase());
            conductorSeleccionado.setApellidos(txtApellidos.getText().trim().toUpperCase());
            conductorSeleccionado.setDireccion(txtDireccion.getText().trim());
            conductorSeleccionado.setTelefono(txtTelefono.getText().trim());
            conductorSeleccionado.setEmail(txtEmail.getText().trim());
            conductorSeleccionado.setTipoSangre((String) cmbTipoSangre.getSelectedItem());

            try {
                conductorSeleccionado.setFechaNacimiento(LocalDate.parse(txtFechaNacimiento.getText().trim()));
            } catch (DateTimeParseException ex) {
                controller.mostrarError("Fecha inválida. Use AAAA-MM-DD");
                return;
            }

            controller.registrarConductor(conductorSeleccionado);
            controller.mostrarExito("¡Registro actualizado correctamente!");
            limpiarFormulario();
            cargarConductores();

        } catch (Exception ex) {
            controller.mostrarError("Error al actualizar: " + ex.getMessage());
        }
    }

    private void guardarConductor() {
        String rol = UsuarioSesion.getUsuario().getRol();
        if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {
            controller.mostrarError("No tiene permisos para realizar esta acción");
            return;
        }
        try {
            if (txtCedula.getText().trim().isEmpty() || txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty()) {
                controller.mostrarError("Los campos Cédula, Nombres y Apellidos son obligatorios");
                return;
            }

            Conductor conductor = (conductorSeleccionado != null) ? conductorSeleccionado : new Conductor();
            conductor.setCedula(txtCedula.getText().trim());
            conductor.setNombres(txtNombres.getText().trim());
            conductor.setApellidos(txtApellidos.getText().trim());

            try {
                conductor.setFechaNacimiento(LocalDate.parse(txtFechaNacimiento.getText().trim()));
            } catch (DateTimeParseException ex) {
                controller.mostrarError("Formato de fecha inválido. Use AAAA-MM-DD");
                return;
            }

            conductor.setDireccion(txtDireccion.getText().trim());
            conductor.setTelefono(txtTelefono.getText().trim());
            conductor.setEmail(txtEmail.getText().trim());
            conductor.setTipoSangre((String) cmbTipoSangre.getSelectedItem());

            Long id = controller.registrarConductor(conductor);
            controller.mostrarExito(conductorSeleccionado != null ? "Conductor actualizado" : "Registrado con ID: " + id);

            limpiarFormulario();
            cargarConductores();
        } catch (Exception ex) {
            controller.mostrarError("Error: " + ex.getMessage());
        }
    }

    private void cargarConductores() {
        try {
            List<Conductor> conductores = controller.obtenerTodosConductores();
            modeloTabla.setRowCount(0);
            for (int i = 0; i < conductores.size(); i++) {
                Conductor c = conductores.get(i);
                Object[] fila = {
                        (i + 1),
                        c.getId(),
                        c.getCedula(),
                        c.getNombres(),
                        c.getApellidos(),
                        c.getFechaNacimiento(),
                        c.getTelefono(),
                        c.isDocumentosValidados() ? "SÍ" : "NO"
                };
                modeloTabla.addRow(fila);
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error al cargar conductores: " + ex.getMessage());
        }
    }

    private void cargarConductorEnFormulario(int fila) {
        try {
            int filaModelo = tablaConductores.convertRowIndexToModel(fila);
            Long id = (Long) modeloTabla.getValueAt(filaModelo, 1);
            conductorSeleccionado = controller.buscarConductorPorId(id);

            if (conductorSeleccionado != null) {
                txtCedula.setText(conductorSeleccionado.getCedula());
                txtNombres.setText(conductorSeleccionado.getNombres());
                txtApellidos.setText(conductorSeleccionado.getApellidos());
                txtFechaNacimiento.setText(conductorSeleccionado.getFechaNacimiento().toString());
                txtDireccion.setText(conductorSeleccionado.getDireccion());
                txtTelefono.setText(conductorSeleccionado.getTelefono());
                txtEmail.setText(conductorSeleccionado.getEmail());

                String tipoSangre = conductorSeleccionado.getTipoSangre();
                for (int i = 0; i < cmbTipoSangre.getItemCount(); i++) {
                    if (cmbTipoSangre.getItemAt(i).equals(tipoSangre)) {
                        cmbTipoSangre.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (LicenciaException ex) {
            controller.mostrarError("Error al cargar conductor: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        conductorSeleccionado = null;
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtFechaNacimiento.setText("1990-01-01");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        cmbTipoSangre.setSelectedIndex(0);
        tablaConductores.clearSelection();
    }

    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayoutManager(4, 1, new Insets(15, 15, 15, 15), -1, -1));
        panelPrincipal.setMinimumSize(new Dimension(900, 600));
        panelPrincipal.setPreferredSize(new Dimension(900, 600));

        panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayoutManager(4, 4, new Insets(10, 10, 10, 10), -1, -1));
        panelPrincipal.add(panelFormulario, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        panelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Datos del Conductor", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        // Labels y Fields... (Se mantienen igual)
        // ...

        scrollTabla = new JScrollPane();
        panelPrincipal.add(scrollTabla, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Conductores Registrados", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tablaConductores = new JTable();
        scrollTabla.setViewportView(tablaConductores);

        panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelPrincipal.add(panelBotones, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        btnGuardar = new JButton("Guardar Conductor");
        panelBotones.add(btnGuardar);

        btnActualizarR = new JButton("Actualizar Registro");
        panelBotones.add(btnActualizarR);

        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnLimpiar);

        btnActualizar = new JButton("Actualizar Lista");
        panelBotones.add(btnActualizar);

        btnEliminar = new JButton("Eliminar Registro");
        panelBotones.add(btnEliminar);

        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnCerrar);

        txtBuscar = new JTextField();
        panelPrincipal.add(txtBuscar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}