package vista;

import modelo.*;
import servicio.Sistema;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DialogoDetalle extends JDialog {
    private final Sistema sis = Sistema.getInstance();
    private final Proyecto p;

    private final JTextArea areaInfo = new JTextArea();
    private final JTextArea areaLecciones = new JTextArea(8, 40);
    private final JTextArea areaResultado = new JTextArea(8, 40);
    private final DefaultListModel<Nota> mNotas = new DefaultListModel<>();
    private final DefaultListModel<Recurso> mRecursos = new DefaultListModel<>();
    private final DefaultListModel<Usuario> mColab = new DefaultListModel<>();
    private final DefaultListModel<Herramienta> mHerr = new DefaultListModel<>();
    private final DefaultListModel<String> mHist = new DefaultListModel<>();
    private final JList<Nota> lNotas = new JList<>(mNotas);
    private final JList<Recurso> lRecursos = new JList<>(mRecursos);
    private final JList<Usuario> lColab = new JList<>(mColab);
    private final JList<Herramienta> lHerr = new JList<>(mHerr);
    private final JList<String> lHist = new JList<>(mHist);

    public DialogoDetalle(Frame owner, Proyecto p) {
        super(owner, p.getTipo() + ": " + p.getNombre(), true);
        this.p = p;

        areaInfo.setEditable(false);
        areaInfo.setLineWrap(true);
        areaInfo.setWrapStyleWord(true);

        JPanel lecciones = new JPanel(new GridLayout(0, 1, 5, 5));
        lecciones.add(seccion("Lecciones aprendidas", areaLecciones, "Guardar lecciones", this::guardarLecciones));
        if (p instanceof Laboratorio)
            lecciones.add(seccion("Resultado del laboratorio", areaResultado, "Guardar resultado", this::guardarResultado));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Información", new JScrollPane(areaInfo));
        tabs.addTab("Notas", Util.panelLista(lNotas,
                Util.boton("Agregar", this::agregarNota),
                Util.boton("Editar", this::editarNota),
                Util.boton("Eliminar", this::eliminarNota)));
        tabs.addTab("Recursos", Util.panelLista(lRecursos,
                Util.boton("Agregar", this::agregarRecurso),
                Util.boton("Editar", this::editarRecurso),
                Util.boton("Eliminar", this::eliminarRecurso)));
        tabs.addTab("Herramientas", Util.panelLista(lHerr,
                Util.boton("Asociar", this::asociarHerramienta),
                Util.boton("Quitar", this::quitarHerramienta)));
        tabs.addTab("Colaboradores", Util.panelLista(lColab,
                Util.boton("Agregar", this::agregarColaborador),
                Util.boton("Eliminar", this::eliminarColaborador)));
        tabs.addTab("Lecciones", lecciones);
        tabs.addTab("Historial", new JScrollPane(lHist));

        add(tabs);
        refrescar();
        setSize(760, 520);
        setLocationRelativeTo(owner);
    }

    private JPanel seccion(String titulo, JTextArea area, String textoBoton, Runnable accion) {
        JPanel s = new JPanel(new BorderLayout(5, 5));
        s.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), titulo, TitledBorder.LEFT, TitledBorder.TOP));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        s.add(new JScrollPane(area), BorderLayout.CENTER);
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT));
        b.add(Util.boton(textoBoton, accion));
        s.add(b, BorderLayout.SOUTH);
        return s;
    }

    private void refrescar() {
        areaInfo.setText(p.resumen());
        areaInfo.setCaretPosition(0);
        mNotas.clear();
        for (Nota n : p.getNotas()) mNotas.addElement(n);
        mRecursos.clear();
        for (Recurso r : p.getRecursos()) mRecursos.addElement(r);
        mHerr.clear();
        for (Herramienta h : p.getHerramientas()) mHerr.addElement(h);
        mColab.clear();
        for (Usuario u : p.getColaboradores()) mColab.addElement(u);
        mHist.clear();
        if (p.getHistorial().isEmpty()) mHist.addElement("Todavía no hay actividades registradas.");
        else for (HistorialProyecto h : p.getHistorial()) mHist.addElement(h.toString());
        areaLecciones.setText(p.getLeccionesAprendidas());
        if (p instanceof Laboratorio) areaResultado.setText(((Laboratorio) p).getResultado());
    }

    private void ejecutar(Util.Accion a) {
        Util.ejecutar(this, a);
        refrescar();
    }

    // Notas
    private void agregarNota() {
        String t = Util.pedir(this, "Contenido de la nota:", "");
        if (t != null) ejecutar(() -> sis.agregarNota(p, t));
    }

    private void editarNota() {
        Nota n = lNotas.getSelectedValue();
        if (n == null) { Util.aviso(this, "Seleccione una nota."); return; }
        String t = Util.pedir(this, "Nuevo contenido:", n.getContenido());
        if (t != null) ejecutar(() -> sis.modificarNota(n, t));
    }

    private void eliminarNota() {
        Nota n = lNotas.getSelectedValue();
        if (n == null) { Util.aviso(this, "Seleccione una nota."); return; }
        if (Util.confirmar(this, "¿Eliminar la nota seleccionada?")) ejecutar(() -> sis.eliminarNota(n));
    }

    // Recursos
    private String[] pedirRecurso(Recurso r) {
        JTextField n = new JTextField(r == null ? "" : r.getNombre(), 20);
        JTextField d = new JTextField(r == null ? "" : r.getDescripcion(), 20);
        JTextField e = new JTextField(r == null ? "" : r.getEnlace(), 20);
        int op = JOptionPane.showConfirmDialog(this,
                Util.formulario(new String[]{"Nombre", "Descripción", "Enlace"}, new JComponent[]{n, d, e}),
                "Recurso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return op == JOptionPane.OK_OPTION ? new String[]{n.getText(), d.getText(), e.getText()} : null;
    }

    private void agregarRecurso() {
        String[] v = pedirRecurso(null);
        if (v != null) ejecutar(() -> sis.agregarRecurso(p, v[0], v[1], v[2]));
    }

    private void editarRecurso() {
        Recurso r = lRecursos.getSelectedValue();
        if (r == null) { Util.aviso(this, "Seleccione un recurso."); return; }
        String[] v = pedirRecurso(r);
        if (v != null) ejecutar(() -> sis.modificarRecurso(r, v[0], v[1], v[2]));
    }

    private void eliminarRecurso() {
        Recurso r = lRecursos.getSelectedValue();
        if (r == null) { Util.aviso(this, "Seleccione un recurso."); return; }
        if (Util.confirmar(this, "¿Eliminar el recurso seleccionado?")) ejecutar(() -> sis.eliminarRecurso(r));
    }

    // Herramientas
    private void asociarHerramienta() {
        List<Herramienta> cat = sis.getHerramientas();
        if (cat.isEmpty()) { Util.aviso(this, "No hay herramientas en el catálogo."); return; }
        Object o = JOptionPane.showInputDialog(this, "Seleccione la herramienta:", "Asociar herramienta",
                JOptionPane.QUESTION_MESSAGE, null, cat.toArray(), cat.get(0));
        if (o != null) ejecutar(() -> sis.asociarHerramienta(p, (Herramienta) o));
    }

    private void quitarHerramienta() {
        Herramienta h = lHerr.getSelectedValue();
        if (h == null) { Util.aviso(this, "Seleccione una herramienta."); return; }
        ejecutar(() -> sis.quitarHerramienta(p, h));
    }

    // Colaboradores
    private void agregarColaborador() {
        List<Usuario> disp = sis.getUsuarios().stream()
                .filter(u -> !u.equals(p.getPropietario())).collect(Collectors.toList());
        if (disp.isEmpty()) { Util.aviso(this, "No hay otros usuarios registrados."); return; }
        Object o = JOptionPane.showInputDialog(this, "Seleccione el usuario:", "Agregar colaborador",
                JOptionPane.QUESTION_MESSAGE, null, disp.toArray(), disp.get(0));
        if (o != null) ejecutar(() -> sis.agregarColaborador(p, (Usuario) o));
    }

    private void eliminarColaborador() {
        Usuario u = lColab.getSelectedValue();
        if (u == null) { Util.aviso(this, "Seleccione un colaborador."); return; }
        if (Util.confirmar(this, "¿Retirar a " + u + " del proyecto?")) ejecutar(() -> sis.eliminarColaborador(p, u));
    }

    // Lecciones y resultado
    private void guardarLecciones() {
        String t = areaLecciones.getText();
        if (Util.ejecutar(this, () -> sis.actualizarLecciones(p, t))) Util.aviso(this, "Lecciones guardadas.");
    }

    private void guardarResultado() {
        String t = areaResultado.getText();
        if (Util.ejecutar(this, () -> sis.registrarResultado((Laboratorio) p, t))) Util.aviso(this, "Resultado guardado.");
    }
}