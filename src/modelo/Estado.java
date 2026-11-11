package modelo;

public enum Estado {
    PENDIENTE("Pendiente"), EN_PROGRESO("En progreso"), EN_PAUSA("En pausa"), COMPLETADO("Completado");
    private final String texto;
    Estado(String texto) { this.texto = texto; }
    @Override public String toString() { return texto; }
}