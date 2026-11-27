package Sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SorcketServidor {
    public static void main(String[] args){

        int puerto = 6000;
        try {
            ServerSocket servidor = new ServerSocket(puerto);
            Socket clienteConectado = null;
            System.out.println("Esperando al cliente...");
            clienteConectado = servidor.accept();

            InputStream entrada = null;
            entrada = clienteConectado.getInputStream();
            DataInputStream flujoEntrada = new DataInputStream(entrada);
            System.out.println("Recibiendo mensaje de el cliente... \n\t");
            // CREO FLUJO DE SALIDA AL CLIENTE
            OutputStream salida = null;
            salida = clienteConectado.getOutputStream();
            DataOutputStream flujoSalida = new DataOutputStream(salida);

            // ENVIO UN SALUDO AL CLIENTE
            flujoSalida.writeUTF("Saludos al cliente del servidor");

            // CERRAR STREAMS Y SOCKETS
            entrada.close();
            flujoEntrada.close();
            salida.close();
            flujoSalida.close();
            clienteConectado.close();
            servidor.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
