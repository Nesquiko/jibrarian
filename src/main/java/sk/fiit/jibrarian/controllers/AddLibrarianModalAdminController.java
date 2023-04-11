package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import static sk.fiit.jibrarian.model.Role.ADMIN;
import static sk.fiit.jibrarian.model.Role.LIBRARIAN;
import static sk.fiit.jibrarian.model.Role.MEMBER;


public class AddLibrarianModalAdminController implements Initializable {

    @FXML
    private TextField emailTextBox;
    @FXML
    private Label infoTextBox;
    @FXML
    private Text errorMSG;
    @FXML
    private RadioButton rButton1;
    @FXML
    private RadioButton rButton2;
    @FXML
    private RadioButton rButton3;
    @FXML
    private Button searchBtn;

    Stage stage;

    Optional<User> user;
    User selected;
    public UserRepository userRepository = RepositoryFactory.getUserRepository();

    public void search(ActionEvent actionEvent) {
        String email = emailTextBox.getText();
        user = userRepository.getUserByEmail(email);
        if (user.isEmpty()) {
            System.out.println("user not found" );
            errorMSG.setVisible(true); 
            ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
            infoTextBox.setText(rs.getString("searchUser"));
            selected = null;
            setRButton(false);
        }
        else {
            selected = user.get();
            infoTextBox.setText(selected.getEmail());
            setRButton(true);
        }


    }

    public void setAdmin(ActionEvent actionEvent) {
        updateUserRole(ADMIN);
        System.out.println("admin is set");
    }

    public void setLibrarian(ActionEvent actionEvent) {
        updateUserRole(LIBRARIAN);
        System.out.println("librarian is set");
    }

    public void SetUser(ActionEvent actionEvent) {
        updateUserRole(MEMBER);
        System.out.println("user is set");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMSG.setVisible(false);
    }
    private void setRButton(Boolean x) {
        if (x) {
            errorMSG.setVisible(false);
            rButton1.setVisible(true);
            rButton2.setVisible(true);
            rButton3.setVisible(true);
            if (selected.getRole() == ADMIN) {
                rButton1.setSelected(true);
                rButton2.setSelected(false);
                rButton3.setSelected(false);
            }
            else if (selected.getRole() == LIBRARIAN) {
                rButton1.setSelected(false);
                rButton2.setSelected(true);
                rButton3.setSelected(false);
            }
            else {
                rButton1.setSelected(false);
                rButton2.setSelected(false);
                rButton3.setSelected(true);
            }
        }
        else {
            rButton1.setVisible(false);
            rButton2.setVisible(false);
            rButton3.setVisible(false);
        }

    }

    private void updateUserRole(Role x){
        selected.setRole(x);
        try {
            userRepository.updateUser(selected);
        } catch (UserRepository.UserNotFound e) {
            e.printStackTrace();
        }
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        infoTextBox.setText(rs.getString("searchUser"));
        searchBtn.setText(rs.getString("search"));
        rButton1.setText(rs.getString("adminLabel"));
        rButton2.setText(rs.getString("librarianLabel"));
        rButton3.setText(rs.getString("userLabel"));
        errorMSG.setText(rs.getString("userNotFound"));
    }
}
    