package sk.fiit.jibrarian.controllers;

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
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


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

    private Integer totalPages = (int) Math.ceil(catalogRepository.getItemPage(0, 12).total() / 12);

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

        rightArrowBtn.setVisible(currentPage != totalPages);
        leftArrowBtn.setVisible(currentPage != 0);
        try {
            for (Item book : books) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/catalog_item.fxml"));
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
                        case MEMBER -> viewName = "../views/book_modal_user.fxml";
                        case LIBRARIAN -> viewName = "../views/book_modal_librarian.fxml";
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewName));
                    Parent root;

                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.initModality(
                            Modality.APPLICATION_MODAL); //toto zabrani klikat na ine miesta v aplikacii, pokym sa nezavrie toto okno

                    switch (role) {
                        case MEMBER -> {
                            BookModalUserController bookModalUserController =
                                    fxmlLoader.getController();  //ziskame BookModal controller cez fxmlLoader
                            bookModalUserController.setData(
                                    book, () -> getCatalogPage(currentPage)); //posleme data do BookModal controllera, ktory je vlastne v novom okne
                        }
                        case LIBRARIAN -> {
                            BookModalLibrarianController bookModalLibrarianController = fxmlLoader.getController();
                            bookModalLibrarianController.setData(
                                    book, () -> getCatalogPage(currentPage));
                        }
                    }
                    stage.show();
                });


            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void pageIncrement() {
        libraryCatalog.getChildren().clear();
        getCatalogPage(++currentPage);
        catalogPageLabel.setText(String.valueOf(currentPage + 1));
    }

    @FXML
    private void pageDecrement() {
        libraryCatalog.getChildren().clear();
        getCatalogPage(--currentPage);
        catalogPageLabel.setText(String.valueOf(currentPage + 1));
    }
}
