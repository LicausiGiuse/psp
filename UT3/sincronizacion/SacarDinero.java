package UT3.sincronizacion;

public class SacarDinero extends Thread{

    private Cuenta cuenta;
    public SacarDinero(String nombre, Cuenta cuenta){
        super(nombre);
        this.cuenta = cuenta;
    }
    @Override
    public void run(){
        for(int i=0; i<3;i++){
            cuenta.retirarDinero(10, getName());
        }
    }
}
