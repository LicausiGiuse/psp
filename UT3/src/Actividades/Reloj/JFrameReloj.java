package Actividades.Reloj;


import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JFrameRelojForm extends JFrame implements Runnable {

    private JLabel labelTime;
    private JPanel panel1;
    private Thread h1;

    public JFrameRelojForm() {
        setContentPane(panel1); // usa el panel generado como contenido
        setTitle("Reloj Digital");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);
        h1 = new Thread(this);
        h1.start();
    }

    public void run() {
        Thread currentThread = Thread.currentThread();
        while (currentThread == h1) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            String horaActual = sdf.format(calendar.getTime());
            labelTime.setText(horaActual);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameRelojForm().setVisible(true);
            }
        });
    }
}