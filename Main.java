package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.io.*;

public class Main {

    public static void main(String[] args) throws InterruptedException{
        //Scanner scan = new Scanner(System.in);
        //System.out.print("Use Defaults? ");
        int size = 5;
        int num = 5;
        int mines = 8;
        int dCount = 8;
        int iCount = 8;
        int eCount = 8;
        Main m = new Main();
        GUI gui = null;
        /*if (scan.nextLine().substring(0, 1).toUpperCase().equals("N")) {
            System.out.print("Input Board Size: ");
            size = scan.nextInt();
            System.out.print("Number of Boards: ");
            num = scan.nextInt();
            System.out.print("How many mines? ");
            mines = scan.nextInt();
            System.out.print("Number of Collapsing Measurements: ");
            dCount = scan.nextInt();
            System.out.print("Number of Interaction Free Measurements: ");
            iCount = scan.nextInt();
            System.out.print("Number of Entropy measurements: ");
            eCount = scan.nextInt();
        }*/

        while (true) {
            try {
                gui = new GUI(size, num, mines, dCount, iCount, eCount);
            }
            catch(IOException e){
                m.notify();
            }
            ButtonSelector buttons = new ButtonSelector(dCount, iCount, eCount, m);
            JFrame window = new JFrame();
            window.setLayout(new BorderLayout());
            window.setTitle("Quantum Minesweeper");
            window.setSize(800, 800);
            window.add(gui, BorderLayout.CENTER, 0);
            window.add(buttons, BorderLayout.LINE_START, 1);
            JLabel winLabel = new JLabel("Quantum Minesweeper", SwingConstants.CENTER);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 64));
            window.add(winLabel, BorderLayout.NORTH, 2);
            ButtonFlagger flagger = new ButtonFlagger(size, num, mines);
            window.add(flagger, BorderLayout.SOUTH, 3);
            gui.finishInitialize();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
            synchronized (m) {
                m.wait();
            }
            gui.stopMusic();
            window.dispose();
        }
    }
}