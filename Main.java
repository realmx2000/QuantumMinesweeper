package com.company;
/**
 * Created by Zhaoyu Lou on 2/14/17.
 */

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        //Query user for game settings. Default recommended settings are already set.
        Scanner scan = new Scanner(System.in);
        System.out.print("Use Defaults? ");
        int size = 5;
        int num = 5;
        int mines = 8;
        int dCount = 8;
        int iCount = 8;
        int eCount = 8;

        Main m = new Main();
        GUI gui = null;

        //If user wants custom settings, ask for them and set them.
        if (scan.nextLine().substring(0, 1).toUpperCase().equals("N")) {
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
        }

        //Create game board and maintain game state. Create new game boards
        //when old ones are disposed of.
        while (true) {

            //Create GUI; if it fails for some reason trigger a reinitialization
            try {
                gui = new GUI(size, num, mines, dCount, iCount, eCount);
            } catch (IOException e) {
                m.notify();
            }

            //Create new frame for the GUI
            JFrame window = new JFrame();
            window.setLayout(new BorderLayout());

            //Create title
            window.setTitle("Quantum Minesweeper");
            window.setSize(800, 800);

            //Add GUI and measurement selection buttons
            window.add(gui, BorderLayout.CENTER, 0);
            ButtonSelector buttons = new ButtonSelector(dCount, iCount, eCount, m);
            window.add(buttons, BorderLayout.LINE_START, 1);

            //Create label
            JLabel winLabel = new JLabel("Quantum Minesweeper", SwingConstants.CENTER);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 64));
            window.add(winLabel, BorderLayout.NORTH, 2);

            //Create solution board for flagging mines
            ButtonFlagger flagger = new ButtonFlagger(size, num, mines);
            window.add(flagger, BorderLayout.SOUTH, 3);

            //Finish initialization and display GUI
            gui.finishInitialize();
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            window.setVisible(true);

            //Await reset signal before disposing of and reinitializing GUI
            synchronized (m) {
                m.wait();
            }

            //Dispose of GUI and loop back to reinitialize
            gui.stopMusic();
            window.dispose();
        }
    }
}