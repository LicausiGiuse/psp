package EjerciciosJava;

public class Lanzador {

    public void lanzarSumador(Integer n1, Integer n2) {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    "EjerciciosJava.Sumador",
                    n1.toString(),
                    n2.toString()
            );

            pb.inheritIO();
            pb.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Lanzador lanzador = new Lanzador();

        lanzador.lanzarSumador(1, 10);
        lanzador.lanzarSumador(11, 20);
    }
}