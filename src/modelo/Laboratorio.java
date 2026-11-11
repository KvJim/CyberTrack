package modelo;

public class Laboratorio extends Proyecto {
    private static final long serialVersionUID = 1L;

    private String resultado = "";

    public Laboratorio(int idProyecto, String nombre, Usuario propietario) {
        super(idProyecto, nombre, propietario);
    }

    public void registrarResultado(String resultado) { this.resultado = resultado; }
    public String getResultado() { return resultado; }

    @Override public String getTipo() { return "Laboratorio"; }

    @Override public String resumen() {
        return super.resumen() + "Resultado: " + (resultado.isEmpty() ? "-" : resultado) + "\n";
    }
}