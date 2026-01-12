package ec.edu.sistemalicencias.service;

import ec.edu.sistemalicencias.dao.LoginDAO;
import ec.edu.sistemalicencias.model.entities.LoginRegistro;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import java.util.List;

public class LoginService {
    private final LoginDAO loginDAO = new LoginDAO();

    public List<LoginRegistro> obtenerHistorial() throws BaseDatosException {
        return loginDAO.obtenerTodos();
    }
}