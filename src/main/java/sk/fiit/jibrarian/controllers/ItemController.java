package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;



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

    public void setData(Item item, User user) throws IOException {
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        this.item = item;
        bookAuthor.setText(item.getAuthor());
        bookTitle.setText(item.getTitle());

        if (user.getRole().equals(Role.MEMBER)){
            bookAvailable.setText(rs.getString("available") + ": " + item.getAvailable().toString());

            bookReserved.setVisible(false);
            bookTotal.setVisible(false);
        }
        else {
            bookAvailable.setText(rs.getString("available") + ": " + item.getAvailable().toString());
            bookReserved.setText(rs.getString("reserved") + ": " + item.getReserved().toString());
            bookTotal.setText(rs.getString("total") + ": " + item.getTotal().toString());
        }

        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        Image image = new Image(is);
        bookImg.setImage(image);
    }
}