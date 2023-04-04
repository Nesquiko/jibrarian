package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.fiit.jibrarian.model.Item;

import java.io.IOException;

public class BookModalLibrarianController {
    @FXML
    public Label description;
    @FXML
    public Button giveOutButton;
    @FXML
    public Button takeInButton;
    @FXML
    public Button editButton;
    @FXML
    private Label bookAvailable;
    @FXML
    private ImageView bookImg;
    @FXML
    private Label bookReserved;
    @FXML
    private Label bookTitle;
    @FXML
    private Label bookTotal;
    private Item item;

    private Stage stage;


    public void setData(Item item) {
        this.item = item;
        bookTitle.setText(item.getTitle());
        description.setText(item.getDescription());
        bookAvailable.setText("Available: " + item.getAvailable().toString());
        bookReserved.setText("Reserved: " + item.getReserved().toString());
        bookTotal.setText("Total: " + item.getTotal().toString());
        Image img = new Image(getClass().getResourceAsStream("../views/book.png"));
        bookImg.setImage(img);
    }

    @FXML
    public void takeIn(){
        var viewName = "../views/librarian_take_in.fxml";
        FXMLLoader fxmlLoader = showScreen(viewName);
        LibrarianTakeInController takeInController = fxmlLoader.getController();
        takeInController.setData(item);
    }

    @FXML
    public void giveOut(){
        var viewName = "../views/librarian_give_out.fxml";
        FXMLLoader fxmlLoader = showScreen(viewName);
        LibrarianGiveOutController giveOutController = fxmlLoader.getController();
        giveOutController.setData(item);
    }
    @FXML
    public void editItem(){
        var viewName = "../views/librarian_give_out.fxml";
        showScreen(viewName);
    }

    public FXMLLoader showScreen(String viewName){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewName));
        Parent root;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        return fxmlLoader;
    }

}
