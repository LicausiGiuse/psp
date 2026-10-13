package Actividades;

public class ThreadEjemplo extends Thread {

    public ThreadEjemplo(String str) {
        super(str);
    }
    public void run(){
        for (int i = 0; i < 5; i++) {
            System.out.println(i+" "+getName());
            System.out.println("Termina thread " + getName());
        }
    }

    public static void main(String[] args){
        new ThreadEjemplo("pepe").start();
        new ThreadEjemplo("juan").start();
        System.out.println("Termina el thread main");
    }
}
