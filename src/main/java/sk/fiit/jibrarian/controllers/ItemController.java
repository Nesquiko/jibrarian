package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.model.Item;

public class ItemController {
    @FXML
    private Label book_author;
    @FXML
    private Label book_available;
    @FXML
    private ImageView book_img;
    @FXML
    private Label book_reserved;
    @FXML
    private Label book_title;
    @FXML
    private Label book_total;
    private Item item;
    public void setData(Item item){
        this.item = item;
        book_author.setText(item.getAuthor());
        book_title.setText(item.getTitle());
        book_available.setText("Available: "+item.getAvailable().toString());
        book_reserved.setText("Reserved: "+item.getReserved().toString());
        book_total.setText("Total: "+item.getTotal().toString());
        Image img = new Image(getClass().getResourceAsStream("../views/book.png"));
        book_img.setImage(img);
    }
}