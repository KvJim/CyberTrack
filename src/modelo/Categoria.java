package modelo;

import java.io.Serializable;

public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idCategoria;
    private String nombre;
    private String descripcion;

    public Categoria(int idCategoria, String nombre, String descripcion) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdCategoria() { return idCategoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override public boolean equals(Object o) { return o instanceof Categoria && ((Categoria) o).idCategoria == idCategoria; }
    @Override public int hashCode() { return idCategoria; }
    @Override public String toString() { return nombre; }
}