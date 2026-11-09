package UT3.src.Actividades.Cronometro;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Cronometro extends JFrame implements Runnable {
    private JPanel panel1;
    private JLabel labelTime;
    private JButton RUNButton;
    private Thread hilo1;
    private volatile boolean running = false;
    private long elapsedMillis = 0;

    public Cronometro() {
        setContentPane(panel1);
        setTitle("Cronometro");
        setSize(300,150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        RUNButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    running = true;
                    RUNButton.setText("Stop");
                    hilo1 = new Thread(Cronometro.this);
                    hilo1.start();
                } else {
                    running = false;
                    RUNButton.setText("Run");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Cronometro().setVisible(true);
        });
    }


    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (!running){
                break;
            }
            elapsedMillis += 10;
            String tiempo = formatearTiempo(elapsedMillis);
            SwingUtilities.invokeLater(() -> labelTime.setText(tiempo));
        }
    }
    private String formatearTiempo(long totalMillis) {
        long horas = totalMillis / 3_600_000;
        long minutos = (totalMillis / 60_000) % 60;
        long segundos = (totalMillis / 1_000) % 60;
        long milisegundos = totalMillis % 1_000;

        return String.format(
                "%02d:%02d:%02d.%03d",
                horas, minutos, segundos, milisegundos
        );
    }
}

