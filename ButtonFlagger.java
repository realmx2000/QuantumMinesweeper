package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Zhaoyu Lou on 3/8/17.
 */

/*
The ButtonFlagger is the solution board that is displayed below the main board. The
player flags tiles on which s/he believes there are mines using this board.
 */
public class ButtonFlagger extends JPanel implements ActionListener {

    //Internal state variables
    private JButton[][] buttons;
    private boolean[][] board;
    private int boardSize;
    private GUI trueBoard;
    private JLabel numRemaining;

    /*
    This constructor initializes the solution board and all the informational
    labels, then adds them to the GUI.
     */
    public ButtonFlagger(int size, int numBoards, int mines) {
        //Initialize the solution board
        board = new boolean[size][size];
        boardSize = size;
        buttons = new JButton[boardSize][boardSize];

        //Initialize the informational labels
        numRemaining = new JLabel("Boards: " + Integer.toString(numBoards));
        JLabel numMines = new JLabel("Mines: " + Integer.toString(mines));
        numRemaining.setFont(new Font("Serif", Font.PLAIN, 20));

        setLayout(new GridLayout(boardSize, boardSize + 1));

        //Add labels and the first 2 rows of buttons; I had to do it in this weird
        //order to format them properly
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

        //Add the remaining buttons
        for (int i = 2; i < boardSize; i++) {
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
    }

    /*
    Handles when the user selects a tile. Most of the logic is handled in GUI,
    this just handles visual updates.
     */
    public void actionPerformed(ActionEvent e) {
        //Get access to the GUI, since the logic for checking the board
        //state is there.
        trueBoard = (GUI) getParent().getComponent(0);

        //Get coordinate of the tile selected
        int[] coordinate = indexOf(e.getSource());

        //If the tile is already flagged, change color to gray and unflag it
        if (board[coordinate[0]][coordinate[1]]) {
            board[coordinate[0]][coordinate[1]] = false;
            buttons[coordinate[0]][coordinate[1]].setBackground(Color.DARK_GRAY);
        }

        //Otherwise, change color to red and flag it
        else {
            board[coordinate[0]][coordinate[1]] = true;
            buttons[coordinate[0]][coordinate[1]].setBackground(Color.red);
        }

        //Check how many mines are remaining and update the informational label
        int remaining = trueBoard.check(board);
        numRemaining.setText("Boards:" + Integer.toString(remaining));
    }

    //Update the informational label with the number of boards in play.
    public void updateLabel(int remaining) {
        numRemaining.setText("Boards:" + Integer.toString(remaining));
    }

    //Gets the position of a button in the solution board.
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