package EjerciciosJava;

import java.io.File;

public class Lanzador2 {
    public void lanzarSumador2(Integer n1, Integer n2,  String fichResultado){
        String clase = "EjerciciosJava.Sumador";
        ProcessBuilder pb;
        try {
           pb = new ProcessBuilder("java",clase,n1.toString(),n2.toString());
           pb.directory(new File("./out/production/UT2"));
           pb.redirectError(new File("errores.txt"));
           pb.redirectOutput(new File(fichResultado));
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public static void main(String[] args){
        Lanzador2 l2 = new Lanzador2();
        l2.lanzarSumador2(1,5,"result1.txt");
        l2.lanzarSumador2(6,10,"result2.txt");
        System.out.println("OK");
    }
}
