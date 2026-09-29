package EjerciciosJava;

import java.io.*;

public class EjemploComando {
    public static void main(String[] args) throws IOException {
        // cmd /C <comando> ;cierra CMD cuando termina.

        Process p = new ProcessBuilder("/usr/bin/ls", "-la").start(); //LINUX

        try {
            InputStream is = p.getInputStream();

            // mostramos en pantalla caracter a caracter
            int c;
            while ((c = is.read()) != -1)
                System.out.print((char) c);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // COMPROBACION DE ERROR - 0 bien - 1 mal
        int exitVal;
        try {
            exitVal = p.waitFor();
            System.out.println("Valor de Salida: " + exitVal);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}// EjemploComando

