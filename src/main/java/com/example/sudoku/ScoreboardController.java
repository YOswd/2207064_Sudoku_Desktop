package com.example.sudoku;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

public class ScoreboardController {

    @FXML
    private ListView<String> listView;

    @FXML
    public void initialize() {
        loadScores();
    }

    private void loadScores() {
        listView.getItems().clear();
        listView.getItems().addAll(
                ScoreBoardHelper.getScores(GameState.difficulty)
        );
    }

    @FXML
    private void onResetClicked() {

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Reset scores for " + GameState.difficulty + "?"
        );

        alert.setTitle("Confirm Reset");
        alert.setHeaderText("This will delete all scores");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK)
            return;

        ScoreBoardHelper.resetScoreboard(GameState.difficulty);

        loadScores();
    }
}
