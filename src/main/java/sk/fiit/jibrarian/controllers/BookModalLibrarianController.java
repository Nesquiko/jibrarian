package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.model.Item;

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


}
