package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import sk.fiit.jibrarian.App;

import java.io.IOException;


public class UserAuthController {
    @FXML
    private void switchToUserScreen() throws IOException {
        App.setRoot("views/user_screen");
        App.maximizeScreen();
    }

    @FXML
    private void switchToLibrarianScreen() throws IOException {
        App.setRoot("views/librarian_screen");
        App.maximizeScreen();
    }

    @FXML
    private void switchToAdminScreen() throws IOException {
        App.setRoot("views/admin_screen");
        App.maximizeScreen();
    }

}
