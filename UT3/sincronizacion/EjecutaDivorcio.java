package UT3.sincronizacion;

public class EjecutaDivorcio {
    public static void main(String[] args) {
        UT3.sincronizacion.Cuenta c = new UT3.sincronizacion.Cuenta(40);
        UT3.sincronizacion.SacarDinero ana = new UT3.sincronizacion.SacarDinero("Ana", c);
        UT3.sincronizacion.SacarDinero juan = new UT3.sincronizacion.SacarDinero("Juán", c);
        ana.start();
        juan.start();
    }
}
