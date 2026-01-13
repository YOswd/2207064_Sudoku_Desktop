package com.example.sudoku;

import java.io.*;

public class GameState {

    public static int[][] initialBoard;
    public static int[][] currentBoard;

    public static Difficulty difficulty = Difficulty.EASY;

    public static String getSaveFile() {
        return "sudoku_save_" + difficulty.name() + ".txt";
    }

    public static boolean isSaved = true;

    public static boolean isSolvedBySystem = false;

    public static long elapsedTimeInSeconds = 0;

    public static void saveToFile() {
        if (initialBoard == null || currentBoard == null) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(getSaveFile()))) {
            pw.println(difficulty.name());
            pw.println(elapsedTimeInSeconds);
            writeBoard(pw, initialBoard);
            writeBoard(pw, currentBoard);
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(getSaveFile()))) {
            difficulty = Difficulty.valueOf(br.readLine());
            elapsedTimeInSeconds = Long.parseLong(br.readLine());
            initialBoard = readBoard(br);
            currentBoard = readBoard(br);
            isSaved = true;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasSavedGame() {
        return new File(getSaveFile()).exists();
    }

    public static void clear() {
        File f = new File(getSaveFile());
        if (f.exists()) f.delete();

        initialBoard = null;
        currentBoard = null;
        isSaved = true;
    }

    private static void writeBoard(PrintWriter pw, int[][] board) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                pw.print(board[r][c]);
                if (c < 8) pw.print(" ");
            }
            pw.println();
        }
    }

    private static int[][] readBoard(BufferedReader br) throws IOException {
        int[][] board = new int[9][9];
        for (int r = 0; r < 9; r++) {
            String[] parts = br.readLine().split(" ");
            for (int c = 0; c < 9; c++) {
                board[r][c] = Integer.parseInt(parts[c]);
            }
        }
        return board;
    }

    public static void saveElapsedTime(long seconds) {
        elapsedTimeInSeconds = seconds;
        saveToFile();
    }

    public static long loadElapsedTime() {
        if (!hasSavedGame()) return 0;
        loadFromFile();
        return elapsedTimeInSeconds;
    }
}
