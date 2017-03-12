package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by realmx2000 on 3/8/17.
 */
public class ButtonFlagger extends JPanel implements ActionListener {
    JButton[][] buttons;
    boolean[][] board;
    int boardSize;
    GUI trueBoard;
    JLabel numRemaining;

    public ButtonFlagger(int size, int numBoards, int mines) {
        board = new boolean[size][size];
        boardSize = size;
        buttons = new JButton[boardSize][boardSize];
        numRemaining = new JLabel("Boards: " + Integer.toString(numBoards));
        JLabel numMines = new JLabel("Mines: " + Integer.toString(mines));
        numRemaining.setFont(new Font("Serif", Font.PLAIN, 20));
        setLayout(new GridLayout(boardSize, boardSize + 1));
        add(numRemaining);
        for (int i = 0; i < boardSize; i++) {
            buttons[0][i] = new JButton();
            buttons[0][i].setBackground(Color.DARK_GRAY);
            buttons[0][i].setOpaque(true);
            buttons[0][i].addActionListener(this);
            add(buttons[0][i]);
        }
        add(numMines);
        for (int i = 0; i < boardSize; i++) {
            buttons[1][i] = new JButton();
            buttons[1][i].setBackground(Color.DARK_GRAY);
            buttons[1][i].setOpaque(true);
            buttons[1][i].addActionListener(this);
            add(buttons[1][i]);
        }
        for (int i = 2; i < boardSize; i++)
            for (int j = 0; j < boardSize + 1; j++) {
                if (j == 0)
                    add(new JLabel());
                else {
                    buttons[i][j - 1] = new JButton();
                    buttons[i][j - 1].setBackground(Color.DARK_GRAY);
                    buttons[i][j - 1].setOpaque(true);
                    buttons[i][j - 1].addActionListener(this);
                    add(buttons[i][j - 1]);
                }
            }
    }

    public void actionPerformed(ActionEvent e) {
        trueBoard = (GUI) getParent().getComponent(0);
        int[] coordinate = indexOf(e.getSource());
        if (board[coordinate[0]][coordinate[1]]) {
            board[coordinate[0]][coordinate[1]] = false;
            buttons[coordinate[0]][coordinate[1]].setBackground(Color.DARK_GRAY);
        } else {
            board[coordinate[0]][coordinate[1]] = true;
            buttons[coordinate[0]][coordinate[1]].setBackground(Color.red);
        }

        int remaining = trueBoard.check(board);
        numRemaining.setText("Boards:" + Integer.toString(remaining));
    }

    public void updateLabel(int remaining) {
        numRemaining.setText("Boards:" + Integer.toString(remaining));
    }

    private int[] indexOf(Object button) {
        int[] coordinate = new int[2];
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                if (buttons[i][j].equals(button)) {
                    coordinate[0] = i;
                    coordinate[1] = j;
                    return coordinate;
                }
        return coordinate; // Should never be reached
    }
}
