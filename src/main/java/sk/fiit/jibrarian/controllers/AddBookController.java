package sk.fiit.jibrarian.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddBookController implements Initializable {
    @FXML
    private Button addBookButton;

    @FXML
    private TextField authorInput;

    @FXML
    private Button cancelButton;

    @FXML
    private ImageView chosenBookImage;

    @FXML
    private TextArea descriptionInput;

    @FXML
    private TextField languageInput;

    @FXML
    private Spinner<Integer> quantityInput;

    @FXML
    private TextField titleInput;
    private Image image;

    private FileChooser fileChooser;
    private File chosenImageFile;
    @FXML
    private TextField totalPagesInput;
    @FXML
    private TextField isbnInput;
    @FXML
    private Label isbnLabel;
    @FXML
    private ComboBox<String> genreInput;
    private String[] itemTypes = { "Book", "Article", "Magazine" };
    public CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser = setFileChooser();
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantityInput.setValueFactory(valueFactory);
    }

    @FXML
    private void chooseImage() {
        Stage stage = (Stage) (addBookButton.getScene()).getWindow();
        chosenImageFile = fileChooser.showOpenDialog(stage);
        if (chosenImageFile != null) {
            image = new Image(String.valueOf(chosenImageFile));
            chosenBookImage.setImage(image);
        }
    }

    @FXML
    private void addBookToDB() throws CatalogRepository.ItemAlreadyExistsException, IOException, URISyntaxException {
        if (titleInput.getText().isEmpty() || authorInput.getText().isEmpty() || quantityInput.getValue()
                .equals(0) || languageInput.getText().isEmpty() || descriptionInput.getText().isEmpty()) {
            showDialog("FILL ALL FIELDS!!!");
        } else {
            Item newBook = new Item();
            newBook.setAuthor(authorInput.getText());
            newBook.setId(UUID.randomUUID());
            newBook.setTitle(titleInput.getText());
            newBook.setDescription(descriptionInput.getText());
            newBook.setLanguage(languageInput.getText());
            newBook.setItemType(ItemType.BOOK);
            newBook.setGenre("unknown");
            newBook.setAvailable(quantityInput.getValue());
            newBook.setReserved(0);
            newBook.setTotal(quantityInput.getValue());
            newBook.setPages(100);

            byte[] imageBytes = Files.readAllBytes(chosenImageFile.toPath());
            newBook.setImage(imageBytes);
            catalogRepository.saveItem(newBook);
            showDialog("ADDED BOOK SUCCESSFULLY");
            clearFields();
        }
    }

    private FileChooser setFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Book Cover Image");
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("JPG,JPEG,PNG", "*.jpg", "*.jpeg", "*.png"));
        return fileChooser;
    }

    private void showDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    @FXML
    private void clearFields() {
        titleInput.clear();
        authorInput.clear();
        languageInput.clear();
        descriptionInput.clear();
        Image img = new Image(getClass().getResourceAsStream("../views/choose.png"));
        chosenBookImage.setImage(img);
    }

}
