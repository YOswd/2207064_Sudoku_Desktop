package com.example.sudoku;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScoreBoardHelper {

    private static final String DB_URL = "jdbc:sqlite:C:/Sudoku/sudoku.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS scoreboard (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    difficulty TEXT NOT NULL,
                    player_name TEXT NOT NULL,
                    time_taken INTEGER NOT NULL,
                    played_on TEXT NOT NULL
                )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addScore(String name, int seconds) {
        String sql = "INSERT INTO scoreboard VALUES(NULL,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, GameState.difficulty.name());
            ps.setString(2, name);
            ps.setInt(3, seconds);
            ps.setString(4, LocalDate.now().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getScores(Difficulty difficulty) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM scoreboard WHERE difficulty=? ORDER BY time_taken ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement st = conn.prepareStatement(sql)){
             st.setString(1, difficulty.name());
             ResultSet rs = st.executeQuery();

            while (rs.next()) {
                list.add(
                        rs.getString("player_name") + " | " +
                                rs.getInt("time_taken") + " sec | " +
                                rs.getString("played_on")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void resetScoreboard(Difficulty difficulty) {
        String sql = "DELETE FROM scoreboard WHERE difficulty = ?";

        try(Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement st = conn.prepareStatement(sql)) {

        st.setString(1, difficulty.name());
        st.executeUpdate();

        } catch (SQLException e) {
           e.printStackTrace();
        }
    }
}
