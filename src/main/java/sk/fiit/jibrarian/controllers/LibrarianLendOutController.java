package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.fiit.jibrarian.controllers.BookModalLibrarianController.OnSuccessfulAction;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.AlertDialog.showDialog;

public class LibrarianLendOutController {
    private static final Logger LOGGER = Logger.getLogger(LibraryCatalogController.class.getName());
    @FXML
    private Label availableLabel;
    @FXML
    private TextField readersEmail;

    @FXML
    private Label reservedLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label totalLabel;
    private final UserRepository userRepository = RepositoryFactory.getUserRepository();
    private final ReservationRepository reservationRepository = RepositoryFactory.getReservationRepository();
    private final CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();
    private Item item;


    private OnSuccessfulAction onSuccessfulAction;

    public void setData(Item item, OnSuccessfulAction onSuccessfulAction) {
        this.item = item;
        this.onSuccessfulAction = onSuccessfulAction;
        titleLabel.setText(item.getTitle());
        availableLabel.setText("Available: " + item.getAvailable().toString());
        reservedLabel.setText("Reserved: " + item.getReserved().toString());
        totalLabel.setText("Total: " + item.getTotal().toString());
    }

    @FXML
    void closeWindow() {
        Stage stage = (Stage) availableLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void lendOut() throws CatalogRepository.ItemNotAvailableException {
        String userEmail = readersEmail.getText();
        var optUser = userRepository.getUserByEmail(userEmail);
        if (optUser.isEmpty()) {
            LOGGER.log(Level.WARNING, "Entered user doesn't exist.");
            showDialog("Entered user doesn't exist!", Alert.AlertType.ERROR);

        } else {
            User user = optUser.get();
            List<Reservation> userReservations = reservationRepository.getReservationsForUser(user);
            for (Reservation reservation : userReservations) {
                if (reservation.getItem().getId().equals(item.getId())) {
                    LocalDate until = (LocalDate.now()).plusDays(14);
                    reservationRepository.deleteReservation(reservation);
                    catalogRepository.lendItem(item, user, until);
                    onSuccessfulAction.refreshData();
                    closeWindow();
                    showDialog("Book " + item.getTitle() + " successfully lent out to user " + userEmail + ".",
                            Alert.AlertType.INFORMATION);
                    return;
                }
            }
            showDialog("Entered user didn't reserve this book.", Alert.AlertType.ERROR);
        }
    }
}
