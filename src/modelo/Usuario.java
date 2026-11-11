package modelo;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idUsuario;
    private String nombre;
    private final String nombreUsuario;
    private String passwordHash;
    private Rol rol;

    public Usuario(int idUsuario, String nombre, String nombreUsuario, String password, Rol rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = cifrar(password);
        this.rol = rol;
    }

    public static String cifrar(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean validarPassword(String password) { return passwordHash.equals(cifrar(password)); }
    public void cambiarPassword(String password) { this.passwordHash = cifrar(password); }
    public boolean esAdmin() { return rol == Rol.ADMINISTRADOR; }

    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNombreUsuario() { return nombreUsuario; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override public boolean equals(Object o) { return o instanceof Usuario && ((Usuario) o).idUsuario == idUsuario; }
    @Override public int hashCode() { return idUsuario; }
    @Override public String toString() { return nombreUsuario; }
}