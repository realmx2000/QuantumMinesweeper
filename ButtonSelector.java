package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by realmx2000 on 2/15/17.
 */
public class ButtonSelector extends JPanel implements ActionListener {
    final private int DETERMINISTIC = 0;
    final private int IFM = 1;
    final private int ENTROPY = 2;
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
    Main x;

    public ButtonSelector(int dCount, int iCount, int eCount, Main m) {
        x = m;
        setLayout(new GridLayout(12, 1));
        measure1.addActionListener(this);
        measure2.addActionListener(this);
        measure3.addActionListener(this);
        reset.addActionListener(this);
        group.add(measure1);
        group.add(measure2);
        group.add(measure3);
        measure1.setSelected(true);
        timeLabel.setText("Decoherence in: " + Integer.toString(totalTime));
        dLabel.setText("Remaining: " + Integer.toString(dCount));
        iLabel.setText("Remaining: " + Integer.toString(iCount));
        eLabel.setText("Remaining: " + Integer.toString(eCount));
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui = (GUI) getParent().getComponent(0);
                totalTime--;
                if (totalTime == 0) {
                    ((Timer) e.getSource()).stop();
                    timeLabel.setText("Decoherence in: " + Integer.toString(totalTime));
                    gui.gameOver();
                } else {
                    timeLabel.setText("Decoherence in: " + Integer.toString(totalTime));
                }
            }
        });
        timeLabel.setForeground(Color.red);
        timer.start();
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

    public void actionPerformed(ActionEvent e) {
        gui = (GUI) getParent().getComponent(0);
        if (e.getSource() == measure1)
            gui.setState(DETERMINISTIC);
        else if (e.getSource() == measure2)
            gui.setState(IFM);
        else if (e.getSource() == measure3)
            gui.setState(ENTROPY);
        else if (e.getSource() == reset) {
            synchronized (x) {
                x.notify();
            }
        }
    }

    public void setdText(int newNumber) {
        dLabel.setText("Remaining: " + Integer.toString(newNumber));
    }

    public void setiText(int newNumber) {
        iLabel.setText("Remaining: " + Integer.toString(newNumber));
    }

    public void seteText(int newNumber) {
        eLabel.setText("Remaining: " + Integer.toString(newNumber));
    }
}