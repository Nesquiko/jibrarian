package sk.fiit.jibrarian.controllers;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.App;
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

        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        bookAvailable.setText(rs.getString("available") + ": " + item.getAvailable().toString());
        bookReserved.setText(rs.getString("reserved") + ": " + item.getReserved().toString());
        bookTotal.setText(rs.getString("total") + ": " + item.getTotal().toString());
        Image img = new Image(getClass().getResourceAsStream("../views/book.png"));
        bookImg.setImage(img);
    }


}
