package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.util.Optional;
import java.util.UUID;


public class AddLibrarianModalAdminController {

    @FXML
    private TextField emailTextBox;
    @FXML
    private Label infoTextBox;
    Stage stage;

    Optional<User> user;
    public UserRepository userRepository = RepositoryFactory.getUserRepository();

    public void saveUserClick(ActionEvent actionEvent) {


    }

    public void search(ActionEvent actionEvent) {
        String email = emailTextBox.getText();
        user = userRepository.getUserByEmail(email);
        if (user.isEmpty()){
            System.out.println("user not found" );
        }
        else{
            User x = user.get();
        }


    }

    public void setAdmin(ActionEvent actionEvent) {
    }

    public void setLibrarian(ActionEvent actionEvent) {
    }

    public void SetUser(ActionEvent actionEvent) {
    }
}
