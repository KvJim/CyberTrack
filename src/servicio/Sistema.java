package servicio;

import excepciones.*;
import modelo.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Sistema {
    private static final String ARCHIVO = "cybertrack.dat";
    private static Sistema instancia;

    public static synchronized Sistema getInstance() {
        if (instancia == null) instancia = new Sistema();
        return instancia;
    }

    private static class Datos implements Serializable {
        private static final long serialVersionUID = 1L;
        List<Usuario> usuarios = new ArrayList<>();
        List<Categoria> categorias = new ArrayList<>();
        List<Herramienta> herramientas = new ArrayList<>();
        List<Proyecto> proyectos = new ArrayList<>();
        int contador = 1;
    }

    private Datos datos;
    private Usuario actual;

    private Sistema() { cargar(); }

    // ---------- Persistencia ----------
    private void cargar() {
        File f = new File(ARCHIVO);
        if (f.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
                datos = (Datos) in.readObject();
                return;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("No se pudo leer el archivo de datos: " + e.getMessage());
            }
        }
        datos = new Datos();
        precargar();
        guardar();
    }

    public synchronized void guardar() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            out.writeObject(datos);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private void precargar() {
        datos.usuarios.add(new Usuario(nuevoId(), "Administrador", "admin", "admin123", Rol.ADMINISTRADOR));
        String[] cats = {"Web", "Redes", "Linux", "Active Directory", "Pentesting", "Blue Team", "Forense",
                "Cloud", "Malware Analysis", "Programación", "Desarrollo Web", "Bases de Datos", "Otros"};
        for (String c : cats) datos.categorias.add(new Categoria(nuevoId(), c, "Categoría " + c));
        Categoria web = datos.categorias.get(0), redes = datos.categorias.get(1),
                pent = datos.categorias.get(4), prog = datos.categorias.get(9);
        datos.herramientas.add(new Herramienta(nuevoId(), "Nmap", "Escáner de puertos y servicios", redes));
        datos.herramientas.add(new Herramienta(nuevoId(), "Wireshark", "Analizador de tráfico de red", redes));
        datos.herramientas.add(new Herramienta(nuevoId(), "Burp Suite", "Proxy para pruebas web", web));
        datos.herramientas.add(new Herramienta(nuevoId(), "Metasploit", "Framework de explotación", pent));
        datos.herramientas.add(new Herramienta(nuevoId(), "Nessus", "Escáner de vulnerabilidades", pent));
        datos.herramientas.add(new Herramienta(nuevoId(), "Git", "Control de versiones", prog));
        datos.herramientas.add(new Herramienta(nuevoId(), "Docker", "Contenedores", prog));
    }

    public synchronized int nuevoId() { return datos.contador++; }

    // ---------- Sesión y usuarios ----------
    public synchronized Usuario getUsuarioActual() { return actual; }
    public synchronized void cerrarSesion() { actual = null; }

    private Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario u : datos.usuarios)
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) return u;
        return null;
    }

    public synchronized Usuario registrar(String nombre, String usuario, String password) throws CyberTrackException {
        if (nombre == null || nombre.trim().isEmpty() || usuario == null || usuario.trim().isEmpty()
                || password == null || password.isEmpty())
            throw new DatosInvalidosException("Debe completar los campos obligatorios.");
        if (buscarUsuario(usuario.trim()) != null)
            throw new DuplicadoException("El nombre de usuario ya existe, utilice otro.");
        Usuario u = new Usuario(nuevoId(), nombre.trim(), usuario.trim(), password, Rol.USUARIO);
        datos.usuarios.add(u);
        guardar();
        return u;
    }

    public synchronized Usuario iniciarSesion(String usuario, String password) throws CyberTrackException {
        Usuario u = buscarUsuario(usuario);
        if (u == null || !u.validarPassword(password))
            throw new CyberTrackException("Usuario o contraseña incorrectos");
        actual = u;
        return u;
    }

    public synchronized void modificarPerfil(String nombre, String nuevaPassword) throws CyberTrackException {
        if (nombre == null || nombre.trim().isEmpty()) throw new DatosInvalidosException("El nombre es obligatorio.");
        actual.setNombre(nombre.trim());
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) actual.cambiarPassword(nuevaPassword);
        guardar();
    }

    private void exigirAdmin() throws AccesoDenegadoException {
        if (actual == null || !actual.esAdmin())
            throw new AccesoDenegadoException("No tiene los permisos necesarios.");
    }

    public synchronized List<Usuario> getUsuarios() { return new ArrayList<>(datos.usuarios); }

    public synchronized void actualizarUsuario(Usuario u, String nombre, Rol rol, String nuevaPassword)
            throws CyberTrackException {
        exigirAdmin();
        if (nombre == null || nombre.trim().isEmpty()) throw new DatosInvalidosException("El nombre es obligatorio.");
        if (u.equals(actual) && rol != Rol.ADMINISTRADOR)
            throw new AccesoDenegadoException("No puede quitarse el rol de administrador a sí mismo.");
        u.setNombre(nombre.trim());
        u.setRol(rol);
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) u.cambiarPassword(nuevaPassword);
        guardar();
    }

    public synchronized void eliminarUsuario(Usuario u) throws CyberTrackException {
        exigirAdmin();
        if (u.equals(actual))
            throw new AccesoDenegadoException("No es posible eliminar la cuenta del administrador en uso.");
        datos.proyectos.removeIf(p -> p.getPropietario().equals(u));
        for (Proyecto p : datos.proyectos) p.eliminarColaborador(u);
        datos.usuarios.remove(u);
        guardar();
    }

    // ---------- Catálogos ----------
    public synchronized List<Categoria> getCategorias() { return new ArrayList<>(datos.categorias); }
    public synchronized List<Herramienta> getHerramientas() { return new ArrayList<>(datos.herramientas); }

    private void validarNombre(String n) throws DatosInvalidosException {
        if (n == null || n.trim().isEmpty()) throw new DatosInvalidosException("El nombre es obligatorio.");
    }

    public synchronized void crearCategoria(String nombre, String desc) throws CyberTrackException {
        exigirAdmin();
        validarNombre(nombre);
        for (Categoria c : datos.categorias)
            if (c.getNombre().equalsIgnoreCase(nombre.trim())) throw new DuplicadoException("Ya existe un registro con ese nombre.");
        datos.categorias.add(new Categoria(nuevoId(), nombre.trim(), desc));
        guardar();
    }

    public synchronized void modificarCategoria(Categoria cat, String nombre, String desc) throws CyberTrackException {
        exigirAdmin();
        validarNombre(nombre);
        for (Categoria c : datos.categorias)
            if (c != cat && c.getNombre().equalsIgnoreCase(nombre.trim())) throw new DuplicadoException("Ya existe un registro con ese nombre.");
        cat.setNombre(nombre.trim());
        cat.setDescripcion(desc);
        guardar();
    }

    public synchronized void eliminarCategoria(Categoria cat) throws CyberTrackException {
        exigirAdmin();
        for (Proyecto p : datos.proyectos)
            if (cat.equals(p.getCategoria()))
                throw new CyberTrackException("La categoría está en uso por proyectos. Primero resuelva esa relación.");
        for (Herramienta h : datos.herramientas)
            if (cat.equals(h.getCategoria()))
                throw new CyberTrackException("La categoría está en uso por herramientas. Primero resuelva esa relación.");
        datos.categorias.remove(cat);
        guardar();
    }

    public synchronized void crearHerramienta(String nombre, String desc, Categoria cat) throws CyberTrackException {
        exigirAdmin();
        validarNombre(nombre);
        if (cat == null) throw new DatosInvalidosException("Debe seleccionar una categoría.");
        for (Herramienta h : datos.herramientas)
            if (h.getNombre().equalsIgnoreCase(nombre.trim())) throw new DuplicadoException("Ya existe un registro con ese nombre.");
        datos.herramientas.add(new Herramienta(nuevoId(), nombre.trim(), desc, cat));
        guardar();
    }

    public synchronized void modificarHerramienta(Herramienta herr, String nombre, String desc, Categoria cat)
            throws CyberTrackException {
        exigirAdmin();
        validarNombre(nombre);
        if (cat == null) throw new DatosInvalidosException("Debe seleccionar una categoría.");
        for (Herramienta h : datos.herramientas)
            if (h != herr && h.getNombre().equalsIgnoreCase(nombre.trim())) throw new DuplicadoException("Ya existe un registro con ese nombre.");
        herr.setNombre(nombre.trim());
        herr.setDescripcion(desc);
        herr.setCategoria(cat);
        guardar();
    }

    public synchronized void eliminarHerramienta(Herramienta herr) throws CyberTrackException {
        exigirAdmin();
        for (Proyecto p : datos.proyectos)
            if (p.getHerramientas().contains(herr))
                throw new CyberTrackException("La herramienta está asociada a proyectos y no se puede eliminar.");
        datos.herramientas.remove(herr);
        guardar();
    }

    // ---------- Proyectos ----------
    public synchronized List<Proyecto> proyectosVisibles() {
        return datos.proyectos.stream().filter(p -> p.tieneAcceso(actual)).collect(Collectors.toList());
    }

    public synchronized List<Proyecto> buscar(String texto, Categoria cat, Dificultad dif, Estado est) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        return proyectosVisibles().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(t))
                .filter(p -> cat == null || cat.equals(p.getCategoria()))
                .filter(p -> dif == null || p.getDificultad() == dif)
                .filter(p -> est == null || p.getEstado() == est)
                .collect(Collectors.toList());
    }

    public synchronized Map<Estado, Long> resumenPorEstado() {
        Map<Estado, Long> r = new EnumMap<>(Estado.class);
        for (Estado e : Estado.values()) r.put(e, 0L);
        for (Proyecto p : proyectosVisibles()) r.put(p.getEstado(), r.get(p.getEstado()) + 1);
        return r;
    }

    public void verificarPropietario(Proyecto p) throws AccesoDenegadoException {
        if (!p.esPropietario(actual))
            throw new AccesoDenegadoException("Esta función es exclusiva del propietario del proyecto.");
    }

    private void registrarHistorial(Proyecto p, String accion, String desc) {
        p.registrarActividad(new HistorialProyecto(nuevoId(), accion, desc, actual, p));
    }

    public synchronized void agregarProyecto(Proyecto p) {
        datos.proyectos.add(p);
        registrarHistorial(p, "Proyecto creado", "Se creó el " + p.getTipo().toLowerCase() + " \"" + p.getNombre() + "\"");
        guardar();
    }

    public synchronized void modificarProyecto(Proyecto p) throws CyberTrackException {
        verificarPropietario(p);
        registrarHistorial(p, "Proyecto modificado", "Se actualizó la información general");
        guardar();
    }

    public synchronized void eliminarProyecto(Proyecto p) throws CyberTrackException {
        verificarPropietario(p);
        datos.proyectos.remove(p);
        guardar();
    }

    public synchronized void cambiarEstado(Proyecto p, Estado e) throws CyberTrackException {
        verificarPropietario(p);
        p.cambiarEstado(e);
        if (e == Estado.COMPLETADO && p.getFechaFinalizacion() == null) p.setFechaFinalizacion(LocalDate.now());
        registrarHistorial(p, "Estado cambiado", "Nuevo estado: " + e);
        guardar();
    }

    public synchronized void cambiarPrioridad(Proyecto p, Prioridad pr) throws CyberTrackException {
        verificarPropietario(p);
        p.cambiarPrioridad(pr);
        registrarHistorial(p, "Prioridad cambiada", "Nueva prioridad: " + pr);
        guardar();
    }

    public synchronized void actualizarLecciones(Proyecto p, String texto) throws CyberTrackException {
        verificarPropietario(p);
        p.actualizarLeccionesAprendidas(texto);
        registrarHistorial(p, "Lecciones actualizadas", "Se registraron las lecciones aprendidas");
        guardar();
    }

    public synchronized void registrarResultado(Laboratorio l, String texto) throws CyberTrackException {
        verificarPropietario(l);
        l.registrarResultado(texto);
        registrarHistorial(l, "Resultado registrado", "Se registró el resultado del laboratorio");
        guardar();
    }

    // ---------- Notas ----------
    public synchronized void agregarNota(Proyecto p, String contenido) throws CyberTrackException {
        if (!p.tieneAcceso(actual)) throw new AccesoDenegadoException("No tiene acceso a este proyecto.");
        if (contenido == null || contenido.trim().isEmpty())
            throw new DatosInvalidosException("La nota no puede estar vacía.");
        p.agregarNota(new Nota(nuevoId(), contenido.trim(), actual, p));
        registrarHistorial(p, "Nota registrada", "Se agregó una nota");
        guardar();
    }

    private void verificarNota(Nota n) throws AccesoDenegadoException {
        if (!n.getAutor().equals(actual) && !n.getProyecto().esPropietario(actual))
            throw new AccesoDenegadoException("Solo el autor de la nota o el propietario del proyecto pueden hacerlo.");
    }

    public synchronized void modificarNota(Nota n, String contenido) throws CyberTrackException {
        verificarNota(n);
        if (contenido == null || contenido.trim().isEmpty())
            throw new DatosInvalidosException("La nota no puede estar vacía.");
        n.setContenido(contenido.trim());
        registrarHistorial(n.getProyecto(), "Nota modificada", "Se modificó una nota");
        guardar();
    }

    public synchronized void eliminarNota(Nota n) throws CyberTrackException {
        verificarNota(n);
        n.getProyecto().eliminarNota(n);
        registrarHistorial(n.getProyecto(), "Nota eliminada", "Se eliminó una nota");
        guardar();
    }

    // ---------- Recursos ----------
    public synchronized void agregarRecurso(Proyecto p, String nombre, String desc, String enlace) throws CyberTrackException {
        verificarPropietario(p);
        validarNombre(nombre);
        p.agregarRecurso(new Recurso(nuevoId(), nombre.trim(), desc, enlace, p));
        registrarHistorial(p, "Recurso agregado", nombre.trim());
        guardar();
    }

    public synchronized void modificarRecurso(Recurso r, String nombre, String desc, String enlace) throws CyberTrackException {
        verificarPropietario(r.getProyecto());
        validarNombre(nombre);
        r.setNombre(nombre.trim());
        r.setDescripcion(desc);
        r.setEnlace(enlace);
        registrarHistorial(r.getProyecto(), "Recurso modificado", nombre.trim());
        guardar();
    }

    public synchronized void eliminarRecurso(Recurso r) throws CyberTrackException {
        verificarPropietario(r.getProyecto());
        r.getProyecto().eliminarRecurso(r);
        registrarHistorial(r.getProyecto(), "Recurso eliminado", r.getNombre());
        guardar();
    }

    // ---------- Herramientas y colaboradores del proyecto ----------
    public synchronized void asociarHerramienta(Proyecto p, Herramienta h) throws CyberTrackException {
        verificarPropietario(p);
        p.agregarHerramienta(h);
        registrarHistorial(p, "Herramienta agregada", h.getNombre());
        guardar();
    }

    public synchronized void quitarHerramienta(Proyecto p, Herramienta h) throws CyberTrackException {
        verificarPropietario(p);
        p.quitarHerramienta(h);
        registrarHistorial(p, "Herramienta retirada", h.getNombre());
        guardar();
    }

    public synchronized void agregarColaborador(Proyecto p, Usuario u) throws CyberTrackException {
        verificarPropietario(p);
        p.agregarColaborador(u);
        registrarHistorial(p, "Colaborador agregado", u.getNombre());
        guardar();
    }

    public synchronized void eliminarColaborador(Proyecto p, Usuario u) throws CyberTrackException {
        verificarPropietario(p);
        p.eliminarColaborador(u);
        registrarHistorial(p, "Colaborador eliminado", u.getNombre());
        guardar();
    }
}