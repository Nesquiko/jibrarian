package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.model.Item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import static sk.fiit.jibrarian.AlertDialog.confirmationDialog;
import static sk.fiit.jibrarian.AlertDialog.showDialog;


public class BookModalLibrarianController {
    private final CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();
    @FXML
    public Label description;
    @FXML
    public Button giveOutButton;
    @FXML
    public Button takeInButton;
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

    @FunctionalInterface
    public interface OnSuccessfulAction {
        void refreshData();
    }

    private OnSuccessfulAction onSuccessfulAction;


    public void setData(Item item, OnSuccessfulAction onSuccessfulAction) {
        this.item = item;
        bookTitle.setText(item.getTitle());
        description.setText(item.getDescription());

        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        bookAvailable.setText(rs.getString("available") + ": " + item.getAvailable().toString());
        bookReserved.setText(rs.getString("reserved") + ": " + item.getReserved().toString());
        bookTotal.setText(rs.getString("total") + ": " + item.getTotal().toString());

        byte[] byteArrayImage = item.getImage();
        InputStream is = new ByteArrayInputStream(byteArrayImage);
        Image image = new Image(is);
        bookImg.setImage(image);
        this.onSuccessfulAction = onSuccessfulAction;
    }

    @FXML
    public void takeIn() {
        var viewName = "../views/librarian_take_in.fxml";
        FXMLLoader fxmlLoader = showScreen(viewName);
        LibrarianTakeInController takeInController = fxmlLoader.getController();
        takeInController.setData(item, onSuccessfulAction);
        closeWindow();
    }

    @FXML
    public void giveOut() {
        var viewName = "../views/librarian_lend_out.fxml";
        FXMLLoader fxmlLoader = showScreen(viewName);
        LibrarianLendOutController giveOutController = fxmlLoader.getController();
        giveOutController.setData(item, onSuccessfulAction);
        closeWindow();
    }

    @FXML
    public void deleteItem() throws CatalogRepository.ItemIsBorrowedException, CatalogRepository.ItemNotFoundException {
        boolean delete = confirmationDialog("Are you sure you want to delete this book?", Alert.AlertType.CONFIRMATION);
        if (delete) {
            catalogRepository.deleteItem(item);
            onSuccessfulAction.refreshData();
            showDialog("Item successfully deleted.", Alert.AlertType.CONFIRMATION);
        }
    }

    @FXML
    void closeWindow() {
        Stage stage = (Stage) takeInButton.getScene().getWindow();
        stage.close();
    }

    public FXMLLoader showScreen(String viewName) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewName));
        Parent root;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        return fxmlLoader;
    }

}
