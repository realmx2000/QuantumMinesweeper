package com.company;
/**
 * Created by Zhaoyu Lou on 2/14/17.
 */

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/*
The GUI class provides the implementation for the GUI and logic for handling any user
input concerning that main board. It also has logic for managing all the other visual
components (even the ones declared elsewhere).
 */

public class GUI extends JPanel implements ActionListener {

    //These 3 ints label the different measurement states of the game, each
    //corresponding to a different type of measurement.
    final private int DETERMINISTIC = 0;
    final private int IFM = 1;
    final private int ENTROPY = 2;

    //Internal state variables; I would have preferred to have fewer global variables
    //but this was better than passing around all the state variables between methods.
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

    /*
    This constructor creates the main board and begins the audio stream.
    dC, iC, and eC are the number of deterministic, interaction free, and
    entropy measurements, respectively. boardSize and mines are the board
    size and the number of mines, and num is the number of boards in play.
    */
    public GUI(int boardSize, int num, int mines, int dC, int iC, int eC) throws IOException {
        //Set game settings from the inputs.
        size = boardSize;
        buttons = new JButton[boardSize][boardSize];
        board = new Board(boardSize, num, mines);
        dCount = dC;
        iCount = iC;
        eCount = eC;

        //Create the buttons comprising the main board
        setLayout(new GridLayout(boardSize + 1, boardSize));
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(Color.DARK_GRAY);
                buttons[i][j].setOpaque(true);
                buttons[i][j].addActionListener(this);
                add(buttons[i][j]);
            }

        //Initialize audio player and start background music
        inputStream = GUI.class.getResourceAsStream("ktane.wav");
        audioStream = new AudioStream(inputStream);
        AudioPlayer.player.start(audioStream);
    }

    //This function establishes connections to the other GUI components
    //which are not part of the main board.
    public void finishInitialize() {
        winLabel = (JLabel) getParent().getComponent(2);
        options = (ButtonSelector) getParent().getComponent(1);
        flagger = (ButtonFlagger) getParent().getComponent(3);

        //board.printBoards();    For debugging only
    }

    /*
    This function handles the user interactions with the main board
    by determining the measurement type currently selected and calling
    different methods to handle the different measurements.
     */
    public void actionPerformed(ActionEvent e) {
        //Get which button was pressed
        int[] coordinate = indexOf(e.getSource());

        //Get which measurement was made, and check that there are still
        //measurements of that type remaining
        if (currState == DETERMINISTIC && dCount > 0) {
            dMeasure(coordinate);
        }
        else if (currState == IFM && iCount > 0) {
            iFMeasure(coordinate);
        }
        else if (currState == ENTROPY && eCount > 0) {
            eMeasure(coordinate);
        }

        //Update the display to reflect the number of boards remaining.
        flagger.updateLabel(board.getNumRemaining());

        //board.printBoards();    For debugging only
    }

    //Performs a deterministic measurement.
    private void dMeasure(int[] coordinate){
        //Decrement the number of measurements remaining
        dCount--;
        options.setdText(dCount);

        //Perform the measurement; if a mine explodes, game over
        if (!board.deterministicMeasure(coordinate))
            gameOver(coordinate);

        //If the mine doesn't explode, remove boards as necessary
        //disable the button, and update the board
        else {
            board.removeBoard(coordinate, true);
            previous.add(coordinate);
            buttons[coordinate[0]][coordinate[1]].setEnabled(false);
            updateBoard();
        }
    }

    //Performs an interaction free measurement.
    private void iFMeasure(int[] coordinate){
        //These integers indicate the outcome of the measurement
        final int DETECT = 2;
        final int DETONATE = 1;
        final int NOTHING = 0;

        //Decrement number of measurements remaining
        iCount--;
        options.setiText(iCount);

        //Perform the interaction free measurement
        int state = board.iFMeasure(coordinate);

        //If the mine exploded, game over
        if (state == DETONATE)
            gameOver(coordinate);

            //If no information was gained, display a '?'
        else if (state == NOTHING) {
            buttons[coordinate[0]][coordinate[1]].setText("?");
        }

        //If a bomb was detected, put an image of a bomb on the tile
        else {
            board.removeBoard(coordinate, false);
            image = new ImageIcon(this.getClass().getResource("bomb.png"));
            Image img = image.getImage();
            Image img2 = img.getScaledInstance(buttons[0][0].getWidth(), buttons[0][0].getHeight(), Image.SCALE_SMOOTH);
            buttons[coordinate[0]][coordinate[1]].setIcon(new ImageIcon(img2));
            updateBoard();
        }
    }

    //Performs an entropy measurement.
    private void eMeasure(int[] coordinate){
        //Decrement number of measurements remaining
        eCount--;
        options.seteText(eCount);

        //Perform the measurement and display the entropy
        double entropy = board.eMeasure(coordinate);
        buttons[coordinate[0]][coordinate[1]].setText(Double.toString(entropy));
        prevEntropy.add(coordinate);
    }

    //Modifies which measurement type is active, which is encapsulated in the
    //currState variable.
    public void setState(int newState) {
        currState = newState;
    }

    /*
    Refreshes the board by updating all the entropy and adjacent mine labels. This
    is needed because as boards drop out of play, the entropy and number of adjacent
    mines will change.
     */
    private void updateBoard() {
        double toDisplay;

        //Update entropy labels
        for (int[] p : prevEntropy) {
            toDisplay = board.eMeasure(p);
            buttons[p[0]][p[1]].setText(Double.toString(toDisplay));
        }

        //Update adjacent mine labels
        for (int[] c : previous) {
            toDisplay = board.countMines(c);
            buttons[c[0]][c[1]].setText(Double.toString(toDisplay));
        }
    }

    //Gets the position of the input button in the main board.
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

    /*
    Handles when the game is lost due to a mine exploding by displaying an explosion
    on the mine tile, disabling all mines, and changing labels to inform the player.
     */
    private void gameOver(int[] coordinate) {

        //Makes sure the game isn't already over
        if (WINSTATE == 0) {

            //Disable all buttons
            disableButtons();

            //Display an explosion on the tile with the mine
            image = new ImageIcon(this.getClass().getResource("explosion.jpg"));
            Image img = image.getImage();
            Image img2 = img.getScaledInstance(buttons[0][0].getWidth(), buttons[0][0].getHeight(), Image.SCALE_SMOOTH);
            buttons[coordinate[0]][coordinate[1]].setIcon(new ImageIcon(img2));

            //Set the big label on top to say 'You Lose'
            winLabel.setForeground(Color.red);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 50));
            winLabel.setText("You Lose.");

            //Stop music and timer
            stopMusic();
            options.stopTimer();

            //Set the winstate to the reflect that the game was lost
            WINSTATE = 1;
        }
    }

    /*
    Handles when the game is lost due to time running out by disabling all buttons
    and changing the label to inform the player.
     */
    public void gameOver() {
        //Make sure the game isn't already over
        if (WINSTATE == 0) {

            //Disable all buttons
            disableButtons();

            //Change the big label on top to show that the game is lost
            winLabel.setForeground(Color.red);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 50));
            winLabel.setText("State Decoherent! You Lose.");

            //Set the winstate to the reflect that the game was lost
            WINSTATE = 1;
        }
    }

    /*
    Handle when the player makes a guess as to where a mine is. If all
    mines have been located, the player wins; otherwise, return the
    number of remaining mines.
     */
    public int check(boolean[][] guess) {
        //Get how many mines are remaining
        int remaining = board.check(guess);

        //If all mines have been located, win
        if (remaining == 0)
            win();

        return remaining;
    }

    //Stop the music.
    public void stopMusic() {
        AudioPlayer.player.stop(audioStream);
    }

    /*
    Handles when the game is won by disabling all buttons and changing the
    label to inform the player.
     */
    private void win() {
        //Make sure the game isn't over already
        if (WINSTATE == 0) {

            //Disable all buttons
            disableButtons();

            //Change the big label on top to say 'You win!'
            winLabel.setForeground(Color.cyan);
            winLabel.setFont(new Font("Serif", Font.PLAIN, 50));
            winLabel.setText("YOU WIN!");

            //Stop the music and timer
            stopMusic();
            options.stopTimer();

            //Set the winstate to the reflect that the game was won
            WINSTATE = 2;
        }
    }

    //Disables all buttons for when the game is over.
    private void disableButtons() {
        for (JButton[] buttonRow : buttons)
            for (JButton button : buttonRow)
                button.setEnabled(false);
    }
}