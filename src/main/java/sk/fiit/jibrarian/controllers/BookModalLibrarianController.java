package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.fiit.jibrarian.AlertDialog;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.AlertDialog.confirmationDialog;
import static sk.fiit.jibrarian.AlertDialog.showDialog;


public class BookModalLibrarianController {
    private static final Logger LOGGER = Logger.getLogger(BookModalUserController.class.getName());
    private final CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();
    public ReservationRepository reservationRepository = RepositoryFactory.getReservationRepository();
    private final UserRepository userRepository = RepositoryFactory.getUserRepository();
    @FXML
    private Label bookAvailable;

    @FXML
    private ImageView bookImg;

    @FXML
    private Label bookReserved;
    @FXML
    private Label bookBorrowed;

    @FXML
    private Label bookTitle;

    @FXML
    private Label bookTotal;

    @FXML
    private Label description;

    @FXML
    private Label reserveLabel;
    private Item item;

    private User user;

    @FunctionalInterface
    public interface OnSuccessfulAction {
        void refreshData();
    }

    private OnSuccessfulAction onSuccessfulAction;


    public void setData(Item item, OnSuccessfulAction onSuccessfulAction) {
        this.item = item;
        this.onSuccessfulAction = onSuccessfulAction;
        var optUser = userRepository.getCurrentlyLoggedInUser();
        if (optUser.isEmpty()) {
            LOGGER.log(Level.SEVERE, "User is not logged in");
            return;
        }
        user = optUser.get();
        bookTitle.setText(item.getTitle());
        description.setText(item.getDescription());

        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        bookAvailable.setText(rs.getString("available") + ": " + item.getAvailable().toString());
        bookReserved.setText(rs.getString("reserved") + ": " + item.getReserved().toString());
        bookBorrowed.setText(rs.getString("borrowed") + ": " + item.getBorrowed().toString());
        bookTotal.setText(rs.getString("total") + ": " + item.getTotal().toString());

        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        Image image = new Image(is);
        bookImg.setImage(image);

    }

    @FXML
    public void takeIn() {
        var viewName = "../views/librarian_take_in.fxml";
        FXMLLoader fxmlLoader = showScreen(viewName, "Take in book Screen");
        LibrarianTakeInController takeInController = fxmlLoader.getController();
        takeInController.setData(item, onSuccessfulAction);
        closeWindow();
    }

    @FXML
    public void giveOut() {
        var viewName = "../views/librarian_lend_out.fxml";
        FXMLLoader fxmlLoader = showScreen(viewName, "Lend Out book Screen");
        LibrarianLendOutController giveOutController = fxmlLoader.getController();
        giveOutController.setData(item, onSuccessfulAction);
        closeWindow();
    }

    @FXML
    public void deleteItem() {
        boolean delete = confirmationDialog("Are you sure you want to delete this book?", Alert.AlertType.CONFIRMATION);
        if (delete) {
            try {
                catalogRepository.deleteItem(item);
                onSuccessfulAction.refreshData();
                closeWindow();
                showDialog("Item successfully deleted.", Alert.AlertType.CONFIRMATION);
            } catch (CatalogRepository.ItemNotFoundException e) {
                throw new RuntimeException(e);
            } catch (CatalogRepository.ItemIsBorrowedException e) {
                showDialog("Item is borrowed and couldn't be deleted.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void reserveBookForLibrarian() {
        Reservation
                reservation = new Reservation(UUID.randomUUID(), user.getId(), item, LocalDate.now().plusDays(2), null);
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());

        try {
            Item updatedItem = reservationRepository.saveReservation(reservation);

            bookAvailable.setText(rs.getString("available") + ": " + updatedItem.getAvailable().toString());
            bookReserved.setText(rs.getString("reserved") + ": " + updatedItem.getReserved().toString());
            bookBorrowed.setText(rs.getString("borrowed") + ": " + updatedItem.getBorrowed().toString());
            bookTotal.setText(rs.getString("total") + ": " + updatedItem.getTotal().toString());

            onSuccessfulAction.refreshData();
            reserveLabel.setText(rs.getString("confirmReservation"));
            reserveLabel.setStyle("-fx-text-fill: green");
        } catch (ReservationRepository.TooManyReservationsException e) {
            AlertDialog.showDialog(rs.getString("tooManyReservations"));
        } catch (CatalogRepository.ItemNotAvailableException e) {
            AlertDialog.showDialog(rs.getString("notAvailable"));
        } catch (ReservationRepository.ItemAlreadyReservedException e) {
            AlertDialog.showDialog(rs.getString("sameReservationError"));
        }
    }

    @FXML
    void closeWindow() {
        Stage stage = (Stage) reserveLabel.getScene().getWindow();
        stage.close();
    }

    public FXMLLoader showScreen(String viewName, String title) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewName));
        Parent root;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        return fxmlLoader;
    }

}
