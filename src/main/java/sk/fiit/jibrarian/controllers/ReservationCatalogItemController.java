package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReservationCatalogItemController {
    @FXML
    private Label reservedUntilLabel;
    @FXML
    private Label bookAuthor;
    @FXML
    private Label bookTitle;
    @FXML
    private ImageView bookImg;

    public void setData(Item item, Reservation reservation) {
        bookAuthor.setText(item.getAuthor());
        bookTitle.setText(item.getTitle());

        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        reservedUntilLabel.setText(rs.getString("reservedUntil") + ": " + reservation.getUntil()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        Image image = new Image(is);
        bookImg.setImage(image);
    }
}

