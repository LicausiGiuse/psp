package UT3.Hilos;

public class ParandoHilos extends Thread {
    public void run(){
        try {
            while (!isInterrupted()){
                System.out.println("en el hilo");
                Thread.sleep(10);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("FIN DEL HILO");
    }
    public static void main(String[] args){
        ParandoHilos  p = new ParandoHilos();
        p.start();
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } p.interrupt();
    }
}
