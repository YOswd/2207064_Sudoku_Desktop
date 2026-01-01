package com.example.sudoku;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.io.File;
import java.util.List;
import java.util.Arrays;

public class MenuController {

    @FXML private Button btnNewGame;
    @FXML private Button btnResumeGame;
    @FXML private Button btnScoreboard;
    @FXML private Button btnDifficulty;
    @FXML private Button btnExit;

    @FXML
    public void initialize() {
        btnNewGame.setOnAction(e -> openSudoku());
        btnResumeGame.setOnAction(e -> resumeGame());
        btnScoreboard.setOnAction(e -> showScoreboard());
        btnDifficulty.setOnAction(e -> showDifficultyDialog());
        btnExit.setOnAction(e -> goToHello());
    }

    private void openSudoku() {
        try {
            if (GameState.hasSavedGame()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Start New Game");
                alert.setHeaderText("Previous saved game will be discarded!");
                alert.setContentText("Do you wish to continue?");
                ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
                if (result != ButtonType.OK) return;

                GameState.clear();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Sudoku.fxml"));
            btnNewGame.getScene().setRoot(loader.load());
            HelloController controller = loader.getController();
            controller.startNewGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resumeGame() {
        Difficulty saved = GameState.getSavedDifficulty();

        if (saved == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Resume Game");
            alert.setHeaderText(null);
            alert.setContentText("No saved game found!");
            alert.showAndWait();
            return;
        }

        ChoiceDialog<Difficulty> dialog = new ChoiceDialog<>(saved, Difficulty.values());

        dialog.setTitle("Resume Game");
        dialog.setHeaderText("Select Difficulty to Resume");
        dialog.setContentText("Difficulty: ");

        dialog.showAndWait().ifPresent( diff ->{
            GameState.difficulty = diff;

            if (!GameState.loadFromFile()) {
                new Alert(Alert.AlertType.ERROR,"Failed to load saved game.").showAndWait();
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Sudoku.fxml"));
                btnResumeGame.getScene().setRoot(loader.load());
                HelloController controller = loader.getController();
                controller.loadGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showScoreboard() {
        List<String> scores = ScoreBoardHelper.getScores(GameState.difficulty);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Scoreboard - " + GameState.difficulty);
        alert.setHeaderText("Best Scores (" + GameState.difficulty + ")");
        alert.setContentText(scores.isEmpty() ? "No scores yet!" : String.join("\n", scores));
        alert.showAndWait();
    }

    private void showDifficultyDialog() {
        ChoiceDialog<Difficulty> dialog = new ChoiceDialog<>(GameState.difficulty,
                Arrays.asList(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD));

        dialog.setTitle("Select Difficulty");
        dialog.setHeaderText("Choose Game Difficulty");
        dialog.setContentText("Difficulty:");

        dialog.showAndWait().ifPresent(selected -> {
            GameState.difficulty = selected;

            new Alert(Alert.AlertType.INFORMATION,"Difficulty set to: " + selected).show();
        });
    }

    private void goToHello() {
        try {
            btnExit.getScene().setRoot(
                    FXMLLoader.load(getClass().getResource("hello-view.fxml"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
