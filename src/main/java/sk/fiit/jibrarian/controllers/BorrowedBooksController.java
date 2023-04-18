package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.BorrowedItem;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BorrowedBooksController implements Initializable {
    public ReservationRepository reservationRepository = RepositoryFactory.getReservationRepository();
    public CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();
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

        List<BorrowedItem> borrowedItems = catalogRepository.getBorrowedItemsForUser(user);
        List<Reservation> reservations = reservationRepository.getReservationsForUser(user);

        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        reservationsTextLabel.setText(rs.getString("reservations"));



        reservationsTextLabel.setVisible(false);
        if (reservations.isEmpty() && borrowedItems.isEmpty()) {
            reservationStatusLabel.setText(rs.getString("noReservations"));
        }

        int column = 0;
        int row = 0;
        if (!reservations.isEmpty()) {
            reservationsTextLabel.setVisible(true);
            try {
                for (Reservation reservation : reservations) {
                    if (LocalDate.now().isAfter(reservation.getUntil())) {
                        reservationRepository.deleteReservation(reservation);
                        continue;
                    }

                    Item book = reservation.getItem();

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("../views/reservation_catalog_item.fxml"));
                    AnchorPane anchorPane = loader.load();

                    ReservationCatalogItemController reservationCatalogItemController = loader.getController();
                    reservationCatalogItemController.setData(book, reservation);

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

        if (borrowedItems.isEmpty()) {
            return;
        }

        column = 0;
        row = 0;

        try {
            for (BorrowedItem borrowedItem : borrowedItems) {

                Item book = borrowedItem.getItem();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/borrowed_item_catalog.fxml"));
                AnchorPane anchorPane = loader.load();

                BorrowedItemCatalogController borrowedItemCatalogController = loader.getController();
                borrowedItemCatalogController.setData(book, borrowedItem);

                if (LocalDate.now().isAfter(borrowedItem.getUntil())) { //ked pouzivatel nevratil knihu
                    borrowedItemCatalogController.borrowedUntilLabel.setStyle("-fx-text-fill: red");
                }

                if (column == 2) {
                    column = 0;
                    row++;
                }
                borrowedGrid.add(anchorPane, column++, row);

            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }


    }
}
