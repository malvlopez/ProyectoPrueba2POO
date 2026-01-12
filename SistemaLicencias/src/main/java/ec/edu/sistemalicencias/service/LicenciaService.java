package ec.edu.sistemalicencias.service;

import ec.edu.sistemalicencias.dao.ConductorDAO;
import ec.edu.sistemalicencias.dao.LicenciaDAO;
import ec.edu.sistemalicencias.dao.PruebaPsicometricaDAO;
import ec.edu.sistemalicencias.model.TipoLicenciaConstantes;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import ec.edu.sistemalicencias.model.exceptions.DocumentoInvalidoException;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import java.util.List;

public class LicenciaService {

    private final ConductorDAO conductorDAO;
    private final PruebaPsicometricaDAO pruebaPsicometricaDAO;
    private final LicenciaDAO licenciaDAO;

    public LicenciaService() {
        this.conductorDAO = new ConductorDAO();
        this.pruebaPsicometricaDAO = new PruebaPsicometricaDAO();
        this.licenciaDAO = new LicenciaDAO();
    }

    public Long registrarConductor(Conductor conductor) throws LicenciaException {
        try {
            conductor.validar();

            Conductor conductorExistente = conductorDAO.buscarPorCedula(conductor.getCedula());
            if (conductorExistente != null && !conductorExistente.getId().equals(conductor.getId())) {
                throw new DocumentoInvalidoException(
                        "Ya existe un conductor registrado con la cédula: " + conductor.getCedula()
                );
            }

            Long id = conductorDAO.guardar(conductor);
            conductor.setId(id);
            return id;

        } catch (DocumentoInvalidoException e) {
            throw e;
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al registrar conductor", e);
        }
    }

    public void actualizarConductor(Conductor conductor) throws LicenciaException {
        try {
            conductor.validar();

            if (conductor.getId() == null || conductorDAO.buscarPorId(conductor.getId()) == null) {
                throw new DocumentoInvalidoException("El conductor no existe en el sistema");
            }

            conductorDAO.guardar(conductor);

        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al actualizar conductor", e);
        }
    }

    public boolean eliminarConductor(Long id) throws LicenciaException {
        try {
            return conductorDAO.eliminar(id);
        } catch (BaseDatosException e) {
            throw new LicenciaException("No se puede eliminar el conductor porque tiene licencias o registros asociados.");
        }
    }

    public void validarDocumentos(Long conductorId, boolean documentosValidos, String observaciones)
            throws LicenciaException {
        try {
            Conductor conductor = conductorDAO.buscarPorId(conductorId);
            if (conductor == null) {
                throw new DocumentoInvalidoException("Conductor no encontrado");
            }

            conductor.setDocumentosValidados(documentosValidos);
            conductor.setObservaciones(observaciones);
            conductorDAO.guardar(conductor);

        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al validar documentos", e);
        }
    }

    public Long registrarPruebaPsicometrica(PruebaPsicometrica prueba) throws LicenciaException {
        try {
            Conductor conductor = conductorDAO.buscarPorId(prueba.getConductorId());
            if (conductor == null) {
                throw new DocumentoInvalidoException("Conductor no encontrado");
            }
            return pruebaPsicometricaDAO.guardar(prueba);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al registrar prueba psicométrica", e);
        }
    }

    public PruebaPsicometrica obtenerPruebaPorConductorId(Long conductorId) throws LicenciaException {
        try {
            List<PruebaPsicometrica> pruebas = pruebaPsicometricaDAO.buscarPorConductor(conductorId);
            if (pruebas != null && !pruebas.isEmpty()) {
                return pruebas.get(0);
            }
            return null;
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener la prueba: " + e.getMessage());
        }
    }

    public void eliminarPruebaPsicometrica(Long pruebaId) throws LicenciaException {
        try {
            List<Licencia> licencias = licenciaDAO.obtenerTodas();
            for (Licencia l : licencias) {
                if (pruebaId.equals(l.getPruebaPsicometricaId())) {
                    licenciaDAO.eliminar(l.getId());
                }
            }
            pruebaPsicometricaDAO.eliminar(pruebaId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al eliminar en cascada: " + e.getMessage());
        }
    }

    public void eliminarLicencia(Long licenciaId) throws LicenciaException {
        try {
            Licencia lic = licenciaDAO.buscarPorId(licenciaId);
            if (lic == null) {
                throw new LicenciaException("La licencia no existe.");
            }
            licenciaDAO.eliminar(licenciaId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al eliminar la licencia: " + e.getMessage());
        }
    }

    public Licencia emitirLicencia(Long conductorId, String tipoLicencia, Long pruebaPsicometricaId)
            throws LicenciaException {
        try {
            Conductor conductor = conductorDAO.buscarPorId(conductorId);
            if (conductor == null) {
                throw new DocumentoInvalidoException("Conductor no encontrado");
            }

            if (!conductor.isDocumentosValidados()) {
                throw new DocumentoInvalidoException("Documentos del conductor no validados");
            }

            if (conductor.calcularEdad() < 18) {
                throw new DocumentoInvalidoException("El conductor debe ser mayor de 18 años");
            }

            if (pruebaPsicometricaId != null) {
                PruebaPsicometrica prueba = pruebaPsicometricaDAO.buscarPorId(pruebaPsicometricaId);
                if (prueba == null || !prueba.estaAprobado()) {
                    throw new DocumentoInvalidoException("Prueba psicométrica no válida o no aprobada");
                }
            }

            List<Licencia> licenciasExistentes = licenciaDAO.buscarPorConductor(conductorId);
            for (Licencia lic : licenciasExistentes) {
                if (tipoLicencia.equals(lic.getTipoLicencia()) && lic.estaVigente()) {
                    throw new DocumentoInvalidoException(
                            "Ya existe una licencia vigente de tipo " + TipoLicenciaConstantes.obtenerNombre(tipoLicencia)
                    );
                }
            }

            Licencia nuevaLicencia = new Licencia(conductorId, tipoLicencia);
            nuevaLicencia.setPruebaPsicometricaId(pruebaPsicometricaId);
            nuevaLicencia.generarNumeroLicencia(conductor.getCedula());
            nuevaLicencia.validar();

            Long licenciaId = licenciaDAO.guardar(nuevaLicencia);
            nuevaLicencia.setId(licenciaId);

            return nuevaLicencia;

        } catch (DocumentoInvalidoException e) {
            throw e;
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al emitir licencia", e);
        }
    }

    public Conductor buscarConductorPorCedula(String cedula) throws LicenciaException {
        try {
            return conductorDAO.buscarPorCedula(cedula);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar conductor", e);
        }
    }

    public Conductor buscarConductorPorId(Long id) throws LicenciaException {
        try {
            return conductorDAO.buscarPorId(id);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar conductor", e);
        }
    }

    public List<Conductor> obtenerTodosConductores() throws LicenciaException {
        try {
            return conductorDAO.obtenerTodos();
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener conductores", e);
        }
    }

    public List<Conductor> buscarConductoresPorNombre(String nombre) throws LicenciaException {
        try {
            return conductorDAO.buscarPorNombre(nombre);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar conductores", e);
        }
    }

    public List<PruebaPsicometrica> obtenerPruebasConductor(Long conductorId) throws LicenciaException {
        try {
            return pruebaPsicometricaDAO.buscarPorConductor(conductorId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener pruebas", e);
        }
    }

    public PruebaPsicometrica obtenerUltimaPruebaAprobada(Long conductorId) throws LicenciaException {
        try {
            return pruebaPsicometricaDAO.obtenerUltimaPruebaAprobada(conductorId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener prueba", e);
        }
    }

    public Licencia buscarLicenciaPorNumero(String numeroLicencia) throws LicenciaException {
        try {
            return licenciaDAO.buscarPorNumero(numeroLicencia);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar licencia", e);
        }
    }

    public List<Licencia> obtenerLicenciasConductor(Long conductorId) throws LicenciaException {
        try {
            return licenciaDAO.buscarPorConductor(conductorId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener licencias", e);
        }
    }

    public List<Licencia> obtenerTodasLicencias() throws LicenciaException {
        try {
            return licenciaDAO.obtenerTodas();
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener licencias", e);
        }
    }

    public List<Licencia> obtenerLicenciasVigentes() throws LicenciaException {
        try {
            return licenciaDAO.obtenerLicenciasVigentes();
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener licencias vigentes", e);
        }
    }

    public void desactivarLicencia(Long licenciaId, String motivo) throws LicenciaException {
        try {
            Licencia licencia = licenciaDAO.buscarPorId(licenciaId);
            if (licencia == null) {
                throw new DocumentoInvalidoException("Licencia no encontrada");
            }
            licencia.setActiva(false);
            licencia.setObservaciones(motivo);
            licenciaDAO.guardar(licencia);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al desactivar licencia", e);
        }
    }
}