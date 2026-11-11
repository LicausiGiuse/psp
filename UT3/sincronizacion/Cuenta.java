package UT3.sincronizacion;

public class Cuenta {
    private int saldo;
    Cuenta(int saldo){this.saldo=saldo;}
    private int getSaldo(){return saldo;}
    private synchronized void restar(int cant){saldo=saldo-cant;}
    public synchronized void retirarDinero(int cant, String nombre){
    //el resto de líneas igual
        if(getSaldo()>= cant){
            System.out.println("SE VA A RETIRAR.\t\tSALDO ACTUAL: ("+getSaldo()+")");
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){}
            restar(cant);
            System.out.println(nombre+" retira => "+ cant+"\t\tSALDO ACTUAL: ("+getSaldo()+")");
        }else{
            System.out.println(nombre+" no puede retirar,\t\tSALDO ACTUAL: ("+getSaldo()+")");
        }
        if(getSaldo()<0){
            System.err.println("SALDO NEGATIVO!!!=>"+getSaldo());
        }
    }
}
