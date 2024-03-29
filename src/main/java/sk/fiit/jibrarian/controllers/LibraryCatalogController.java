package sk.fiit.jibrarian.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.App.APP_CLASSPATH;


public class LibraryCatalogController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(LibraryCatalogController.class.getName());
    @FXML
    private GridPane libraryCatalog;
    @FXML
    private Label catalogPageLabel;
    @FXML
    private Button leftArrowBtn;
    @FXML
    private Button rightArrowBtn;
    private Integer currentPage = 0;

    private User user;

    private final UserRepository userRepository = RepositoryFactory.getUserRepository();
    private final CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();

    private final Integer totalPages = (int) Math.floor(catalogRepository.getItemPage(0, 12).total() / 12.01);

    private List<Item> getData(Integer page) {
        return catalogRepository.getItemPage(page, 12).items();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var optUser = userRepository.getCurrentlyLoggedInUser();
        if (optUser.isEmpty()) {
            LOGGER.log(Level.SEVERE, "User is not logged in");
            return;
        }
        user = optUser.get();

        getCatalogPage(currentPage);
        catalogPageLabel.setText(String.valueOf(currentPage + 1));
    }

    public void getCatalogPage(Integer page) {
        List<Item> books = getData(page);

        int column = 0;
        int row = 0;
        libraryCatalog.getChildren().clear();
        rightArrowBtn.setVisible(!Objects.equals(currentPage, totalPages));
        leftArrowBtn.setVisible(currentPage != 0);
        try {
            for (Item book : books) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(APP_CLASSPATH + "/views/catalog_item.fxml"));
                AnchorPane anchorPane = loader.load();

                ItemController itemController = loader.getController();
                itemController.setData(book, user);

                if (column == 3) {
                    column = 0;
                    row++;
                }
                libraryCatalog.add(anchorPane, column++, row);

                anchorPane.setOnMouseClicked(mouseEvent -> { //zobrazenie modalu po kliknuti na knihu
                    String viewName = "";
                    Role role = user.getRole();
                    switch (role) {
                        case MEMBER -> viewName = APP_CLASSPATH + "/views/book_modal_user.fxml";
                        case LIBRARIAN -> viewName = APP_CLASSPATH + "/views/book_modal_librarian.fxml";
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewName));
                    Parent root;

                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Failed to load view", e);
                        return;
                    }
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    //toto zabrani klikat na ine miesta v aplikacii, pokym sa nezavrie toto okno
                    stage.initModality(Modality.APPLICATION_MODAL);
                    switch (role) {
                        case MEMBER -> {
                            BookModalUserController bookModalUserController =
                                    fxmlLoader.getController();  //ziskame BookModal controller cez fxmlLoader
                            bookModalUserController.setData(book, () -> getCatalogPage(
                                    currentPage)); //posleme data do BookModal controllera, ktory je vlastne v novom okne
                        }
                        case LIBRARIAN -> {
                            BookModalLibrarianController bookModalLibrarianController = fxmlLoader.getController();
                            bookModalLibrarianController.setData(book, () -> getCatalogPage(currentPage));
                        }
                    }
                    stage.show();
                });
            }

        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load view", exception);
        }
    }

    @FXML
    private void pageIncrement() {
        TranslateTransition tt = new TranslateTransition();
        AtomicBoolean played = new AtomicBoolean(false);
        tt.setDuration(Duration.millis(300));
        tt.setByX(-1920);
        tt.setNode(libraryCatalog);
        tt.setAutoReverse(true);
        tt.setOnFinished(v -> {
            if (!played.get()) {
                getCatalogPage(++currentPage);
                catalogPageLabel.setText(String.valueOf(currentPage + 1));
                libraryCatalog.setTranslateX(1920);
                played.set(true);
                tt.setByX(-1920);
                tt.play();
            }
        });
        tt.play();
    }

    @FXML
    private void pageDecrement() {
        TranslateTransition tt = new TranslateTransition();
        AtomicBoolean played = new AtomicBoolean(false);
        tt.setDuration(Duration.millis(400));
        tt.setByX(1920);
        tt.setNode(libraryCatalog);
        tt.setOnFinished(v -> {
            if (!played.get()) {
                getCatalogPage(--currentPage);
                catalogPageLabel.setText(String.valueOf(currentPage + 1));
                played.set(true);
                libraryCatalog.setTranslateX(-1920);
                tt.setByX(1920);
                tt.play();
            }
        });
        tt.play();
    }
}
