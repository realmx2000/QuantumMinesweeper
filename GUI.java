package com.company; /**
 * Created by realmx2000 on 2/14/17.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import sun.audio.*;

public class GUI extends JPanel implements ActionListener {
    final private int DETERMINISTIC = 0;
    final private int IFM = 1;
    final private int ENTROPY = 2;
    private int dCount;
    private int iCount;
    private int eCount;
    private JButton[][] buttons;
    private int size;
    private int currState = DETERMINISTIC;
    private ImageIcon image;
    private ArrayList<int[]> previous = new ArrayList<int[]>();
    private ArrayList<int[]> prevEntropy = new ArrayList<int[]>();
    private JLabel winLabel;
    private Board board;
    private ButtonSelector options;
    private ButtonFlagger flagger;
    private AudioStream audioStream;
    private InputStream inputStream;
    private int WINSTATE = 0;

    public GUI(int boardSize, int num, int mines, int dC, int iC, int eC) throws IOException {
        size = boardSize;
        buttons = new JButton[boardSize][boardSize];
        board = new Board(boardSize, num, mines);
        dCount = dC;
        iCount = iC;
        eCount = eC;
        setLayout(new GridLayout(boardSize + 1, boardSize));
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(Color.DARK_GRAY);
                buttons[i][j].setOpaque(true);
                buttons[i][j].addActionListener(this);
                add(buttons[i][j]);
            }
        inputStream = GUI.class.getResourceAsStream("ktane.wav");
        audioStream = new AudioStream(inputStream);
        AudioPlayer.player.start(audioStream);
    }

    public void finishInitialize() {
        winLabel = (JLabel) getParent().getComponent(2);
        options = (ButtonSelector) getParent().getComponent(1);
        board.printBoards();
        flagger = (ButtonFlagger) getParent().getComponent(3);
    }

    public void actionPerformed(ActionEvent e) {
        int[] coordinate = indexOf(e.getSource());
        if (currState == DETERMINISTIC && dCount > 0) {
            dCount--;
            options.setdText(dCount);
            if (!board.deterministicMeasure(coordinate))
                gameOver(coordinate);
            else {
                board.removeBoard(coordinate, true);
                previous.add(coordinate);
                buttons[coordinate[0]][coordinate[1]].setEnabled(false);
                updateBoard();
            }
        }
        if (currState == IFM && iCount > 0) {
            iCount--;
            options.setiText(iCount);
            int state = board.iFMeasure(coordinate);
            if (state == 1)
                gameOver(coordinate);
            else if (state == 0) {
                buttons[coordinate[0]][coordinate[1]].setText("?");
            } else {
                board.removeBoard(coordinate, false);
                image = new ImageIcon(this.getClass().getResource("bomb.png"));
                Image img = image.getImage();
                Image img2 = img.getScaledInstance(buttons[0][0].getWidth(), buttons[0][0].getHeight(), Image.SCALE_SMOOTH);
                buttons[coordinate[0]][coordinate[1]].setIcon(new ImageIcon(img2));
                updateBoard();
            }
        }
        if (currState == ENTROPY && eCount > 0) {
            eCount--;
            options.seteText(eCount);
            double entropy = board.eMeasure(coordinate);
            buttons[coordinate[0]][coordinate[1]].setText(Double.toString(entropy));
            prevEntropy.add(coordinate);
        }
        flagger.updateLabel(board.getSize());
        board.printBoards();
    }

    public void setState(int newState) {
        currState = newState;
    }

    private void updateBoard() {
        double toDisplay;
        for (int[] p : prevEntropy) {
            toDisplay = board.eMeasure(p);
            buttons[p[0]][p[1]].setText(Double.toString(toDisplay));
        }
        for (int[] c : previous) {
            toDisplay = board.countMines(c);
            buttons[c[0]][c[1]].setText(Double.toString(toDisplay));
        }
    }

    private int[] indexOf(Object button) {
        int[] coordinate = new int[2];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (buttons[i][j].equals(button)) {
                    coordinate[0] = i;
                    coordinate[1] = j;
                    return coordinate;
                }
        return coordinate; // Should never be reached
    }

    private void gameOver(int[] coordinate) {
        if(WINSTATE == 0) {
            for (JButton[] buttonRow : buttons)
                for (JButton button : buttonRow)
                    button.setEnabled(false);
            image = new ImageIcon(this.getClass().getResource("explosion.jpg"));
            Image img = image.getImage();
            Image img2 = img.getScaledInstance(buttons[0][0].getWidth(), buttons[0][0].getHeight(), Image.SCALE_SMOOTH);
            buttons[coordinate[0]][coordinate[1]].setIcon(new ImageIcon(img2));
            winLabel.setForeground(Color.red);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 50));
            winLabel.setText("You Lose.");
            WINSTATE = 1;
        }
    }

    public void gameOver() {
        if(WINSTATE == 0) {
            for (JButton[] buttonRow : buttons)
                for (JButton button : buttonRow)
                    button.setEnabled(false);
            winLabel.setForeground(Color.red);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 50));
            winLabel.setText("State Decoherent! You Lose.");
            WINSTATE = 1;
        }
    }

    public int check(boolean[][] guess) {
        int remaining = board.check(guess);
        if (remaining == 0)
            win();
        return remaining;
    }

    public void stopMusic(){
        AudioPlayer.player.stop(audioStream);
    }

    private void win() {
        if(WINSTATE == 0) {
            for (JButton[] buttonRow : buttons)
                for (JButton button : buttonRow)
                    button.setEnabled(false);
            winLabel.setForeground(Color.cyan);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 50));
            winLabel.setText("YOU WIN!");
            WINSTATE = 2;
        }
    }
}