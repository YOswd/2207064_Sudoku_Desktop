package com.example.sudoku;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;

public class HelloController {

    @FXML private GridPane grid;
    @FXML private Button btnSolve, btnNew, btnExit, btnSave, btnClear;
    @FXML private Label timerLabel;

    private TextField[][] cells = new TextField[9][9];
    private long startTime;
    private Timeline timer;
    private long elapsedTimeInSeconds = 0;

    private static final String COLOR_SELECTED = "#3F51B5";
    private static final String COLOR_RELATED = "#cfe8ff";
    private static final String COLOR_NORMAL = "white";

    private int selectedRow = -1;
    private int selectedCol = -1;

    @FXML
    public void initialize() {
        StackPane.setAlignment(grid, Pos.CENTER);
        grid.setStyle("-fx-background-color: black; -fx-background-radius: 10;");
        grid.setPrefSize(450, 450);


        createGrid();

        grid.setMinSize(450, 450);
        grid.setPrefSize(450, 450);
        grid.setMaxSize(450, 450);

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
                tf.setAlignment(Pos.CENTER);
                tf.setFont(Font.font(18));

                final int row = r;
                final int col = c;

                tf.setOnMouseClicked(e -> {
                    selectedRow = row;
                    selectedCol = col;
                    refreshHighlights();
                });

                tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(tf, Priority.ALWAYS);
                GridPane.setVgrow(tf, Priority.ALWAYS);

                int top = (r % 3 == 0) ? 3 : 1;
                int left = (c % 3 == 0) ? 3 : 1;
                int right = (r == 8) ? 3 : 1;
                int bottom = (c == 8) ? 3 : 1;

                tf.setStyle("-fx-border-color: red;" + "-fx-border-width: "
                        + top + " " + right + " " + bottom + " " + left + " " + "-fx-background-color: white;");

                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("[1-9]?")) {
                        tf.setText(oldVal);
                        return;
                    }

                    refreshHighlights();

                    if(isGameCompletedCorrectly()) onGameCompleted();
                });

                cells[r][c] = tf;
                grid.add(tf, c, r);
            }
        }
    }

    private static int parseIntOrZero(String s) {
        if(s == null || s.isEmpty()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void refreshAllConflicts() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (!cells[r][c].getText().isEmpty()) {
                    checkCellConflict(r, c);
                }
            }
        }
    }

    private void refreshHighlights() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c].setStyle(
                        "-fx-font-size: 18; -fx-alignment: center;" +
                                "-fx-background-color: " + COLOR_NORMAL + ";" +
                                "-fx-border-color: black;"
                );
            }
        }

        if (selectedRow != -1 && selectedCol != -1) {
            for (int i = 0; i < 9; i++) {
                highlightCell(selectedRow, i, COLOR_RELATED);
                highlightCell(i, selectedCol, COLOR_RELATED);
            }

            int startRow = selectedRow - selectedRow % 3;
            int startCol = selectedCol - selectedCol % 3;

            for (int r = startRow; r < startRow + 3; r++) {
                for (int c = startCol; c < startCol + 3; c++) {
                    highlightCell(r, c, COLOR_RELATED);
                }
            }

            highlightCell(selectedRow, selectedCol, COLOR_SELECTED);
        }

        refreshAllConflicts();
    }


    private void checkCellConflict(int row, int col) {
        String text = cells[row][col].getText();
        if (text.isEmpty()) return;

        int num = Integer.parseInt(text);

        for (int c = 0; c < 9; c++) {
            if (c != col && num == parseIntOrZero(cells[row][c].getText())) {
                markConflict(row, col);
                markConflict(row, c);
            }
        }

        for (int r = 0; r < 9; r++) {
            if (r != row && num == parseIntOrZero(cells[r][col].getText())) {
                markConflict(row, col);
                markConflict(r, col);
            }
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;

        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if ((r != row || c != col) &&
                        num == parseIntOrZero(cells[r][c].getText())) {
                    markConflict(row, col);
                    markConflict(r, c);
                }
            }
        }
    }

    private void highlightCell(int r, int c, String color) {
        cells[r][c].setStyle(
                "-fx-font-size: 18; -fx-alignment: center;" +
                        "-fx-background-color: " + color + ";" +
                        "-fx-border-color: black;"
        );
    }

    private void markConflict(int r, int c) {
        cells[r][c].setStyle(
                "-fx-font-size: 18; -fx-alignment: center;" +
                        "-fx-background-color: #ffb3b3;" +
                        "-fx-border-color: red;" +
                        "-fx-border-width: 2;"
        );
    }

    private void startNewGameClicked() {
        File saveFile = new File(GameState.getSaveFile());

        if (saveFile.exists()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Start New Game");
            alert.setHeaderText("Previous saved game will be discarded!");
            alert.setContentText("Do you wish to continue?");

            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (result != ButtonType.OK) return;

            saveFile.delete();
        }

        startNewGame();
    }

    public void startNewGame() {
        btnSave.setDisable(false);
        btnClear.setDisable(false);

        elapsedTimeInSeconds = 0;
        selectedRow = -1;
        selectedCol = -1;
        startTime = System.currentTimeMillis();
        startTimer();
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
                cells[r][c].setStyle("-fx-font-size: 18; -fx-alignment: center; -fx-background-color: white");
            }
        }
        grid.requestFocus();
    }

    public void loadGame() {
        btnSave.setDisable(false);
        btnClear.setDisable(false);
        GameState.isSolvedBySystem = false;

        elapsedTimeInSeconds = GameState.loadElapsedTime();
        startTime = System.currentTimeMillis();
        startTimer();

        if (GameState.initialBoard == null || GameState.currentBoard == null) return;

        selectedCol = -1;
        selectedRow = -1;

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int value = GameState.currentBoard[r][c];
                cells[r][c].setText(value == 0 ? "" : String.valueOf(value));
                cells[r][c].setEditable(GameState.initialBoard[r][c] == 0);
            }
        }

       refreshAllConflicts();
       grid.requestFocus();

        GameState.isSaved = true;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++)
            System.arraycopy(board[r], 0, copy[r], 0, 9);
        return copy;
    }

    private void saveGame() {
        elapsedTimeInSeconds += (System.currentTimeMillis() - startTime) / 1000;
        startTime = System.currentTimeMillis();
        GameState.saveElapsedTime(elapsedTimeInSeconds);

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
        int[][] board = copyBoard(GameState.initialBoard);
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

        if (timer != null) timer.stop();
        timerLabel.setText("Time: 00:00");

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

    private boolean isGameCompletedCorrectly() {
        int[][] board = getCurrentBoard();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == 0) return false;

                int num = board[r][c];
                board[r][c] = 0;

                if (!isValid(board, r, c, num)) {
                    return false;
                }

                board[r][c] = num;
            }
        }
        return true;
    }

    private int[][] getCurrentBoard() {
        int[][] board = new int[9][9];
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                board[r][c] = parseIntOrZero(cells[r][c].getText());
        return board;
    }

    private void onGameCompleted() {
        if(GameState.isSolvedBySystem) return;

        btnSave.setDisable(true);
        btnClear.setDisable(true);
        btnNew.setDisable(true);

        int timeTaken = (int) ((System.currentTimeMillis() - startTime) / 1000);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Congratulations!");
        dialog.setHeaderText("You solved the puzzle!");
        dialog.setContentText("Enter your name:");

        dialog.showAndWait().ifPresent(name -> {
            ScoreBoardHelper.addScore(name, timeTaken);
            goToMenu();
        });

        if (GameState.isSaved) return;
        GameState.isSaved = true;
    }

    private void startTimer() {
        if (timer != null) timer.stop();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long totalElapsed = elapsedTimeInSeconds + (System.currentTimeMillis() - startTime) / 1000;
            int minutes = (int) (totalElapsed / 60);
            int seconds = (int) (totalElapsed % 60);
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
}
