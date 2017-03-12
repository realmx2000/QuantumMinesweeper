package com.company;

import java.util.Random;
import java.util.ArrayList;

/**
 * Created by realmx2000 on 2/16/17.
 */
public class Board {
    private int NUM_BOARDS = 3;
    private int NUM_MINES = 15;
    private ArrayList<boolean[][]> board;
    private ArrayList<boolean[][]> toGuess;
    private int size;

    public Board(int size, int num, int mines) {
        this.size = size;
        NUM_BOARDS = num;
        NUM_MINES = mines;
        board = new ArrayList<boolean[][]>(NUM_BOARDS);
        for (int i = 0; i < NUM_BOARDS; i++) {
            boolean[][] instance = new boolean[size][size];
            board.add(instance);
        }
        createMines();
        toGuess = board;
    }

    private void createMines() {
        Random r = new Random();
        int temp;
        for (int i = 0; i < NUM_BOARDS; i++) {
            boolean[] mines = new boolean[size * size];
            for (int j = 0; j < NUM_MINES; j++) {
                temp = r.nextInt(size * size);
                if (!mines[temp])
                    mines[temp] = true;
                else
                    j--;
            }
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    board.get(i)[j][k] = mines[j * size + k];
                }
            }
        }
    }

    public boolean deterministicMeasure(int[] coordinate) {
        int total = 0;
        Random r = new Random();
        for (int i = 0; i < board.size(); i++)
            if (board.get(i)[coordinate[0]][coordinate[1]])
                total += 1;
        double threshold = (double) total / board.size();
        if (r.nextDouble() < threshold)
            return false;
        return true;
    }

    public int iFMeasure(int[] coordinate) {
        int total = 0;
        Random r = new Random();
        for (int i = 0; i < board.size(); i++)
            if (board.get(i)[coordinate[0]][coordinate[1]])
                total += 1;
        double threshold = (double) total / board.size();
        double rand = r.nextDouble();
        if (rand < threshold / 4)
            return 2;
        if (rand > threshold / 4 && rand < threshold / 2)
            return 1;
        return 0;

    }

    public double eMeasure(int[] coordinate) {
        int mines = 0;
        int clear = 0;
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i)[coordinate[0]][coordinate[1]])
                mines++;
            else
                clear++;
        }
        double mineProb = (double) mines / board.size();
        double clearProb = (double) clear / board.size();
        return 1.0 - mineProb * mineProb - clearProb * clearProb;
    }

    public double countMines(int[] coordinate) {
        double numMines = 0;
        for (boolean[][] instance : board) {
            for (int i = coordinate[0] - 1; i < coordinate[0] + 2; i++) {
                for (int j = coordinate[1] - 1; j < coordinate[1] + 2; j++) {
                    if ((i >= 0 && i < size) && (j >= 0 && j < size) && !(i == coordinate[0] && j == coordinate[1]))
                        if (instance[i][j])
                            numMines += 1.0;
                }
            }
        }
        return numMines / board.size();
    }

    public void removeBoard(int[] coordinate, boolean type) {
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i)[coordinate[0]][coordinate[1]] == type)
                board.remove(i);
        }
        for (boolean[][] single : toGuess)
            if (!board.contains(single))
                toGuess.remove(single);
    }

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

    public int getSize() {
        return board.size();
    }

    public int check(boolean[][] guess) {
        boolean flag = true;
        for (int i = 0; i < toGuess.size(); i++) {
            flag = true;
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (board.get(i)[j][k] != guess[j][k])
                        flag = false;
                }
            }
            if (flag)
                toGuess.remove(i);
        }
        return toGuess.size();
    }
}
