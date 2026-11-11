package servicio;

public class Autoguardado extends Thread {
    private final Sistema sistema;

    public Autoguardado(Sistema sistema) {
        this.sistema = sistema;
        setDaemon(true);
        setName("Autoguardado");
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Thread.sleep(30000);
                sistema.guardar();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}