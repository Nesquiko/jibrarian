package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sk.fiit.jibrarian.model.Item;

import java.net.URL;
import java.util.ResourceBundle;

public class LibrarianTakeInController{

    @FXML
    private Label availableLabel;

    @FXML
    private Label reservedLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label totalLabel;

    public void setData(Item item){
        titleLabel.setText(item.getTitle());
        availableLabel.setText("Available: "+ item.getAvailable().toString());
        reservedLabel.setText("Reserved: "+ item.getReserved().toString());
        totalLabel.setText("Total: "+ item.getTotal().toString());
    }
    @FXML
    private void cancelButton() {
        Stage stage = (Stage) availableLabel.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void takeInButton() {

    }
}