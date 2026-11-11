package modelo;

public enum Dificultad {
    BASICA("Básica"), INTERMEDIA("Intermedia"), AVANZADA("Avanzada");
    private final String texto;
    Dificultad(String texto) { this.texto = texto; }
    @Override public String toString() { return texto; }
}