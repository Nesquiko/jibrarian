package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.App.APP_CLASSPATH;

public class AdminScreenListController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(AdminScreenListController.class.getName());
    private final UserRepository userRepository = RepositoryFactory.getUserRepository();
    @FXML
    private VBox listOfUsers = null;
    @FXML
    private Label roleLabel;
    @FXML
    RadioButton LibrarianLabel, UserLabel, AdminLabel;
    @FXML
    private Button modifyBtn, refreshBtn;

    private int selected = 2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadList();
    }

    private List<User> getData() {
        if (selected == 1) {
            return userRepository.getAllAdmins();
        } else if (selected == 2) {
            return userRepository.getAllLibrarians();
        } else {
            LOGGER.severe("Error in AdminScreenListController.getData()");
            return Collections.emptyList();
        }

    }

    public void loadList() {
        listOfUsers.getChildren().clear();
        List<User> users = getData();
        for (User user : users) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(APP_CLASSPATH + "/views/admin_screen_user.fxml"));
                AnchorPane anchorPane = loader.load();
                AdminScreenUserController adminScreenUserController = loader.getController();
                adminScreenUserController.setData(user, this);
                adminScreenUserController.switchLocals();
                listOfUsers.getChildren().add(anchorPane);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error in AdminScreenListController.loadList()", e);
                e.printStackTrace();
            }
        }
    }

    public void addClick(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource(APP_CLASSPATH + "/views/add_librarian_modal_admin.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
            AddLibrarianModalAdminController controller = fxmlLoader.getController();
            controller.switchLocals();
            controller.setInfo(this);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in AdminScreenListController.addClick()", e);
            return;
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void refreshClick(ActionEvent actionEvent) {
        loadList();
    }

    public void getAdmin(ActionEvent actionEvent) {
        selected = 1;
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        roleLabel.setText(rs.getString("admins"));
        loadList();
    }

    public void getLibrarian(ActionEvent actionEvent) {
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        roleLabel.setText(rs.getString("librarians"));
        selected = 2;
        loadList();
    }

    public void getUser(ActionEvent actionEvent) {
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        roleLabel.setText(rs.getString("users"));
        selected = 3;
        loadList();
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        if (roleLabel != null) roleLabel.setText(rs.getString("librarians"));
        if (AdminLabel != null) AdminLabel.setText(rs.getString("adminLabel"));
        if (LibrarianLabel != null) LibrarianLabel.setText(rs.getString("librarianLabel"));
        if (UserLabel != null) UserLabel.setText(rs.getString("userLabel"));
        if (modifyBtn != null) modifyBtn.setText(rs.getString("modifyBtn"));
        if (refreshBtn != null) refreshBtn.setText(rs.getString("refreshBtn"));
    }
}
