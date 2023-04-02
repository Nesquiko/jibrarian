package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sk.fiit.jibrarian.model.User;



public class AdminScreenUserController {
    @FXML
    private Label userId;

    public void setData(User user) {
        userId.setText(user.getEmail());
    }
}
