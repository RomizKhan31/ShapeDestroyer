package Application.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Application.ModelPlayers;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Application.SpecialClassForView;

public class ScoreController implements Initializable {
    @FXML
    public TableView<ModelPlayers> playersTable;


    public void BackToHome(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Application/FXMLS/Welcome.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public TableColumn<ModelPlayers, String> nameCol;
    @FXML
    public TableColumn<ModelPlayers, String> timeCol;
    @FXML
    public TableColumn<ModelPlayers, String> scoreCol;

    public ObservableList<ModelPlayers> obsvList = FXCollections.observableArrayList();


    public void initialize(URL location, ResourceBundle resources) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        ArrayList<ModelPlayers> playerList = SpecialClassForView.viewScores();
        obsvList.addAll(playerList);
        playersTable.setItems(obsvList);
    }

}
