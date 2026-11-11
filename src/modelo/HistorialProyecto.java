package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorialProyecto implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idHistorial;
    private final LocalDateTime fecha;
    private final String accion;
    private final String descripcion;
    private final Usuario usuario;
    private final Proyecto proyecto;

    public HistorialProyecto(int idHistorial, String accion, String descripcion, Usuario usuario, Proyecto proyecto) {
        this.idHistorial = idHistorial;
        this.fecha = LocalDateTime.now();
        this.accion = accion;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.proyecto = proyecto;
    }

    public int getIdHistorial() { return idHistorial; }
    public LocalDateTime getFecha() { return fecha; }
    public String getAccion() { return accion; }
    public String getDescripcion() { return descripcion; }
    public Usuario getUsuario() { return usuario; }
    public Proyecto getProyecto() { return proyecto; }

    @Override public String toString() {
        return "[" + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "] "
                + accion + " - " + descripcion + " (" + usuario + ")";
    }
}