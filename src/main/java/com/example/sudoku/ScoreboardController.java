package com.example.sudoku;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.util.List;

public class ScoreboardController {

    @FXML
    private ListView<String> listView;

    @FXML
    public void initialize() {
        refreshScores();
    }

    private void refreshScores() {
        listView.getItems().clear();

        List<String> scores = ScoreBoardHelper.getScores(GameState.difficulty);

        if (scores.isEmpty()) {
            listView.getItems().add("No scores yet!");
            return;
        }

        int rank = 1;
        for (String s : scores) {
            if (rank > 10) break;
            listView.getItems().add(rank + ". " + s);
            rank++;
        }
    }

    @FXML
    private void onResetClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Scoreboard");
        alert.setHeaderText("Reset scores for " + GameState.difficulty + "?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                ScoreBoardHelper.resetScoreboard(GameState.difficulty);
                refreshScores();
            }
        });
    }

    @FXML
    private void onBackClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            listView.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
