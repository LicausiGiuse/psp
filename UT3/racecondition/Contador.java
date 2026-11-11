package UT3.racecondition;

public class Contador {
    private int valor=0;
    Contador(int valor){this.valor=valor;}
    public synchronized void incrementa(){valor= valor+1;}
    public synchronized void decrementa(){valor= valor-1;}
    public int getValor(){return valor;}
}
