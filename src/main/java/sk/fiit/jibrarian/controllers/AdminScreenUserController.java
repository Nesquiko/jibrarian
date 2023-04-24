package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.model.Role.ADMIN;
import static sk.fiit.jibrarian.model.Role.LIBRARIAN;
import static sk.fiit.jibrarian.model.Role.MEMBER;


public class AdminScreenUserController {
    private static final Logger LOGGER = Logger.getLogger(AdminScreenUserController.class.getName());
    private final UserRepository userRepository = RepositoryFactory.getUserRepository();

    @FXML
    private Label userId;
    @FXML
    private Button removeButton;
    @FXML
    private RadioButton rButton1;
    @FXML
    private RadioButton rButton2;
    @FXML
    private RadioButton rButton3;
    @FXML
    private AnchorPane pane;
    private AdminScreenListController adminScreenListController;
    private User user;

    public void setData(User user, AdminScreenListController adminScreenListController) {
        this.user = user;
        this.adminScreenListController = adminScreenListController;
        userId.setText(user.getEmail());
        if (user.getRole() == ADMIN) {
            rButton1.setSelected(true);
            rButton2.setSelected(false);
            rButton3.setSelected(false);
        } else if (user.getRole() == LIBRARIAN) {
            rButton1.setSelected(false);
            rButton2.setSelected(true);
            rButton3.setSelected(false);
        } else {
            rButton1.setSelected(false);
            rButton2.setSelected(false);
            rButton3.setSelected(true);
        }
    }

    public void deleteUser(ActionEvent actionEvent) throws UserRepository.UserNotFound {
        removeButton.setVisible(false);
        userId.setTextFill(Paint.valueOf("Red"));
        userRepository.deleteUser(user);
        rButton1.setVisible(false);
        rButton2.setVisible(false);
        rButton3.setVisible(false);
        adminScreenListController.loadList();
    }

    public void setAdmin(ActionEvent actionEvent) {
        updateUserRole(ADMIN);
        LOGGER.info("admin is set");
        adminScreenListController.loadList();
    }

    public void setLibrarian(ActionEvent actionEvent) {
        updateUserRole(LIBRARIAN);
        LOGGER.info("librarian is set");
        adminScreenListController.loadList();
    }

    public void setUser(ActionEvent actionEvent) {
        updateUserRole(MEMBER);
        LOGGER.info("user is set");
        adminScreenListController.loadList();
    }

    private void updateUserRole(Role x) {
        user.setRole(x);
        try {
            userRepository.updateUser(user);
        } catch (UserRepository.UserNotFound e) {
            e.printStackTrace();
        }
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        removeButton.setText(rs.getString("delete"));
        rButton1.setText(rs.getString("adminLabel"));
        rButton2.setText(rs.getString("librarianLabel"));
        rButton3.setText(rs.getString("userLabel"));
    }
}
