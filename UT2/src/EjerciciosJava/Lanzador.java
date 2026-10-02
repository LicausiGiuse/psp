package EjerciciosJava;

public class Lanzador {

    public void lanzarSumador(int n1, int n2) {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    "EjerciciosJava.Sumador",
                    String.valueOf(n1),
                    String.valueOf(n2)
            );

            pb.inheritIO();
            pb.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}