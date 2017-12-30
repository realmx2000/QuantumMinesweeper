package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Zhaoyu Lou on 2/15/17.
 */

/*
The ButtonSelector class provides the implementation for visual aspects
and user interactions with the menu for selecting measurement types and
resetting the board.
 */
public class ButtonSelector extends JPanel implements ActionListener {

    //These 3 constants represent which measurement type is currently selected
    final private int DETERMINISTIC = 0;
    final private int IFM = 1;
    final private int ENTROPY = 2;

    //GUI elements, made global because passing them all around got annoying
    private JRadioButton measure1 = new JRadioButton("Collapsing");
    private JRadioButton measure2 = new JRadioButton("Interaction Free");
    private JRadioButton measure3 = new JRadioButton("Entropy");

    private ButtonGroup group = new ButtonGroup();
    private GUI gui;

    private JLabel dLabel = new JLabel();
    private JLabel iLabel = new JLabel();
    private JLabel eLabel = new JLabel();

    private Timer timer;
    private int totalTime = 248;
    private JLabel timeLabel = new JLabel();

    private JButton reset = new JButton("Restart");
    final private Main x;

    /*
    This constructor adds the GUI elements to the panel, starts the game timer,
    and adds the panel into the GUI.
     */
    public ButtonSelector(int dCount, int iCount, int eCount, Main m) {
        x = m;
        setLayout(new GridLayout(12, 1));

        //Configure and add the buttons to the ButtonGroup
        measure1.addActionListener(this);
        measure2.addActionListener(this);
        measure3.addActionListener(this);
        reset.addActionListener(this);

        group.add(measure1);
        group.add(measure2);
        group.add(measure3);
        measure1.setSelected(true);

        //Configure the informational labels
        timeLabel.setText("Decoherence in: " + Integer.toString(totalTime));

        dLabel.setText("Remaining: " + Integer.toString(dCount));
        iLabel.setText("Remaining: " + Integer.toString(iCount));
        eLabel.setText("Remaining: " + Integer.toString(eCount));

        //Instantiate the game timer and start it
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui = (GUI) getParent().getComponent(0);
                totalTime--;
                if (totalTime == 0) {
                    ((Timer) e.getSource()).stop();
                    timeLabel.setText("State Decoherent");
                    gui.gameOver();
                } else {
                    timeLabel.setText("Decoherence in: " + Integer.toString(totalTime));
                }
            }
        });

        timeLabel.setForeground(Color.red);
        timer.start();

        //Add all the elements to the GUI
        add(new JPanel());
        add(timeLabel);
        add(measure1);
        add(dLabel);

        add(new JPanel());
        add(measure2);
        add(iLabel);

        add(new JPanel());
        add(measure3);
        add(eLabel);
        add(reset);

        add(new JPanel());
    }

    /*
    This method handles when the user selects one of the buttons, either to
    change the measurement type or to reset the game.
     */
    public void actionPerformed(ActionEvent e) {
        //Get access to the GUI, since it manages the measurements themselves
        //so it needs to know the measurement selected
        gui = (GUI) getParent().getComponent(0);

        //If changing the measurement type, change the GUI state accordingly
        if (e.getSource() == measure1)
            gui.setState(DETERMINISTIC);
        else if (e.getSource() == measure2)
            gui.setState(IFM);
        else if (e.getSource() == measure3)
            gui.setState(ENTROPY);

            //If resetting the game, signal the main class to trigger a reset
        else if (e.getSource() == reset) {
            synchronized (x) {
                x.notify();
            }
        }
    }

    //Stops the timer for when the game ends.
    public void stopTimer() { timer.stop(); }

    //Change the label showing the number of deterministic measurements left.
    public void setdText(int newNumber) {
        dLabel.setText("Remaining: " + Integer.toString(newNumber));
    }

    //Change the label showing the number of interaction free measurements left.
    public void setiText(int newNumber) {
        iLabel.setText("Remaining: " + Integer.toString(newNumber));
    }

    //Change the label showing the number of entropy measurements left.
    public void seteText(int newNumber) {
        eLabel.setText("Remaining: " + Integer.toString(newNumber));
    }
}