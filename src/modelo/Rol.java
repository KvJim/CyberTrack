package modelo;

public enum Rol {
    USUARIO("Usuario"), ADMINISTRADOR("Administrador");
    private final String texto;
    Rol(String texto) { this.texto = texto; }
    @Override public String toString() { return texto; }
}