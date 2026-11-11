package vista;

import modelo.Estado;
import servicio.Sistema;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class PanelResumen extends JPanel {
    private final JLabel lblTotal = new JLabel();
    private final Map<Estado, JLabel> labels = new EnumMap<>(Estado.class);

    public PanelResumen() {
        setLayout(new GridLayout(0, 1, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        Font f = lblTotal.getFont().deriveFont(Font.PLAIN, 18f);
        lblTotal.setFont(f.deriveFont(Font.BOLD));
        add(lblTotal);
        for (Estado e : Estado.values()) {
            JLabel l = new JLabel();
            l.setFont(f);
            labels.put(e, l);
            add(l);
        }
        refrescar();
    }

    public void refrescar() {
        Map<Estado, Long> r = Sistema.getInstance().resumenPorEstado();
        long total = 0;
        for (Estado e : Estado.values()) {
            long n = r.get(e);
            total += n;
            labels.get(e).setText(e + ": " + n);
        }
        lblTotal.setText("Total de proyectos y laboratorios: " + total);
    }
}