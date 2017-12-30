package com.company;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Zhaoyu Lou on 2/16/17.
 */

/*
The board class provides the implementation of the main game board, particularly
the visual components and the measurement interactions. This class interacts with
GUI to create the full backend.
 */
public class Board {

    //Internal state variables
    private int NUM_BOARDS = 3;
    private int NUM_MINES = 15;
    private int size;

    private ArrayList<boolean[][]> board;
    private ArrayList<boolean[][]> toGuess;

    /*
    This constructor sets the state variables, initializes the main board, and
    adds it to the GUI.
     */
    public Board(int size, int num, int mines) {

        //Set state variables
        this.size = size;
        NUM_BOARDS = num;
        NUM_MINES = mines;
        board = new ArrayList<boolean[][]>(NUM_BOARDS);

        //Initialize all game boards in play and add them to the main board
        for (int i = 0; i < NUM_BOARDS; i++) {
            boolean[][] instance = new boolean[size][size];
            board.add(instance);
        }

        //Generate mines
        createMines();

        //Create a copy of all the mines. The toGuess copy is needed for the
        //rare case in which two boards remain in play at the end of the game.
        toGuess = board;
    }

    //This method randomly scatters a set number of mines across each board.
    private void createMines() {
        Random r = new Random();
        int temp;

        //Add the proper number of boards
        for (int i = 0; i < NUM_BOARDS; i++) {

            //Generate the specified number of unique mine indices
            boolean[] mines = new boolean[size * size];
            for (int j = 0; j < NUM_MINES; j++) {
                temp = r.nextInt(size * size);
                if (!mines[temp])
                    mines[temp] = true;
                else
                    j--;
            }

            //Add the mines into the main game board
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    board.get(i)[j][k] = mines[j * size + k];
                }
            }
        }
    }

    /*
    This implements the first half of the deterministic measurement,
    checking whether or not a mine explodes. The second half, counting the
    number of mines, is handled in GUI.
     */
    public boolean deterministicMeasure(int[] coordinate) {
        int total = 0;
        Random r = new Random();

        //Count number of mines present on this tile
        for (int i = 0; i < board.size(); i++)
            if (board.get(i)[coordinate[0]][coordinate[1]])
                total += 1;

        //Calculate probability of mine exploding, then produce an outcome accordingly.
        double threshold = (double) total / board.size();
        if (r.nextDouble() < threshold)
            return false;
        return true;
    }

    /*
    Implementation of the interaction free measurement. Checks if a mine explodes,
    is detected, or if nothing happens, and returns an integer corresponding to the
    result.
     */
    public int iFMeasure(int[] coordinate) {
        int total = 0;
        Random r = new Random();
        final int DETECT = 2;
        final int DETONATE = 1;
        final int NOTHING = 0;

        //Count number of mines on this tile
        for (int i = 0; i < board.size(); i++)
            if (board.get(i)[coordinate[0]][coordinate[1]])
                total += 1;

        //Calculate total mine probability
        double threshold = (double) total / board.size();
        double rand = r.nextDouble();

        //Probability of detonation
        if (rand < threshold / 4)
            return DETONATE;

        //Probability of detection
        if (rand > threshold / 4 && rand < threshold / 2)
            return DETECT;

        //Otherwise, nothing happens
        return NOTHING;

    }

    //Implementation of the linear entropy measurement.
    public double eMeasure(int[] coordinate) {
        int mines = 0;
        int clear = 0;

        //Count number of mines and number of clear spaces on this tile
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i)[coordinate[0]][coordinate[1]])
                mines++;
            else
                clear++;
        }

        //Calculate the probability of there being a mine
        double mineProb = (double) mines / board.size();

        //Probability of tile being clear
        double clearProb = (double) clear / board.size();

        //Linear entropy calculation (I know I can simplify this since clearProb is
        //1-mineProb but this makes the calculation more transparent).
        return 1.0 - mineProb * mineProb - clearProb * clearProb;
    }

    //Counts the average number of mines adjacent to a tile.
    public double countMines(int[] coordinate) {
        double numMines = 0;

        //Iterate over all boards
        for (boolean[][] instance : board) {

            //Iterate over adjacent tiles
            for (int i = coordinate[0] - 1; i < coordinate[0] + 2; i++) {
                for (int j = coordinate[1] - 1; j < coordinate[1] + 2; j++) {

                    //Ensure coordinate is in bounds, and don't count the tile itself
                    if ((i >= 0 && i < size) && (j >= 0 && j < size) && !(i == coordinate[0] && j == coordinate[1]))
                        if (instance[i][j])
                            numMines += 1.0;
                }
            }
        }

        //Average over all boards
        return numMines / board.size();
    }

    /*
    Handles removing boards from play as the superposition collapses. Removes all
    boards which either have a mine or don't have a mine, depending on the type
    parameter, then updates the toGuess board to match.
     */
    public void removeBoard(int[] coordinate, boolean type) {

        //Remove boards that match the type parameter
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i)[coordinate[0]][coordinate[1]] == type)
                board.remove(i);
        }

        //Remove the corresponding boards from the toGuess copy
        for (boolean[][] single : toGuess)
            if (!board.contains(single))
                toGuess.remove(single);
    }

    /*
    Prints all the game boards, with all the mines marked. This is
    used for debugging purposes only.
    */
    public void printBoards() {
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (board.get(i)[j][k])
                        System.out.print("X");
                    else
                        System.out.print("O");
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println("-----------------");
    }

    //Check how many boards remain in play.
    public int getNumRemaining() {
        return board.size();
    }

    /*
    This method checks the solution board that the user has flagged to see if it's
    correct. In the unlikely event of more than one board remaining at play at the
    end of the game, the user must flag each board one at a time; this is why the
    logic around the toGuess board is needed.
     */
    public int check(boolean[][] guess) {
        boolean flag;

        //Iterate over all boards
        for (int i = 0; i < toGuess.size(); i++) {
            flag = true;

            //If any tiles don't match, the guess is incorrect
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (board.get(i)[j][k] != guess[j][k])
                        flag = false;
                }
            }

            //If the board matches, remove the board from the list of remaining
            //boards to guess
            if (flag)
                toGuess.remove(i);
        }

        //Return the number of boards still remaining to guess
        return toGuess.size();
    }
}