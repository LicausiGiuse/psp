package UT3.racecondition;


public class HiloA extends Thread{

    private Contador contador;

    public HiloA (String nombre, Contador contador){
        setName(nombre);
        this.contador = contador;
    }

    @Override
    public void run(){
        for(int j=0; j<300;j++){
            contador.incrementa();
            try {
                Thread.sleep(10);
            }catch (InterruptedException e){}
        }
        System.out.println(getName()+" contador vale: " + contador.getValor());
    }
}
