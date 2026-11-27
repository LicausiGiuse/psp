package ejemplos;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClienteSocketEjemplo {
    public static void main(String[] args) {
        int puerto = 6000;
        String host="192.168.113.41";
        try {
            //ABRIR SOCKET
            Socket cliente = new Socket(host, puerto);

            InetAddress inet = cliente.getInetAddress();
            System.out.println("Puerto local: " + cliente.getLocalPort());
            System.out.println("Puerto remoto: " + cliente.getPort());
            System.out.println("Host remoto: " + inet.getHostName());

            cliente.close();//CERRAR SOCKET
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
