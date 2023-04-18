package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.model.BorrowedItem;
import sk.fiit.jibrarian.model.Item;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BorrowedItemCatalogController {
    @FXML
    public Label borrowedUntilLabel;
    @FXML
    public Label bookAuthor;
    @FXML
    public Label bookTitle;
    @FXML
    public ImageView bookImg;

    private Item item;

    private BorrowedItem borrowedItem;

    public void setData(Item item, BorrowedItem borrowedItem) throws IOException {
        this.item = item;
        this.borrowedItem = borrowedItem;

        bookAuthor.setText(item.getAuthor());
        bookTitle.setText(item.getTitle());

        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        borrowedUntilLabel.setText(rs.getString("borrowedUntil") + ": " + borrowedItem.getUntil().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        Image image = new Image(is);
        bookImg.setImage(image);
    }
}
