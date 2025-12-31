package com.example.sudoku;

import java.sql.*;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:C:/Sudoku/sudoku.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {

            st.execute("DROP TABLE IF EXISTS sudoku_puzzles");
            st.execute("""
               CREATE TABLE IF NOT EXISTS sudoku_puzzles (
                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                   puzzle TEXT NOT NULL
               )
            """);

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sudoku_puzzles");
            if (rs.next() && rs.getInt(1) == 0) {
                st.execute("""
                   INSERT INTO sudoku_puzzles (puzzle) VALUES
                   ('530070000600195000098000060800060003400803001700020006060000280000419005000080079'),
                   ('000260701680070090190004500820100040004602900050003028009300074040050036703018000'),
                   ('302609005000000300000003010700000006060708020900000001090500000004000000800402107'),
                   ('006100720000050004000003060007000080010709050080000600030500000100060000040009100'),
                   ('000907000007000800000000000030040020050600040060010030000000000009000100000103000'),
                   ('300200000000107000706030500070009080900020004010800050009040301000702000000008006'),
                   ('000000907000420180000705026100904000050000040000507009920108000034059000507000000'),
                   ('030050040008010500460000012070502080000603000040109030250000098001020600080060020'),
                   ('020810740700003100009002003030007050000040000060500070500900600001200800076053090'),
                   ('100920000524010000000000070050008102000000000402700090060000000000030945000071006')
               """);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int[][] getRandomPuzzle() {
        int[][] board = new int[9][9];
        String puzzle = null;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT puzzle FROM sudoku_puzzles ORDER BY RANDOM() LIMIT 1")) {

            if (rs.next()) puzzle = rs.getString("puzzle");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (puzzle == null) {
            System.out.println("No puzzle found in database! Using default puzzle.");
            puzzle = "530070000600195000098000060800060003400803001700020006060000280000419005000080079";
        }

        int k = 0;
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                board[r][c] = puzzle.charAt(k++) - '0';

        return board;
    }
}
