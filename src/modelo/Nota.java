package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Nota implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idNota;
    private String contenido;
    private final LocalDateTime fecha;
    private final Usuario autor;
    private final Proyecto proyecto;

    public Nota(int idNota, String contenido, Usuario autor, Proyecto proyecto) {
        this.idNota = idNota;
        this.contenido = contenido;
        this.autor = autor;
        this.proyecto = proyecto;
        this.fecha = LocalDateTime.now();
    }

    public int getIdNota() { return idNota; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFecha() { return fecha; }
    public Usuario getAutor() { return autor; }
    public Proyecto getProyecto() { return proyecto; }

    @Override public String toString() {
        return "[" + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "] " + autor + ": " + contenido;
    }
}