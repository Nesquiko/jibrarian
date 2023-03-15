package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void library(ActionEvent actionEvent) {
        loadScreenPart("../views/librarian_library_screen.fxml");
    }

    @FXML
    public void add_book(ActionEvent actionEvent) {
        loadScreenPart("../views/librarian_add_book_screen.fxml");
    }
    @FXML
    public void borrowed_books(ActionEvent actionEvent) {
        loadScreenPart("../views/librarian_borrowed_books_screen.fxml");
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
