package UT3.racecondition;

public class Ejecutar {
    public static void main(String[] args) {
        Contador c = new Contador(100);
        UT3.racecondition.HiloA a = new UT3.racecondition.HiloA("HiloA", c);
        UT3.racecondition.HiloB b = new UT3.racecondition.HiloB("HiloB", c);
        a.start();
        b.start();
    }
}
