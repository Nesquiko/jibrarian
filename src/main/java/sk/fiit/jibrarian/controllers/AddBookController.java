package sk.fiit.jibrarian.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static sk.fiit.jibrarian.AlertDialog.showDialog;
import static sk.fiit.jibrarian.App.APP_CLASSPATH;


public class AddBookController implements Initializable {
    private final String[] itemTypes = {"Not Set", "Book", "Article", "Magazine"};
    private final String[] itemGenres =
            {"Not Set", "Action and Adventure", "Classics", "Comic Book", "Detective and Mystery", "Fantasy", "Historical Fiction",//
                    "Horror", "Literary Fiction", "Romance", "Science Fiction (Sci-Fi)", "Short Stories", "Suspense and Thrillers"};
    private final CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();
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
    private Label titleLabel, authorLabel, quantityLabel, languageLabel, totalPagesLabel, descriptionLabel, isbnLabel,
            itemTypeLabel, genreLabel;
    @FXML
    private ComboBox<String> itemTypeInput;
    @FXML
    private ComboBox<String> genreInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser = setFileChooser();
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantityInput.setValueFactory(valueFactory);
        isbnLabel.setVisible(false);
        isbnInput.setVisible(false);
        setListeners();
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
    private void addBookToDB() throws CatalogRepository.ItemAlreadyExistsException, IOException {
        if (checkInputs()) {
            Item newBook = new Item();
            newBook.setAuthor(authorInput.getText());
            newBook.setId(UUID.randomUUID());
            newBook.setTitle(titleInput.getText());
            newBook.setDescription(descriptionInput.getText());
            newBook.setLanguage(languageInput.getText());
            newBook.setItemType(getItemTypeFromSelected());
            newBook.setGenre(genreInput.getSelectionModel().getSelectedItem());
            newBook.setAvailable(quantityInput.getValue());
            newBook.setReserved(0);
            newBook.setTotal(quantityInput.getValue());
            newBook.setPages(Integer.valueOf(totalPagesInput.getText()));

            byte[] imageBytes = Files.readAllBytes(chosenImageFile.toPath());
            newBook.setImage(imageBytes);
            catalogRepository.saveItem(newBook);
            clearFields();

            ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
            showDialog(rs.getString("bookAdded"), INFORMATION);
        }
    }

    private FileChooser setFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Book Cover Image");
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("JPG,JPEG,PNG", "*.jpg", "*.jpeg", "*.png"));
        return fileChooser;
    }

    private ItemType getItemTypeFromSelected() {
        return switch (itemTypeInput.getSelectionModel().getSelectedItem()) {
            case "Magazine" -> ItemType.MAGAZINE;
            case "Article" -> ItemType.ARTICLE;
            default -> ItemType.BOOK;
        };
    }

    private boolean checkInputs() {
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        if (titleInput.getText().isEmpty() || authorInput.getText().isEmpty() || totalPagesInput.getText()
                .isEmpty() || (Integer.parseInt(totalPagesInput.getText()) < 1 || Integer.parseInt(
                totalPagesInput.getText()) > 9999) || languageInput.getText().isEmpty() || descriptionInput.getText()
                .isEmpty() || genreInput.getSelectionModel().getSelectedItem().equals("Not Set")) {
            showDialog(rs.getString("fillAllFields"), ERROR);
            return false;
        } else if (chosenImageFile == null) {
            showDialog(rs.getString("chooseImage"), ERROR);
            return false;
        }
        return true;
    }

    private void setListeners() {
        setTextFieldInputListener(titleInput, titleLabel);
        setTextFieldInputListener(authorInput, authorLabel);
        setTextFieldInputListener(languageInput, languageLabel);
        setTextFieldInputListener(totalPagesInput, totalPagesLabel);
        setTextFieldInputListener(isbnInput, isbnLabel);
        setTextAreaInputListener(descriptionInput, descriptionLabel);
        setComboBox(genreInput, itemGenres);
        setComboBox(itemTypeInput, itemTypes);

        itemTypeInput.setOnAction(actionEvent -> {
            var selectedItem = itemTypeInput.getSelectionModel().getSelectedItem();
            if (selectedItem.equals("Book")) {
                isbnLabel.setVisible(true);
                isbnInput.setVisible(true);
            } else {
                isbnLabel.setVisible(false);
                isbnInput.setVisible(false);
            }
            if (selectedItem.equals("Not Set")) {
                itemTypeLabel.setTextFill(Color.RED);
            } else {
                itemTypeLabel.setTextFill(Color.BLACK);
            }
        });
        genreInput.setOnAction(actionEvent -> {
            var selectedItem = genreInput.getSelectionModel().getSelectedItem();
            if (selectedItem.equals("Not Set")) {
                genreLabel.setTextFill(Color.RED);
            } else {
                genreLabel.setTextFill(Color.BLACK);
            }
        });
        // force the field to be numeric only
        totalPagesInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                totalPagesInput.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }

    private void setTextAreaInputListener(TextArea textArea, Label textLabel) {
        textArea.textProperty().addListener((observable) -> {
            if (textArea.getText().isEmpty()) {
                textLabel.setTextFill(Color.RED);
            } else {
                textLabel.setTextFill(Color.BLACK);
            }
        });
    }

    private void setTextFieldInputListener(TextField textField, Label textLabel) {
        textField.textProperty().addListener((observable) -> {
            if (textField.getText().isEmpty()) {
                textLabel.setTextFill(Color.RED);
            } else {
                textLabel.setTextFill(Color.BLACK);
            }
        });
    }

    private void setComboBox(ComboBox cBox, String[] arrayList) {
        ObservableList<String> list = FXCollections.observableArrayList(arrayList);
        cBox.setItems(list);
        cBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void clearFields() {
        titleInput.clear();
        authorInput.clear();
        languageInput.clear();
        descriptionInput.clear();
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantityInput.setValueFactory(valueFactory);
        isbnInput.clear();
        itemTypeInput.getSelectionModel().selectFirst();
        genreInput.getSelectionModel().selectFirst();
        totalPagesInput.clear();
        Image img =
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(APP_CLASSPATH + "/views/choose.png")));
        chosenBookImage.setImage(img);
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        titleLabel.setText(rs.getString("title"));
        authorLabel.setText(rs.getString("author"));
        quantityLabel.setText(rs.getString("quantity"));
        languageLabel.setText(rs.getString("language"));
        totalPagesLabel.setText(rs.getString("totalPages"));
        descriptionLabel.setText(rs.getString("description"));
        isbnLabel.setText(rs.getString("isbn"));
        itemTypeLabel.setText(rs.getString("itemType"));
        genreLabel.setText(rs.getString("genre"));
        addBookButton.setText(rs.getString("addBookBtn"));
        cancelButton.setText(rs.getString("cancelBtn"));
    }

}
