package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.model.Item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ItemController {
    @FXML
    private Label bookAuthor;
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

    public void setData(Item item) throws IOException {
        this.item = item;
        bookAuthor.setText(item.getAuthor());
        bookTitle.setText(item.getTitle());
        bookAvailable.setText("Available: " + item.getAvailable().toString());
        bookReserved.setText("Reserved: " + item.getReserved().toString());
        bookTotal.setText("Total: " + item.getTotal().toString());
        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        javafx.scene.image.Image image1 = new Image(is);
        bookImg.setImage(image1);
    }
}