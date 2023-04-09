package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BorrowedBooksController implements Initializable {
    public ReservationRepository reservationRepository = RepositoryFactory.getReservationRepository();
    @FXML
    public Label reservationsTextLabel;
    private UserRepository userRepository = RepositoryFactory.getUserRepository();
    private User user;

    @FXML
    private GridPane borrowedGrid;

    @FXML
    private GridPane reservationsGrid;

    @FXML
    private Label reservationStatusLabel;

    private static final Logger LOGGER = Logger.getLogger(BorrowedBooksController.class.getName());
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var optUser = userRepository.getCurrentlyLoggedInUser();
        if (optUser.isEmpty()) {
            LOGGER.log(Level.SEVERE, "User is not logged in");
            return;
        }
        user = optUser.get();


        List<Reservation> reservations = reservationRepository.getReservationsForUser(user);

        if (reservations.isEmpty()){
            reservationsTextLabel.setVisible(false);
            reservationStatusLabel.setText("No reservations yet");
            return;
        }

        int column = 0;
        int row = 0;
        try {
            for (Reservation reservation : reservations) {
                Item book = reservation.getItem();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/reservation_catalog_item.fxml"));
                AnchorPane anchorPane = loader.load();

                ReservationCatalogItemController reservationCatalogItemController = loader.getController();
                reservationCatalogItemController.setData(book, reservation, user);

                if (column == 1) {
                    column = 0;
                    row++;
                }
                reservationsGrid.add(anchorPane, column++, row);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
