package ec.edu.sistemalicencias.model.entities;

public class Usuario {
    private Long id;
    private String username;
    private String passwordHash;
    private String rol;
    private boolean estado;
    private String NombreCompleto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return NombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.NombreCompleto = nombreCompleto;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getUsername() {
        return NombreCompleto;
    }

    public void setUsername(String username) {
        this.NombreCompleto = username;
    }
}
