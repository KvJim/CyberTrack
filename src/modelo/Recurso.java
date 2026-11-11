package modelo;

import java.io.Serializable;

public class Recurso implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idRecurso;
    private String nombre;
    private String descripcion;
    private String enlace;
    private final Proyecto proyecto;

    public Recurso(int idRecurso, String nombre, String descripcion, String enlace, Proyecto proyecto) {
        this.idRecurso = idRecurso;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.enlace = enlace;
        this.proyecto = proyecto;
    }

    public int getIdRecurso() { return idRecurso; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEnlace() { return enlace; }
    public void setEnlace(String enlace) { this.enlace = enlace; }
    public Proyecto getProyecto() { return proyecto; }

    @Override public String toString() {
        return nombre + " | " + enlace + (descripcion == null || descripcion.isEmpty() ? "" : " | " + descripcion);
    }
}