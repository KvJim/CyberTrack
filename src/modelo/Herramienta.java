package modelo;

import java.io.Serializable;

public class Herramienta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idHerramienta;
    private String nombre;
    private String descripcion;
    private Categoria categoria;

    public Herramienta(int idHerramienta, String nombre, String descripcion, Categoria categoria) {
        this.idHerramienta = idHerramienta;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public int getIdHerramienta() { return idHerramienta; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override public boolean equals(Object o) { return o instanceof Herramienta && ((Herramienta) o).idHerramienta == idHerramienta; }
    @Override public int hashCode() { return idHerramienta; }
    @Override public String toString() { return nombre; }
}