package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.fiit.jibrarian.controllers.BookModalLibrarianController.OnSuccessfulAction;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.BorrowedItem;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.User;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.AlertDialog.showDialog;

public class LibrarianTakeInController {

    private static final Logger LOGGER = Logger.getLogger(LibrarianTakeInController.class.getName());
    @FXML
    private Label availableLabel;

    @FXML
    private Label reservedLabel;

    @FXML
    private Label titleLabel;
    @FXML
    private Label borrowedLabel;

    @FXML
    private TextField readersEmail;

    @FXML
    private Label totalLabel;
    private final UserRepository userRepository = RepositoryFactory.getUserRepository();
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
        borrowedLabel.setText("Borrowed: " + item.getBorrowed().toString());
    }

    @FXML
    void closeWindow() {
        Stage stage = (Stage) availableLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void takeInButton() throws CatalogRepository.ItemNotFoundException {
        String userEmail = readersEmail.getText();
        var optUser = userRepository.getUserByEmail(userEmail);
        if (optUser.isEmpty()) {
            LOGGER.log(Level.WARNING, "Entered user doesnt exist.");
            showDialog("Entered user doesn't exist!", Alert.AlertType.ERROR);
        } else {
            User user = optUser.get();
            List<BorrowedItem> borrowedItemList = catalogRepository.getBorrowedItemsForUser(user);
            for (BorrowedItem bItem : borrowedItemList) {
                if (bItem.getItem().getId().equals(item.getId())) {
                    catalogRepository.returnItem(bItem);
                    closeWindow();
                    onSuccessfulAction.refreshData();
                    showDialog("Book " + item.getTitle() + " successfully returned from user " + userEmail + ".",
                            Alert.AlertType.INFORMATION);
                    return;
                }
            }
            showDialog("Entered user didn't lend out this book.", Alert.AlertType.ERROR);
        }
    }
}