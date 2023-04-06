package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.util.UUID;


public class UserAuthController {
    public static User user;
    @FXML
    private void switchToUserScreen() throws IOException {
        user = new User(UUID.randomUUID(), "xyz@gmail.com", "hocico", Role.MEMBER);
        App.setRoot("views/user_screen");
        App.maximizeScreen();
    }

    @FXML
    private void switchToLibrarianScreen() throws IOException {
        user = new User(UUID.randomUUID(), "xyz@gmail.com", "hocico", Role.LIBRARIAN);
        App.setRoot("views/librarian_screen");
        App.maximizeScreen();
    }

    @FXML
    private void switchToAdminScreen() throws IOException {
        user = new User(UUID.randomUUID(), "xyz@gmail.com", "hocico", Role.ADMIN);
        App.setRoot("views/admin_screen");
        App.maximizeScreen();
    }
}
