package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.UtilAuth;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.App.APP_CLASSPATH;

public class AdminScreenController implements Initializable {

    @FXML
    private BorderPane bp;
    @FXML
    private ToggleButton libBtn, addBookBtn, dashboardBtn, logoutBtn;
    @FXML
    private Label email;

    private FXMLLoader loader;

    public Label getEmail() {
        return email;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadScreenPart(APP_CLASSPATH + "/views/library_catalog_screen.fxml");
        libBtn.setSelected(true);
        libBtn.setDisable(true);
    }

    @FXML
    public void library(ActionEvent actionEvent) {
        loadScreenPart(APP_CLASSPATH + "/views/library_catalog_screen.fxml");
        libBtn.setDisable(true);
        addBookBtn.setDisable(false);
        dashboardBtn.setDisable(false);
        addBookBtn.setSelected(false);
        dashboardBtn.setSelected(false);
    }

    @FXML
    public void addBook(ActionEvent actionEvent) {
        loadScreenPart(APP_CLASSPATH + "/views/librarian_add_book_screen.fxml");
        AddBookController controller = loader.getController();
        controller.switchLocals();
        addBookBtn.setDisable(true);
        libBtn.setDisable(false);
        dashboardBtn.setDisable(false);
        libBtn.setSelected(false);
        dashboardBtn.setSelected(false);
    }

    @FXML
    public void adminDashboard(ActionEvent actionEvent) {
        loadScreenPart(APP_CLASSPATH + "/views/admin_screen_list.fxml");
        AdminScreenListController controller = loader.getController();
        controller.switchLocals();
        dashboardBtn.setDisable(true);
        libBtn.setDisable(false);
        addBookBtn.setDisable(false);
        libBtn.setSelected(false);
        addBookBtn.setSelected(false);
    }

    @FXML
    public void exit() throws IOException {
        App.setRoot("views/Login");
        LoginController controller = App.getLoader().getController();
        controller.switchLocals();
        App.minimizeScreen();
        Logger.getLogger(LoginController.class.getName()).log(Level.INFO, "User logged out");
    }

    private void loadScreenPart(String part) {
        Parent root = null;
        try {
            URL fxmlLocation = getClass().getResource(part);
            loader = new FXMLLoader(fxmlLocation);
            root = loader.load();

        } catch (IOException error) {
            Logger.getLogger(LibrarianScreenController.class.getName()).log(Level.SEVERE, null, error);
        }

        bp.setCenter(root);
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        libBtn.setText(rs.getString("libBtn"));
        dashboardBtn.setText(rs.getString("dashboardBtn"));
        addBookBtn.setText(rs.getString("addBookBtn"));
        logoutBtn.setText(rs.getString("logout"));
        getEmail().setText(UtilAuth.getEmail());
    }
}
