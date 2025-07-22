package Application.Controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameController {

    @FXML private Label modeLabel;
    @FXML private Label scoreLabel;
    @FXML private Pane gamePane;

    @FXML private Button exitButton;

    private Timeline gameLoop;
    private int score = 0;
    private double fallingSpeed = 2.0;
    private String mode;
    private final List<Shape> fallingShapes = new ArrayList<>();
    private final Random random = new Random();
    private int ticks = 0;


    public void setFallingSpeed(double speed) {
        this.fallingSpeed = speed;
    }

    public void setMode(String mode) {
        this.mode = mode;
        if (modeLabel != null) {
            modeLabel.setText("Mode: " + mode);
        }
    }


    @FXML
    public void initialize() {
        startGame();
    }




    private void startGame() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            try {
                updateGame();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();
    }

    private void updateGame() throws Exception {
        // Gradually increase falling speed
        ticks++;
        if (ticks % 300 == 0) {
            fallingSpeed += 0.5;
        }

        // Occasionally spawn a new shape
        if (random.nextDouble() < 0.05) {
            Shape shape = createRandomShape();
            if (!isOverlapping(shape)) {
                fallingShapes.add(shape);
                gamePane.getChildren().add(shape);
            }
        }

        // Move all shapes downward
        Iterator<Shape> iterator = fallingShapes.iterator();
        while (iterator.hasNext()) {
            Shape shape = iterator.next();
            shape.setLayoutY(shape.getLayoutY() + fallingSpeed);

            if (shape.getLayoutY() >= gamePane.getHeight()) {
                gameLoop.stop();
                //update player score in the database
                WelcomeController.updatePlayerScore(WelcomeController.getUserName(), score);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game Over! Shape reached the bottom.");
                alert.show();
                return;
            }
        }

        scoreLabel.setText("Score: " + score);
    }

    private Shape createRandomShape() {
        int type = random.nextInt(3);
        Shape shape;
        double x = random.nextDouble() * (gamePane.getWidth() - 60);

        if (type == 0) {
            shape = new Circle(20, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
        } else if (type == 1) {
            shape = new Rectangle(30, 30, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
        } else {
            shape = new Rectangle(50, 20, Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
        }

        shape.setLayoutX(x);
        shape.setLayoutY(0);

        shape.setOnMouseClicked((MouseEvent e) -> {
            gamePane.getChildren().remove(shape);
            fallingShapes.remove(shape);
            score += 10;
            scoreLabel.setText("Score: " + score);
        });

        return shape;
    }

    private boolean isOverlapping(Shape newShape) {
        for (Shape shape : fallingShapes) {
            if (newShape.getBoundsInParent().intersects(shape.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

















    private void resetGame() {
        score = 0;
        scoreLabel.setText("Score: 0");
        gamePane.getChildren().clear();
        fallingShapes.clear();
        fallingSpeed = 2.0;
        ticks = 0;
    }

    @FXML
    private void handlePause() {
        if (gameLoop != null) {
            if (gameLoop.getStatus() == Animation.Status.RUNNING) {
                gameLoop.pause();
//                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game Paused!");
//                alert.show();
            } else {
                gameLoop.play();
//                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game Resumed!");
//                alert.show();
            }
        }
    }

    @FXML
    private void handleRestart() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        resetGame();
        startGame();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game Restarted!");
        alert.show();
    }

    @FXML
    private void handleExit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Application/FXMLS/welcome.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) gamePane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Welcome");
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load homepage.");
            alert.show();
        }
    }


}
