package ec.edu.sistemalicencias.model.entities;

import java.sql.Timestamp;

public class LoginRegistro {
    private int idLogin;
    private long idUsuario;
    private String username;
    private Timestamp fechaIngreso;

    public int getIdLogin() { return idLogin; }
    public void setIdLogin(int idLogin) { this.idLogin = idLogin; }

    public long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(long idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}