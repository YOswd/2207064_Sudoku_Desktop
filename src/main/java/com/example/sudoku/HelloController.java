package com.example.sudoku;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.File;

public class HelloController {

    @FXML private GridPane grid;
    @FXML private Button btnSolve, btnNew, btnExit, btnSave, btnClear;

    private TextField[][] cells = new TextField[9][9];
    private long startTime;

    private static final String SAVE_FILE = "sudoku_save.txt";

    @FXML
    public void initialize() {
        createGrid();

        btnSolve.setOnAction(e -> solveSudoku());
        btnNew.setOnAction(e -> startNewGameClicked());
        btnExit.setOnAction(e -> goToMenu());
        btnSave.setOnAction(e -> saveGame());
        btnClear.setOnAction(e -> clearGrid());
    }

    private void createGrid() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                TextField tf = new TextField();
                tf.setPrefSize(50, 50);
                tf.setStyle("-fx-font-size:18; -fx-alignment:center;");
                final int row = r, col = c;

                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("[1-9]?")) tf.setText(oldVal);
                });

                cells[r][c] = tf;
                grid.add(tf, c, r);
            }
        }
    }

    private void startNewGameClicked() {
        File saveFile = new File(SAVE_FILE);

        if (saveFile.exists()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Start New Game");
            alert.setHeaderText("Previous saved game will be discarded!");
            alert.setContentText("Do you want to continue?");

            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (result != ButtonType.OK) return;

            saveFile.delete();
        }

        startNewGame();
    }

    public void startNewGame() {
        btnSave.setDisable(false);
        btnClear.setDisable(false);

        startTime = System.currentTimeMillis();
        int[][] puzzle = DatabaseHelper.getRandomPuzzle(GameState.difficulty);

        GameState.initialBoard = new int[9][9];
        GameState.currentBoard = new int[9][9];
        GameState.isSaved = false;
        GameState.isSolvedBySystem = false;

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int value = puzzle[r][c];
                GameState.initialBoard[r][c] = value;
                GameState.currentBoard[r][c] = value;
                cells[r][c].setText(value == 0 ? "" : String.valueOf(value));
                cells[r][c].setEditable(value == 0);
            }
        }
    }

    public void loadGame() {
        btnSave.setDisable(false);
        btnClear.setDisable(false);
        GameState.isSolvedBySystem = false;

        startTime = System.currentTimeMillis();
        if (GameState.initialBoard == null || GameState.currentBoard == null) {
            return;
        }
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int value = GameState.currentBoard[r][c];
                cells[r][c].setText(value == 0 ? "" : String.valueOf(value));
                cells[r][c].setEditable(GameState.initialBoard[r][c] == 0);
            }
        }
        GameState.isSaved = true;
    }


    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++)
            System.arraycopy(board[r], 0, copy[r], 0, 9);
        return copy;
    }

    private void saveGame() {
        if(GameState.isSolvedBySystem) return;
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++) {
                String text = cells[r][c].getText();
                GameState.currentBoard[r][c] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        GameState.saveToFile();
        GameState.isSaved = true;
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game saved successfully!");
        alert.showAndWait();
    }

    private void clearGrid() {
        if(GameState.isSolvedBySystem) return;
        if (GameState.initialBoard == null || GameState.currentBoard == null) {
            return;
        }

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {

                if (GameState.initialBoard[r][c] == 0) {
                    cells[r][c].clear();
                    GameState.currentBoard[r][c] = 0;
                }
            }
        }

        GameState.isSaved = false;
    }


    private void solveSudoku() {
        if(GameState.currentBoard == null) return;
        GameState.isSolvedBySystem = true;
        int[][] board = copyBoard(GameState.currentBoard);
        if (!solve(board)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot solve this puzzle!");
            alert.showAndWait();
            return;
        }

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c].setText(String.valueOf(board[r][c]));
                cells[r][c].setEditable(false);
            }
        }

        btnSave.setDisable(true);
        btnClear.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Solved by System");
        alert.setHeaderText("Game Terminated");
        alert.setContentText("Game Over.");
        alert.showAndWait();

        GameState.isSaved = true;
    }

    private boolean solve(int[][] board) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, r, c, num)) {
                            board[r][c] = num;
                            if (solve(board)) return true;
                            board[r][c] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < 9; i++)
            if (board[row][i] == num || board[i][col] == num) return false;
        int startRow = row - row % 3, startCol = col - col % 3;
        for (int r = startRow; r < startRow + 3; r++)
            for (int c = startCol; c < startCol + 3; c++)
                if (board[r][c] == num) return false;
        return true;
    }

    private void goToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            grid.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
