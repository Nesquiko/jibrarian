package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.util.UUID;


public class AddLibrarianModalAdminController {
    @FXML
    private TextField idTextBox;
    @FXML
    private TextField emailTextBox;
    @FXML
    private TextField passwordTextBox;
    Stage stage;

    public UserRepository userRepository = RepositoryFactory.getUserRepository();

    public void saveUserClick(ActionEvent actionEvent) {
        User user = new User(UUID.randomUUID(),  emailTextBox.getText(), passwordTextBox.getText(), Role.LIBRARIAN );
        try {
            userRepository.saveUser(user);
        } catch (UserRepository.AlreadyExistingUserException e) {
            e.printStackTrace();
        }

    }
}
