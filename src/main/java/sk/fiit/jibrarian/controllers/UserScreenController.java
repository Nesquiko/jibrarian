package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import sk.fiit.jibrarian.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserScreenController implements Initializable {

    @FXML
    private BorderPane bp;
    @FXML
    private ToggleButton libBtn, borrowBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        libBtn.setSelected(true);
        libBtn.setDisable(true);
        loadScreenPart("../views/library_catalog_screen.fxml");
    }

    @FXML
    public void library(ActionEvent actionEvent) {
        loadScreenPart("../views/library_catalog_screen.fxml");
        libBtn.setDisable(true);
        borrowBtn.setDisable(false);
        borrowBtn.setSelected(false);
    }

    @FXML
    public void borrowed_books(ActionEvent actionEvent) {
        loadScreenPart("../views/borrowed_books.fxml");
        borrowBtn.setDisable(true);
        libBtn.setDisable(false);
        libBtn.setSelected(false);
    }

    @FXML
    public void exit() throws IOException {
        App.setRoot("views/user_auth");
    }

    private void loadScreenPart(String part) {
        Parent root = null;
        try {
            URL fxmlLocation = getClass().getResource(part);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            root = loader.load();


        } catch (IOException error) {
            Logger.getLogger(LibrarianScreenController.class.getName()).log(Level.SEVERE, null, error);
        }

        bp.setCenter(root);
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        libBtn.setText(rs.getString("libBtn"));
        borrowBtn.setText(rs.getString("borrowBtn"));
    }

}
