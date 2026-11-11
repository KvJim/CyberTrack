package vista;

import excepciones.CyberTrackException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Util {

    public interface Accion { void run() throws Exception; }

    public static boolean ejecutar(Component padre, Accion a) {
        try {
            a.run();
            return true;
        } catch (CyberTrackException ex) {
            JOptionPane.showMessageDialog(padre, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje);
    }

    public static String pedir(Component padre, String mensaje, String inicial) {
        Object r = JOptionPane.showInputDialog(padre, mensaje, "CyberTrack", JOptionPane.PLAIN_MESSAGE, null, null, inicial);
        return r == null ? null : r.toString();
    }

    public static List<String> lineas(String texto) {
        List<String> r = new ArrayList<>();
        for (String s : texto.split("\\R")) if (!s.trim().isEmpty()) r.add(s.trim());
        return r;
    }

    public static JPanel formulario(String[] etiquetas, JComponent[] campos) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 5, 3, 5);
        g.anchor = GridBagConstraints.NORTHWEST;
        for (int i = 0; i < etiquetas.length; i++) {
            g.gridy = i;
            g.gridx = 0; g.weightx = 0; g.weighty = 0; g.fill = GridBagConstraints.NONE;
            p.add(new JLabel(etiquetas[i]), g);
            g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
            if (campos[i] instanceof JScrollPane) { g.fill = GridBagConstraints.BOTH; g.weighty = 1; }
            p.add(campos[i], g);
        }
        return p;
    }

    public static JButton boton(String texto, Runnable r) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> r.run());
        return b;
    }

    public static JPanel panelLista(JList<?> lista, JButton... botones) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(new JScrollPane(lista), BorderLayout.CENTER);
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton x : botones) b.add(x);
        p.add(b, BorderLayout.SOUTH);
        return p;
    }

    public static ListCellRenderer<Object> renderer(Function<Object, String> f) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean fo) {
                super.getListCellRendererComponent(l, v, i, s, fo);
                setText(f.apply(v));
                return this;
            }
        };
    }
}