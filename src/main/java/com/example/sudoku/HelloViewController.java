package com.example.sudoku;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelloViewController {

    @FXML private Button btnPlay;
    @FXML private Button btnExit;

    @FXML
    public void initialize() {
        System.out.println("HelloViewController loaded");

        btnPlay.setOnAction(e -> openMenu());
        btnExit.setOnAction(e -> exitGame());
    }

    private void openMenu() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("menu.fxml")
            );

            Stage stage = (Stage) btnPlay.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void exitGame() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}
