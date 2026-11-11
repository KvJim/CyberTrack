package vista;

import modelo.*;
import servicio.Sistema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelProyectos extends JPanel {
    private final Sistema sis = Sistema.getInstance();
    private final JTextField txtBuscar = new JTextField(15);
    private final JComboBox<Object> cmbCat = new JComboBox<>();
    private final JComboBox<Object> cmbDif = new JComboBox<>();
    private final JComboBox<Object> cmbEst = new JComboBox<>();
    private final JLabel lblMensaje = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Tipo", "Nombre", "Categoría", "Dificultad", "Prioridad", "Estado", "Propietario"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private List<Proyecto> actuales = new ArrayList<>();

    public PanelProyectos() {
        setLayout(new BorderLayout(5, 5));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Nombre:"));
        filtros.add(txtBuscar);
        filtros.add(new JLabel("Categoría:"));
        filtros.add(cmbCat);
        filtros.add(new JLabel("Dificultad:"));
        filtros.add(cmbDif);
        filtros.add(new JLabel("Estado:"));
        filtros.add(cmbEst);
        filtros.add(Util.boton("Buscar", this::buscar));
        filtros.add(Util.boton("Limpiar", () -> { txtBuscar.setText(""); recargar(); }));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(Util.boton("Crear", this::crear));
        acciones.add(Util.boton("Ver detalle", this::detalle));
        acciones.add(Util.boton("Editar", this::editar));
        acciones.add(Util.boton("Eliminar", this::eliminar));
        acciones.add(Util.boton("Cambiar estado", this::cambiarEstado));
        acciones.add(Util.boton("Cambiar prioridad", this::cambiarPrioridad));

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(lblMensaje, BorderLayout.NORTH);
        sur.add(acciones, BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
    }

    public void recargar() {
        cargarCombo(cmbCat, sis.getCategorias().toArray());
        cargarCombo(cmbDif, Dificultad.values());
        cargarCombo(cmbEst, Estado.values());
        buscar();
    }

    private void cargarCombo(JComboBox<Object> c, Object[] items) {
        c.removeAllItems();
        c.addItem("Todas");
        for (Object o : items) c.addItem(o);
    }

    private void buscar() {
        Object oc = cmbCat.getSelectedItem(), od = cmbDif.getSelectedItem(), oe = cmbEst.getSelectedItem();
        Categoria c = oc instanceof Categoria ? (Categoria) oc : null;
        Dificultad d = od instanceof Dificultad ? (Dificultad) od : null;
        Estado e = oe instanceof Estado ? (Estado) oe : null;
        actuales = sis.buscar(txtBuscar.getText(), c, d, e);
        modelo.setRowCount(0);
        for (Proyecto p : actuales) {
            modelo.addRow(new Object[]{p.getId(), p.getTipo(), p.getNombre(), p.getCategoria(),
                    p.getDificultad(), p.getPrioridad(), p.getEstado(), p.getPropietario()});
        }
        lblMensaje.setText(actuales.isEmpty()
                ? "  No se encontraron proyectos que coincidan con los criterios ingresados." : " ");
    }

    private Proyecto seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Util.aviso(this, "Seleccione un proyecto de la lista.");
            return null;
        }
        return actuales.get(fila);
    }

    private Frame ventana() { return (Frame) SwingUtilities.getWindowAncestor(this); }

    private void crear() {
        DialogoProyecto d = new DialogoProyecto(ventana(), null);
        d.setVisible(true);
        if (d.isGuardado()) buscar();
    }

    private void detalle() {
        Proyecto p = seleccionado();
        if (p == null) return;
        new DialogoDetalle(ventana(), p).setVisible(true);
        buscar();
    }

    private void editar() {
        Proyecto p = seleccionado();
        if (p == null) return;
        if (!Util.ejecutar(this, () -> sis.verificarPropietario(p))) return;
        DialogoProyecto d = new DialogoProyecto(ventana(), p);
        d.setVisible(true);
        buscar();
    }

    private void eliminar() {
        Proyecto p = seleccionado();
        if (p == null) return;
        if (!Util.ejecutar(this, () -> sis.verificarPropietario(p))) return;
        if (Util.confirmar(this, "¿Desea eliminar \"" + p.getNombre() + "\"?")) {
            if (Util.ejecutar(this, () -> sis.eliminarProyecto(p))) Util.aviso(this, "Proyecto eliminado.");
            buscar();
        }
    }

    private void cambiarEstado() {
        Proyecto p = seleccionado();
        if (p == null) return;
        Object r = JOptionPane.showInputDialog(this, "Nuevo estado:", "Cambiar estado",
                JOptionPane.QUESTION_MESSAGE, null, Estado.values(), p.getEstado());
        if (r != null) Util.ejecutar(this, () -> sis.cambiarEstado(p, (Estado) r));
        buscar();
    }

    private void cambiarPrioridad() {
        Proyecto p = seleccionado();
        if (p == null) return;
        Object r = JOptionPane.showInputDialog(this, "Nueva prioridad:", "Cambiar prioridad",
                JOptionPane.QUESTION_MESSAGE, null, Prioridad.values(), p.getPrioridad());
        if (r != null) Util.ejecutar(this, () -> sis.cambiarPrioridad(p, (Prioridad) r));
        buscar();
    }
}