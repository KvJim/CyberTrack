package vista;

import excepciones.DatosInvalidosException;
import servicio.Sistema;

import javax.swing.*;
import java.awt.*;

public class VentanaRegistro extends JDialog {
    private final JTextField txtNombre = new JTextField(15);
    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final JPasswordField txtConfirmar = new JPasswordField(15);

    public VentanaRegistro(Frame owner) {
        super(owner, "Registro de usuario", true);

        JPanel form = Util.formulario(
                new String[]{"Nombre", "Usuario", "Contraseña", "Confirmar contraseña"},
                new JComponent[]{txtNombre, txtUsuario, txtPass, txtConfirmar});
        JButton btnRegistrar = new JButton("Registrarse");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel botones = new JPanel();
        botones.add(btnRegistrar);
        botones.add(btnCancelar);

        setLayout(new BorderLayout(10, 10));
        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> {
            boolean ok = Util.ejecutar(this, () -> {
                String p = new String(txtPass.getPassword());
                if (!p.equals(new String(txtConfirmar.getPassword())))
                    throw new DatosInvalidosException("Las contraseñas no coinciden.");
                Sistema.getInstance().registrar(txtNombre.getText(), txtUsuario.getText(), p);
            });
            if (ok) {
                Util.aviso(this, "El registro se realizó correctamente.");
                dispose();
            }
        });
        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }
}