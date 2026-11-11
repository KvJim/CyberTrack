package vista;

import servicio.Sistema;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);

    public VentanaLogin() {
        super("CyberTrack - Inicio de sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("CyberTrack", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JPanel form = Util.formulario(new String[]{"Usuario", "Contraseña"}, new JComponent[]{txtUsuario, txtPass});
        JButton btnIngresar = new JButton("Iniciar sesión");
        JButton btnRegistro = new JButton("Registrarse");
        JPanel botones = new JPanel();
        botones.add(btnIngresar);
        botones.add(btnRegistro);

        setLayout(new BorderLayout(10, 10));
        add(titulo, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> ingresar());
        btnRegistro.addActionListener(e -> new VentanaRegistro(this).setVisible(true));
        getRootPane().setDefaultButton(btnIngresar);

        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        boolean ok = Util.ejecutar(this, () ->
                Sistema.getInstance().iniciarSesion(txtUsuario.getText().trim(), new String(txtPass.getPassword())));
        if (ok) {
            new VentanaPrincipal().setVisible(true);
            dispose();
        }
    }
}