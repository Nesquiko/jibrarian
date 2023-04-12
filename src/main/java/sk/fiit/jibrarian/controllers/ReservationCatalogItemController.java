package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReservationCatalogItemController {
    @FXML
    public Label reservedUntilLabel;
    @FXML
    public Label bookAuthor;
    @FXML
    public Label bookTitle;
    @FXML
    public ImageView bookImg;

    private Item item;

    private Reservation reservation;

    public void setData(Item item, Reservation reservation, User user) throws IOException {
        this.item = item;
        this.reservation = reservation;

        bookAuthor.setText(item.getAuthor());
        bookTitle.setText(item.getTitle());

        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        reservedUntilLabel.setText(rs.getString("reservedUntil") + ": " + reservation.getUntil().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        Image image = new Image(is);
        bookImg.setImage(image);
    }
}

