package ec.edu.sistemalicencias.controller;

import com.itextpdf.text.DocumentException;
import ec.edu.sistemalicencias.model.TipoLicenciaConstantes;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;
import ec.edu.sistemalicencias.service.LicenciaService;
import ec.edu.sistemalicencias.util.PDFGenerator;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LicenciaController {

    private final LicenciaService licenciaService;

    public LicenciaController() {
        this.licenciaService = new LicenciaService();
    }

    public Long registrarConductor(Conductor conductor) throws LicenciaException {
        return licenciaService.registrarConductor(conductor);
    }

    public boolean eliminarConductor(Long id) throws LicenciaException {
        return licenciaService.eliminarConductor(id);
    }

    public void actualizarConductor(Conductor conductor) throws LicenciaException {
        licenciaService.actualizarConductor(conductor);
    }

    public PruebaPsicometrica buscarPruebaPorConductorId(Long conductorId) throws LicenciaException {
        return licenciaService.obtenerPruebaPorConductorId(conductorId);
    }

    public void validarDocumentos(Long conductorId, boolean documentosValidos, String observaciones)
            throws LicenciaException {
        licenciaService.validarDocumentos(conductorId, documentosValidos, observaciones);
    }

    public Long registrarPruebaPsicometrica(PruebaPsicometrica prueba) throws LicenciaException {
        return licenciaService.registrarPruebaPsicometrica(prueba);
    }

    public Licencia emitirLicencia(Long conductorId, String tipoLicencia, Long pruebaPsicometricaId)
            throws LicenciaException {

        String codigoLimpio = tipoLicencia;

        if (tipoLicencia != null && tipoLicencia.length() > 5) {
            codigoLimpio = TipoLicenciaConstantes.obtenerCodigoDesdeNombre(tipoLicencia);
        }

        return licenciaService.emitirLicencia(conductorId, codigoLimpio, pruebaPsicometricaId);
    }

    public Conductor buscarConductorPorCedula(String cedula) throws LicenciaException {
        return licenciaService.buscarConductorPorCedula(cedula);
    }

    public Conductor buscarConductorPorId(Long id) throws LicenciaException {
        return licenciaService.buscarConductorPorId(id);
    }

    public List<Conductor> obtenerTodosConductores() throws LicenciaException {
        return licenciaService.obtenerTodosConductores();
    }

    public List<Conductor> buscarConductoresPorNombre(String nombre) throws LicenciaException {
        return licenciaService.buscarConductoresPorNombre(nombre);
    }

    public List<PruebaPsicometrica> obtenerPruebasConductor(Long conductorId) throws LicenciaException {
        return licenciaService.obtenerPruebasConductor(conductorId);
    }

    public PruebaPsicometrica obtenerUltimaPruebaAprobada(Long conductorId) throws LicenciaException {
        return licenciaService.obtenerUltimaPruebaAprobada(conductorId);
    }

    public Licencia buscarLicenciaPorNumero(String numeroLicencia) throws LicenciaException {
        return licenciaService.buscarLicenciaPorNumero(numeroLicencia);
    }

    public List<Licencia> obtenerLicenciasConductor(Long conductorId) throws LicenciaException {
        return licenciaService.obtenerLicenciasConductor(conductorId);
    }

    public List<Licencia> obtenerTodasLicencias() throws LicenciaException {
        return licenciaService.obtenerTodasLicencias();
    }

    public List<Licencia> obtenerLicenciasVigentes() throws LicenciaException {
        return licenciaService.obtenerLicenciasVigentes();
    }

    public void desactivarLicencia(Long licenciaId, String motivo) throws LicenciaException {
        licenciaService.desactivarLicencia(licenciaId, motivo);
    }

    public boolean generarDocumentoLicencia(Long licenciaId, String rutaArchivo) {
        try {
            List<Licencia> todasLicencias = licenciaService.obtenerTodasLicencias();
            Licencia licenciaEncontrada = todasLicencias.stream()
                    .filter(l -> l.getId().equals(licenciaId))
                    .findFirst()
                    .orElse(null);

            if (licenciaEncontrada == null) {
                JOptionPane.showMessageDialog(null, "No se encontró la licencia", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            final Licencia licencia = licenciaEncontrada;
            Conductor conductor = licenciaService.buscarConductorPorId(licencia.getConductorId());

            PruebaPsicometrica prueba = null;
            if (licencia.getPruebaPsicometricaId() != null) {
                prueba = licenciaService.obtenerPruebasConductor(conductor.getId())
                        .stream()
                        .filter(p -> p.getId().equals(licencia.getPruebaPsicometricaId()))
                        .findFirst()
                        .orElse(null);
            }

            PDFGenerator.generarLicenciaPDF(licencia, conductor, prueba, rutaArchivo);

            JOptionPane.showMessageDialog(null, "Documento generado exitosamente en:\n" + rutaArchivo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (LicenciaException | DocumentException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error al generar el documento:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void eliminarPruebaPsicometrica(Long pruebaId) throws LicenciaException {
        licenciaService.eliminarPruebaPsicometrica(pruebaId);
    }

    public void eliminarLicencia(Long licenciaId) throws LicenciaException {
        licenciaService.eliminarLicencia(licenciaId);
    }

    public boolean generarDocumentoLicenciaConDialogo(Long licenciaId) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Licencia PDF");
        fileChooser.setSelectedFile(new File("Licencia_" + licenciaId + ".pdf"));

        int resultado = fileChooser.showSaveDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }
            return generarDocumentoLicencia(licenciaId, rutaArchivo);
        }
        return false;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean confirmar(String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(null, mensaje, "Confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return respuesta == JOptionPane.YES_OPTION;
    }
}