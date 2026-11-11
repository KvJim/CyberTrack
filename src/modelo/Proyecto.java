package modelo;

import excepciones.DuplicadoException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Proyecto implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final int idProyecto;
    protected String nombre;
    protected String descripcion = "";
    protected Categoria categoria;
    protected Dificultad dificultad = Dificultad.BASICA;
    protected Prioridad prioridad = Prioridad.MEDIA;
    protected Estado estado = Estado.PENDIENTE;
    protected LocalDate fechaInicio;
    protected LocalDate fechaFinalizacion;
    protected String objetivoPrincipal = "";
    protected List<String> objetivosEspecificos = new ArrayList<>();
    protected List<String> habilidadesAprendidas = new ArrayList<>();
    protected String leccionesAprendidas = "";
    protected final Usuario propietario;
    protected List<Usuario> colaboradores = new ArrayList<>();
    protected List<Herramienta> herramientas = new ArrayList<>();
    protected List<Recurso> recursos = new ArrayList<>();
    protected List<Nota> notas = new ArrayList<>();
    protected List<HistorialProyecto> historial = new ArrayList<>();

    public Proyecto(int idProyecto, String nombre, Usuario propietario) {
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.propietario = propietario;
    }

    // Polimorfismo: Laboratorio sobrescribe estos métodos
    public String getTipo() { return "Proyecto"; }

    public String resumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo: ").append(getTipo()).append("\n");
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Descripción: ").append(descripcion).append("\n");
        sb.append("Categoría: ").append(fmt(categoria)).append("\n");
        sb.append("Dificultad: ").append(dificultad).append("\n");
        sb.append("Prioridad: ").append(prioridad).append("\n");
        sb.append("Estado: ").append(estado).append("\n");
        sb.append("Fecha de inicio: ").append(fmt(fechaInicio)).append("\n");
        sb.append("Fecha de finalización: ").append(fmt(fechaFinalizacion)).append("\n");
        sb.append("Duración (días): ").append(getDuracion()).append("\n");
        sb.append("Propietario: ").append(propietario.getNombre()).append("\n");
        sb.append("Objetivo principal: ").append(objetivoPrincipal).append("\n");
        sb.append("Objetivos específicos:\n");
        for (String o : objetivosEspecificos) sb.append("  - ").append(o).append("\n");
        sb.append("Habilidades aprendidas:\n");
        for (String h : habilidadesAprendidas) sb.append("  - ").append(h).append("\n");
        return sb.toString();
    }

    protected static String fmt(Object o) { return o == null ? "-" : o.toString(); }

    public int getDuracion() {
        if (fechaInicio == null || fechaFinalizacion == null) return 0;
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFinalizacion);
    }

    public boolean esPropietario(Usuario u) { return u != null && propietario.equals(u); }
    public boolean tieneAcceso(Usuario u) { return u != null && (esPropietario(u) || colaboradores.contains(u)); }

    public void cambiarEstado(Estado e) { this.estado = e; }
    public void cambiarPrioridad(Prioridad p) { this.prioridad = p; }
    public void actualizarLeccionesAprendidas(String t) { this.leccionesAprendidas = t; }

    public void agregarNota(Nota n) { notas.add(n); }
    public void eliminarNota(Nota n) { notas.remove(n); }
    public void agregarRecurso(Recurso r) { recursos.add(r); }
    public void eliminarRecurso(Recurso r) { recursos.remove(r); }
    public void registrarActividad(HistorialProyecto h) { historial.add(h); }

    public void agregarHerramienta(Herramienta h) throws DuplicadoException {
        if (herramientas.contains(h)) throw new DuplicadoException("La herramienta ya está asociada al proyecto.");
        herramientas.add(h);
    }
    public void quitarHerramienta(Herramienta h) { herramientas.remove(h); }

    public void agregarColaborador(Usuario u) throws DuplicadoException {
        if (esPropietario(u) || colaboradores.contains(u))
            throw new DuplicadoException("El usuario ya forma parte del proyecto.");
        colaboradores.add(u);
    }
    public void eliminarColaborador(Usuario u) { colaboradores.remove(u); }

    public int getId() { return idProyecto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Dificultad getDificultad() { return dificultad; }
    public void setDificultad(Dificultad dificultad) { this.dificultad = dificultad; }
    public Prioridad getPrioridad() { return prioridad; }
    public Estado getEstado() { return estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate f) { this.fechaInicio = f; }
    public LocalDate getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDate f) { this.fechaFinalizacion = f; }
    public String getObjetivoPrincipal() { return objetivoPrincipal; }
    public void setObjetivoPrincipal(String o) { this.objetivoPrincipal = o; }
    public List<String> getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(List<String> l) { this.objetivosEspecificos = l; }
    public List<String> getHabilidadesAprendidas() { return habilidadesAprendidas; }
    public void setHabilidadesAprendidas(List<String> l) { this.habilidadesAprendidas = l; }
    public String getLeccionesAprendidas() { return leccionesAprendidas; }
    public Usuario getPropietario() { return propietario; }
    public List<Usuario> getColaboradores() { return colaboradores; }
    public List<Herramienta> getHerramientas() { return herramientas; }
    public List<Recurso> getRecursos() { return recursos; }
    public List<Nota> getNotas() { return notas; }
    public List<HistorialProyecto> getHistorial() { return historial; }

    @Override public String toString() { return nombre; }
}