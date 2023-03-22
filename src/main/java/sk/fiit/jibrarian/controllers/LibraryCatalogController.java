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
import sk.fiit.jibrarian.data.impl.InMemoryCatalogRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static sk.fiit.jibrarian.controllers.UserAuthController.user;


public class LibraryCatalogController implements Initializable {
    @FXML
    private GridPane libraryCatalog;


    public CatalogRepository inMemoryCatalogRepository = new InMemoryCatalogRepository();

    private List<Item> getData() throws CatalogRepository.ItemAlreadyExistsException {
        Item item;
        for (int i = 0; i < 12; i++) {
            item = new Item();
            item.setAuthor("Lev Nikolajevic Tolstoy");
            item.setId(UUID.randomUUID());
            item.setTitle("War and Peace");
            item.setDescription("hocico");
            item.setLanguage("EN");
            item.setItemType(ItemType.BOOK);
            item.setGenre("Genre");
            item.setAvailable(5);
            item.setReserved(1);
            item.setTotal(6);
            item.setPages(100);
            inMemoryCatalogRepository.saveItem(item);
        }
        return inMemoryCatalogRepository.getItemPage(0, 12);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Item> books;
        try {
            books = getData();
        } catch (CatalogRepository.ItemAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
        int column = 0;
        int row = 0;
        try {
            for (int i = 0; i < 12; i++) {
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
