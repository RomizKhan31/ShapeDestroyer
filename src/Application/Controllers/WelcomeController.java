package Application.Controllers;

import Application.Credentials;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.sql.*;

public class WelcomeController {

    @FXML
    private Label userChecker;
    @FXML
    private TextField userTextField;

    private static String URL = Credentials.getUrl();
    private  static String USER = Credentials.getUser();
    private static String PASSWORD = Credentials.getPassword();

    private static String userName = "";

    public static String getUserName() {
        return userName;
    }

    public void submitUser() {
        userName = userTextField.getText();
        if (userName.isEmpty()) {
            userChecker.setText("Please enter a username!");
            return;
        }
        try {
            boolean success = addPlayer(userName);
            if (success) {
                userChecker.setText("Player added successfully!");
            } else {
                userChecker.setText("Username isn't available. Try another.");
            }
        } catch (Exception e) {
            userChecker.setText("Unexpected error: " + e.getMessage());
        }
    }


    public boolean addPlayer(String playerName) throws SQLException {
        String insertQuery = "INSERT INTO players (name) VALUES (?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, playerName);
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw e;
        }
    }


    //For updating player score in the database
    static void updatePlayerScore(String userName, int score){
        if (userName == null || userName.isEmpty()) {
            System.out.println("Username is null or empty. Cannot update score.");
            return;
        }
        String updateQuery = "UPDATE players SET score = ? WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, score);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @FXML
    public void easyMode() {
        String mode  = "Easy";
        startGame(1.0, mode);
    }

    @FXML
    public void mediumMode() {
        String mode  = "Medium";
        startGame(3.0, mode);
    }

    @FXML
    public void hardMode() {
        String mode  = "Hard";
        startGame(4.5, mode);
    }

    private void startGame(double fallingSpeed, String str) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Application/FXMLS/gameUi.fxml"));
            Parent root = loader.load();

            GameController Obj = loader.getController();
            Obj.setFallingSpeed(fallingSpeed);
            Obj.setMode(str);

            Stage stage = (Stage) userTextField.getScene().getWindow();
            Scene scene = new Scene(root, 400, 630);
            stage.setScene(scene);
            stage.setTitle("Shape Destroyer");

            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToScore(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Application/FXMLS/ScoreTable.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Shape Destroyer");
        stage.setResizable(false);
        stage.show();
    }


    public void downloadRecords(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Player Records");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showSaveDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if (selectedFile == null) return; // user cancelled

        // ✅ Ensure the file has .csv extension
        File file = selectedFile;
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        String query = "SELECT * FROM players order by score desc";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter writer = new FileWriter(file)) {

            // Write CSV header
            writer.append("Player Name,Registration Time,Score\n");

            // Write data rows safely with quotes
            while (rs.next()) {
                writer.append("\"").append(rs.getString("name")).append("\"").append(",");
                writer.append("\"").append(rs.getTimestamp("registration_time").toString()).append("\"").append(",");
                writer.append("\"").append(String.valueOf(rs.getInt("score"))).append("\"").append("\n");
            }

            System.out.println("CSV exported successfully: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
