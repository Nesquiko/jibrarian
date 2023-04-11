package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.fiit.jibrarian.model.Item;

public class LibrarianLendOutController {

    @FXML
    private Label availableLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button giveOutButton;

    @FXML
    private TextField readersEmail;

    @FXML
    private TextField reservationCode;

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
    void closeWindow() {
        Stage stage = (Stage) availableLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void lendOut() {

    }
}
