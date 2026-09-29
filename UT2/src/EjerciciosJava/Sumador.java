package EjerciciosJava;

import java.util.Scanner;

public class Sumador {



         public static int sumar(int n1, int n2){
             return n1 + n2;
         }
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args){

             System.out.println("numero 1: ");
             int n1 = sc.nextInt();
             System.out.println("numero 2: ");
             int n2 = sc.nextInt();
        System.out.println(sumar(n1,n2));

    }
}
