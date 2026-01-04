package ec.edu.sistemalicencias.model;

/**
 * Clase con constantes para tipos de licencias de conducir en Ecuador.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */

public class TipoLicenciaConstantes {

    public static final String TIPO_A = "A";
    public static final String TIPO_B = "B";
    public static final String TIPO_C = "C";
    public static final String TIPO_D = "D";
    public static final String TIPO_E = "E";
    public static final String TIPO_F = "F";

    public static final String NOMBRE_TIPO_A = "Tipo A - Motocicletas y ciclomotores";
    public static final String NOMBRE_TIPO_B = "Tipo B - Vehículos livianos hasta 3500 kg";
    public static final String NOMBRE_TIPO_C = "Tipo C - Vehículos pesados de carga";
    public static final String NOMBRE_TIPO_D = "Tipo D - Transporte público de pasajeros";
    public static final String NOMBRE_TIPO_E = "Tipo E - Vehículos especiales y maquinaria";
    public static final String NOMBRE_TIPO_F = "Tipo F - Transporte comercial profesional";

    public static final String[] TIPOS_LICENCIA = {
            TIPO_A, TIPO_B, TIPO_C, TIPO_D, TIPO_E, TIPO_F
    };

    public static final String[] NOMBRES_TIPOS_LICENCIA = {
            NOMBRE_TIPO_A, NOMBRE_TIPO_B, NOMBRE_TIPO_C,
            NOMBRE_TIPO_D, NOMBRE_TIPO_E, NOMBRE_TIPO_F
    };

    private TipoLicenciaConstantes() {
        throw new AssertionError("No se debe instanciar esta clase");
    }

    public static String obtenerNombre(String tipo) {
        if (tipo == null) return "Desconocido";
        switch (tipo) {
            case TIPO_A: return NOMBRE_TIPO_A;
            case TIPO_B: return NOMBRE_TIPO_B;
            case TIPO_C: return NOMBRE_TIPO_C;
            case TIPO_D: return NOMBRE_TIPO_D;
            case TIPO_E: return NOMBRE_TIPO_E;
            case TIPO_F: return NOMBRE_TIPO_F;
            default: return "Tipo " + tipo;
        }
    }

    public static String obtenerCodigoDesdeNombre(String nombreDescriptivo) {
        if (nombreDescriptivo == null) return TIPO_B;
        if (nombreDescriptivo.contains("Tipo A")) return TIPO_A;
        if (nombreDescriptivo.contains("Tipo B")) return TIPO_B;
        if (nombreDescriptivo.contains("Tipo C")) return TIPO_C;
        if (nombreDescriptivo.contains("Tipo D")) return TIPO_D;
        if (nombreDescriptivo.contains("Tipo E")) return TIPO_E;
        if (nombreDescriptivo.contains("Tipo F")) return TIPO_F;
        return TIPO_B;
    }

    public static boolean esValido(String tipo) {
        if (tipo == null) return false;
        for (String t : TIPOS_LICENCIA) {
            if (t.equals(tipo)) return true;
        }
        return false;
    }
    public static String obtenerNombreCorto(String tipo) {
        if (tipo == null) return "X";
        return tipo.replace("TIPO_", "");
    }
}