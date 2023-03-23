package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.model.Item;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static sk.fiit.jibrarian.controllers.UserAuthController.user;


public class LibraryCatalogController implements Initializable {
    @FXML
    private GridPane libraryCatalog;


    public CatalogRepository catalogRepository = RepositoryFactory.getCatalogRepository();

    private List<Item> getData() throws CatalogRepository.ItemAlreadyExistsException, IOException, URISyntaxException {
        Item item;
        /*
        for (int i = 0; i < 5; i++) {
            item = new Item();
            item.setAuthor("Miroslav Toorovic");
            item.setId(UUID.randomUUID());
            item.setTitle("JavaFX Ultra Pro Course");
            item.setDescription("ALELELEEELE");
            item.setLanguage("EN");
            item.setItemType(ItemType.BOOK);
            item.setGenre("Course");
            item.setAvailable(5);
            item.setReserved(1);
            item.setTotal(6);
            item.setPages(100);
            URI uri = getClass().getResource("../views/book.png").toURI();
            Path path = Path.of(uri);
            byte[] imageBytes = Files.readAllBytes(path);
            item.setImage(imageBytes);
            catalogRepository.saveItem(item);
        }*/
        return catalogRepository.getItemPage(0, 12);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Item> books;
        try {
            books = getData();
        } catch (CatalogRepository.ItemAlreadyExistsException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        int column = 0;
        int row = 0;
        try {
            for (int i = 0; i < books.size(); i++) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/catalog_item.fxml"));
                AnchorPane anchorPane = loader.load();

                Item item = books.get(i);
                ItemController itemController = loader.getController();
                itemController.setData(item);


                if (column == 3) {
                    column = 0;
                    row++;
                }
                libraryCatalog.add(anchorPane, column++, row);

                anchorPane.setOnMouseClicked(mouseEvent -> {
                    String viewName = "";
                    String role = user.getRole().toString();
                    switch (role) {
                        case ("MEMBER") -> viewName = "../views/book_modal_user.fxml";
                        case ("LIBRARIAN") -> viewName = "../views/book_modal_librarian.fxml";
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
                        case ("MEMBER") -> {
                            BookModalUserController bookModalUserController =
                                    fxmlLoader.getController();  //ziskame BookModal controller cez fxmlLoader
                            bookModalUserController.setData(
                                    item); //posleme data do BookModal controllera, ktory je vlastne v novom okne
                        }
                        case ("LIBRARIAN") -> {
                            BookModalLibrarianController bookModalLibrarianController = fxmlLoader.getController();
                            bookModalLibrarianController.setData(item);
                        }
                    }
                    stage.show();
                });


            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
