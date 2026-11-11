package modelo;

public enum Prioridad {
    BAJA("Baja"), MEDIA("Media"), ALTA("Alta");
    private final String texto;
    Prioridad(String texto) { this.texto = texto; }
    @Override public String toString() { return texto; }
}