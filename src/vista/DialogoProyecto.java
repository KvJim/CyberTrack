package vista;

import excepciones.CyberTrackException;
import excepciones.DatosInvalidosException;
import modelo.*;
import servicio.Sistema;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DialogoProyecto extends JDialog {
    private final Sistema sis = Sistema.getInstance();
    private final Proyecto existente;
    private boolean guardado = false;

    private final JTextField txtNombre = new JTextField(25);
    private final JTextField txtInicio = new JTextField(10);
    private final JTextField txtFin = new JTextField(10);
    private final JTextField txtObjetivo = new JTextField(25);
    private final JTextArea areaDesc = new JTextArea(3, 25);
    private final JTextArea areaObjEsp = new JTextArea(3, 25);
    private final JTextArea areaHab = new JTextArea(3, 25);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Proyecto general", "Laboratorio"});
    private final JComboBox<Categoria> cmbCat = new JComboBox<>();
    private final JComboBox<Dificultad> cmbDif = new JComboBox<>(Dificultad.values());
    private final JComboBox<Prioridad> cmbPri = new JComboBox<>(Prioridad.values());
    private final JComboBox<Estado> cmbEst = new JComboBox<>(Estado.values());

    public DialogoProyecto(Frame owner, Proyecto existente) {
        super(owner, existente == null ? "Crear proyecto o laboratorio" : "Editar proyecto", true);
        this.existente = existente;
        for (Categoria c : sis.getCategorias()) cmbCat.addItem(c);

        if (existente != null) {
            txtNombre.setText(existente.getNombre());
            areaDesc.setText(existente.getDescripcion());
            cmbTipo.setSelectedIndex(existente instanceof Laboratorio ? 1 : 0);
            cmbTipo.setEnabled(false);
            cmbCat.setSelectedItem(existente.getCategoria());
            cmbDif.setSelectedItem(existente.getDificultad());
            cmbPri.setSelectedItem(existente.getPrioridad());
            cmbEst.setSelectedItem(existente.getEstado());
            txtInicio.setText(existente.getFechaInicio() == null ? "" : existente.getFechaInicio().toString());
            txtFin.setText(existente.getFechaFinalizacion() == null ? "" : existente.getFechaFinalizacion().toString());
            txtObjetivo.setText(existente.getObjetivoPrincipal());
            areaObjEsp.setText(String.join("\n", existente.getObjetivosEspecificos()));
            areaHab.setText(String.join("\n", existente.getHabilidadesAprendidas()));
        } else {
            txtInicio.setText(LocalDate.now().toString());
        }

        JPanel form = Util.formulario(
                new String[]{"Tipo", "Nombre *", "Descripción", "Categoría *", "Dificultad", "Prioridad", "Estado",
                        "Inicio (AAAA-MM-DD)", "Fin (AAAA-MM-DD)", "Objetivo principal *",
                        "Objetivos específicos (uno por línea)", "Habilidades (una por línea)"},
                new JComponent[]{cmbTipo, txtNombre, new JScrollPane(areaDesc), cmbCat, cmbDif, cmbPri, cmbEst,
                        txtInicio, txtFin, txtObjetivo, new JScrollPane(areaObjEsp), new JScrollPane(areaHab)});

        JPanel botones = new JPanel();
        botones.add(Util.boton("Guardar", this::guardar));
        botones.add(Util.boton("Cancelar", this::dispose));

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        setSize(560, 700);
        setLocationRelativeTo(owner);
    }

    public boolean isGuardado() { return guardado; }

    private LocalDate fecha(String t) throws DatosInvalidosException {
        t = t.trim();
        if (t.isEmpty()) return null;
        try {
            return LocalDate.parse(t);
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("Fecha inválida. Use el formato AAAA-MM-DD.");
        }
    }

    private void guardar() {
        boolean ok = Util.ejecutar(this, () -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) throw new DatosInvalidosException("El nombre es obligatorio.");
            if (txtObjetivo.getText().trim().isEmpty()) throw new DatosInvalidosException("El objetivo principal es obligatorio.");
            Categoria cat = (Categoria) cmbCat.getSelectedItem();
            if (cat == null) throw new DatosInvalidosException("Debe seleccionar una categoría.");
            LocalDate ini = fecha(txtInicio.getText());
            LocalDate fin = fecha(txtFin.getText());
            if (ini != null && fin != null && fin.isBefore(ini))
                throw new DatosInvalidosException("La fecha de finalización no puede ser anterior al inicio.");

            boolean nuevo = existente == null;
            Proyecto p = existente;
            if (nuevo) {
                boolean lab = cmbTipo.getSelectedIndex() == 1;
                p = lab ? new Laboratorio(sis.nuevoId(), nombre, sis.getUsuarioActual())
                        : new Proyecto(sis.nuevoId(), nombre, sis.getUsuarioActual());
            }
            p.setNombre(nombre);
            p.setDescripcion(areaDesc.getText().trim());
            p.setCategoria(cat);
            p.setDificultad((Dificultad) cmbDif.getSelectedItem());
            p.cambiarPrioridad((Prioridad) cmbPri.getSelectedItem());
            p.cambiarEstado((Estado) cmbEst.getSelectedItem());
            p.setFechaInicio(ini);
            p.setFechaFinalizacion(fin);
            p.setObjetivoPrincipal(txtObjetivo.getText().trim());
            List<String> objs = Util.lineas(areaObjEsp.getText());
            List<String> habs = Util.lineas(areaHab.getText());
            p.setObjetivosEspecificos(objs);
            p.setHabilidadesAprendidas(habs);

            if (nuevo) sis.agregarProyecto(p);
            else sis.modificarProyecto(p);
        });
        if (ok) {
            guardado = true;
            Util.aviso(this, existente == null ? "Se creó correctamente." : "Los cambios se guardaron correctamente.");
            dispose();
        }
    }
}
