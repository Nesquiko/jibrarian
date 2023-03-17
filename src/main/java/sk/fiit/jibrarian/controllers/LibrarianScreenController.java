package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import sk.fiit.jibrarian.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibrarianScreenController implements Initializable {

    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;
    @FXML
    private ToggleButton lib_btn, add_book_btn, borrow_btn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadScreenPart("../views/library_catalog_screen.fxml");

        lib_btn.setSelected(true);
        lib_btn.setDisable(true);
    }

    @FXML
    public void library(ActionEvent actionEvent) {
        loadScreenPart("../views/library_catalog_screen.fxml");
        lib_btn.setDisable(true);
        add_book_btn.setDisable(false);
        borrow_btn.setDisable(false);
        add_book_btn.setSelected(false);
        borrow_btn.setSelected(false);

    }

    @FXML
    public void add_book(ActionEvent actionEvent) {
        loadScreenPart("../views/librarian_add_book_screen.fxml");
        add_book_btn.setDisable(true);
        lib_btn.setDisable(false);
        borrow_btn.setDisable(false);
        lib_btn.setSelected(false);
        borrow_btn.setSelected(false);
    }
    @FXML
    public void borrowed_books(ActionEvent actionEvent) {
        loadScreenPart("../views/librarian_borrowed_books_screen.fxml");
        borrow_btn.setDisable(true);
        lib_btn.setDisable(false);
        add_book_btn.setDisable(false);
        lib_btn.setSelected(false);
        add_book_btn.setSelected(false);
    }

    @FXML
    public void exit() throws IOException {
        App.setRoot("views/user_auth");
    }

    private void loadScreenPart(String part){
        Parent root = null;
        try {
            URL fxmlLocation = getClass().getResource(part);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            root = loader.load();

        }catch (IOException error){
            Logger.getLogger(LibrarianScreenController.class.getName()).log(Level.SEVERE, null, error);
        }

        bp.setCenter(root);

    }

}
