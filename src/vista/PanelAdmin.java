package vista;

import modelo.*;
import servicio.Sistema;

import javax.swing.*;

public class PanelAdmin extends JTabbedPane {
    private final Sistema sis = Sistema.getInstance();
    private final DefaultListModel<Usuario> mU = new DefaultListModel<>();
    private final DefaultListModel<Categoria> mC = new DefaultListModel<>();
    private final DefaultListModel<Herramienta> mH = new DefaultListModel<>();
    private final JList<Usuario> lU = new JList<>(mU);
    private final JList<Categoria> lC = new JList<>(mC);
    private final JList<Herramienta> lH = new JList<>(mH);

    public PanelAdmin() {
        lU.setCellRenderer(Util.renderer(o -> {
            Usuario u = (Usuario) o;
            return u.getNombre() + " (" + u.getNombreUsuario() + ") - " + u.getRol();
        }));
        lC.setCellRenderer(Util.renderer(o -> {
            Categoria c = (Categoria) o;
            return c.getNombre() + " - " + c.getDescripcion();
        }));
        lH.setCellRenderer(Util.renderer(o -> {
            Herramienta h = (Herramienta) o;
            return h.getNombre() + " [" + h.getCategoria() + "] - " + h.getDescripcion();
        }));

        addTab("Usuarios", Util.panelLista(lU,
                Util.boton("Editar", this::editarUsuario),
                Util.boton("Eliminar", this::eliminarUsuario)));
        addTab("Categorías", Util.panelLista(lC,
                Util.boton("Crear", () -> pedirCategoria(null)),
                Util.boton("Editar", this::editarCategoria),
                Util.boton("Eliminar", this::eliminarCategoria)));
        addTab("Herramientas", Util.panelLista(lH,
                Util.boton("Crear", () -> pedirHerramienta(null)),
                Util.boton("Editar", this::editarHerramienta),
                Util.boton("Eliminar", this::eliminarHerramienta)));
        refrescar();
    }

    public void refrescar() {
        mU.clear();
        for (Usuario u : sis.getUsuarios()) mU.addElement(u);
        mC.clear();
        for (Categoria c : sis.getCategorias()) mC.addElement(c);
        mH.clear();
        for (Herramienta h : sis.getHerramientas()) mH.addElement(h);
    }

    // Usuarios
    private void editarUsuario() {
        Usuario u = lU.getSelectedValue();
        if (u == null) { Util.aviso(this, "Seleccione un usuario."); return; }
        JTextField n = new JTextField(u.getNombre(), 20);
        JComboBox<Rol> r = new JComboBox<>(Rol.values());
        r.setSelectedItem(u.getRol());
        JPasswordField pw = new JPasswordField(20);
        int op = JOptionPane.showConfirmDialog(this,
                Util.formulario(new String[]{"Nombre", "Rol", "Nueva contraseña (opcional)"}, new JComponent[]{n, r, pw}),
                "Editar usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) return;
        if (Util.ejecutar(this, () -> sis.actualizarUsuario(u, n.getText(), (Rol) r.getSelectedItem(), new String(pw.getPassword()))))
            Util.aviso(this, "Los cambios se guardaron correctamente.");
        refrescar();
    }

    private void eliminarUsuario() {
        Usuario u = lU.getSelectedValue();
        if (u == null) { Util.aviso(this, "Seleccione un usuario."); return; }
        if (Util.confirmar(this, "También se eliminarán los proyectos de los que " + u
                + " es propietario y su participación como colaborador. ¿Continuar?")) {
            Util.ejecutar(this, () -> sis.eliminarUsuario(u));
            refrescar();
        }
    }

    // Categorías
    private void editarCategoria() {
        Categoria c = lC.getSelectedValue();
        if (c == null) { Util.aviso(this, "Seleccione una categoría."); return; }
        pedirCategoria(c);
    }

    private void pedirCategoria(Categoria c) {
        JTextField n = new JTextField(c == null ? "" : c.getNombre(), 20);
        JTextField d = new JTextField(c == null ? "" : c.getDescripcion(), 20);
        int op = JOptionPane.showConfirmDialog(this,
                Util.formulario(new String[]{"Nombre", "Descripción"}, new JComponent[]{n, d}),
                "Categoría", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) return;
        Util.ejecutar(this, () -> {
            if (c == null) sis.crearCategoria(n.getText(), d.getText());
            else sis.modificarCategoria(c, n.getText(), d.getText());
        });
        refrescar();
    }

    private void eliminarCategoria() {
        Categoria c = lC.getSelectedValue();
        if (c == null) { Util.aviso(this, "Seleccione una categoría."); return; }
        if (Util.confirmar(this, "¿Eliminar la categoría " + c + "?")) {
            Util.ejecutar(this, () -> sis.eliminarCategoria(c));
            refrescar();
        }
    }

    // Herramientas
    private void editarHerramienta() {
        Herramienta h = lH.getSelectedValue();
        if (h == null) { Util.aviso(this, "Seleccione una herramienta."); return; }
        pedirHerramienta(h);
    }

    private void pedirHerramienta(Herramienta h) {
        JTextField n = new JTextField(h == null ? "" : h.getNombre(), 20);
        JTextField d = new JTextField(h == null ? "" : h.getDescripcion(), 20);
        JComboBox<Categoria> c = new JComboBox<>(sis.getCategorias().toArray(new Categoria[0]));
        if (h != null) c.setSelectedItem(h.getCategoria());
        int op = JOptionPane.showConfirmDialog(this,
                Util.formulario(new String[]{"Nombre", "Descripción", "Categoría"}, new JComponent[]{n, d, c}),
                "Herramienta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) return;
        Util.ejecutar(this, () -> {
            if (h == null) sis.crearHerramienta(n.getText(), d.getText(), (Categoria) c.getSelectedItem());
            else sis.modificarHerramienta(h, n.getText(), d.getText(), (Categoria) c.getSelectedItem());
        });
        refrescar();
    }

    private void eliminarHerramienta() {
        Herramienta h = lH.getSelectedValue();
        if (h == null) { Util.aviso(this, "Seleccione una herramienta."); return; }
        if (Util.confirmar(this, "¿Eliminar la herramienta " + h + "?")) {
            Util.ejecutar(this, () -> sis.eliminarHerramienta(h));
            refrescar();
        }
    }
}