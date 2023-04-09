package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import sk.fiit.jibrarian.AlertDialog;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookModalUserController {
    @FXML
    public Label description;
    @FXML
    public Button reserveButton;
    @FXML
    public Label reserveLabel;
    @FXML
    private Label bookAvailable;
    @FXML
    private ImageView bookImg;
    @FXML
    private Label bookTitle;
    private Item item;

    private User user;

    public ReservationRepository reservationRepository = RepositoryFactory.getReservationRepository();

    private UserRepository userRepository = RepositoryFactory.getUserRepository();
    private static final Logger LOGGER = Logger.getLogger(BookModalUserController.class.getName());

    @FunctionalInterface
    public interface OnSuccessfulReserve {
        void refreshData();
    }

    private OnSuccessfulReserve onSuccessfulReserve;

    public void onReserveButtonClicked() {
        Reservation reservation = new Reservation(UUID.randomUUID(), user.getId(), item, LocalDate.now().plusDays(2), null);

        try{
            Item updatedItem = reservationRepository.saveReservation(reservation);

            item.setReserved(updatedItem.getReserved());
            item.setAvailable(updatedItem.getAvailable());
            bookAvailable.setText("Available: " + updatedItem.getAvailable().toString());
            onSuccessfulReserve.refreshData();

            reserveLabel.setText("Rezervácia prebehla úspešne");
            reserveLabel.setStyle("-fx-text-fill: green");
        }
        catch (ReservationRepository.TooManyReservationsException e){
            AlertDialog.showDialog("prilis vela rezervacii");
        } catch (CatalogRepository.ItemNotAvailableException e) {
            AlertDialog.showDialog("nie je dostupna");
        }


    }

    public void setData(Item item, OnSuccessfulReserve onSuccessfulReserve) {
        var optUser = userRepository.getCurrentlyLoggedInUser();
        if (optUser.isEmpty()) {
            LOGGER.log(Level.SEVERE, "User is not logged in");
            return;
        }
        user = optUser.get();
        this.item = item;
        this.onSuccessfulReserve = onSuccessfulReserve;

        bookTitle.setText(item.getTitle());
        description.setText(item.getDescription());
        bookAvailable.setText("Available: " + item.getAvailable().toString());

        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        Image image = new Image(is);
        bookImg.setImage(image);

    }


}
