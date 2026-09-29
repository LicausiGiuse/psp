package EjerciciosJava;

import java.io.*;

public class EjemploJava {
    public static void main(String[] args) throws IOException {

        //creamos objeto File al directorio donde está EjemploComando
        File directorio = new File("./out/production/UT2");

        //El proceso a ejecutar es EjemploComando
        ProcessBuilder pb =
                new ProcessBuilder("java", "EjerciciosJava.EjemploComando");

        //inheritIO El hijo recoge usa el Input y Output del padre como propio
        pb.inheritIO();
        //se establece el directorio donde se encuentra el ejecutable
        pb.directory(directorio);
        System.out.printf("Directorio de trabajo %s%n",pb.directory());
        //se ejecuta el proceso
        Process p = pb.start();
    }

}// EjemploJava
