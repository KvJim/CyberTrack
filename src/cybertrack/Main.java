package cybertrack;

import servicio.Autoguardado;
import servicio.Sistema;
import vista.VentanaLogin;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Sistema sistema = Sistema.getInstance();
        new Autoguardado(sistema).start();
        Runtime.getRuntime().addShutdownHook(new Thread(sistema::guardar));
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}