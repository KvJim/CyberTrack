package vista;

import modelo.Usuario;
import servicio.Sistema;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private final Sistema sis = Sistema.getInstance();
    private final PanelResumen resumen = new PanelResumen();
    private final PanelProyectos proyectos = new PanelProyectos();
    private PanelAdmin admin;

    public VentanaPrincipal() {
        super("CyberTrack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Usuario u = sis.getUsuarioActual();
        JLabel lblSesion = new JLabel("  Sesión: " + u.getNombre() + " (" + u.getRol() + ")");
        JButton btnSalir = new JButton("Cerrar sesión");
        JPanel top = new JPanel(new BorderLayout());
        top.add(lblSesion, BorderLayout.WEST);
        top.add(btnSalir, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Panel principal", resumen);
        tabs.addTab("Proyectos", proyectos);
        if (u.esAdmin()) {
            admin = new PanelAdmin();
            tabs.addTab("Administración", admin);
        }
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c == resumen) resumen.refrescar();
            else if (c == proyectos) proyectos.recargar();
            else if (c == admin) admin.refrescar();
        });

        btnSalir.addActionListener(e -> {
            sis.cerrarSesion();
            dispose();
            new VentanaLogin().setVisible(true);
        });

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        proyectos.recargar();

        setSize(950, 560);
        setLocationRelativeTo(null);
    }
}