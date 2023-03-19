package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;

import java.awt.print.Book;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class LibraryCatalogController implements Initializable {
    @FXML
    private GridPane libraryCatalog;
    public List<Item> itemList = new ArrayList<>();

    private List<Item> getData() {
        List<Item> itemList = new ArrayList<>();
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
            itemList.add(item);
        }
        return itemList;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemList.addAll(getData());
        int column = 0;
        int row = 0;
        try {
            for (int i = 0; i < 12; i++) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/catalog_item.fxml"));
                AnchorPane anchorPane = loader.load();
                ItemController itemController = loader.getController();
                Item item = itemList.get(i);
                itemController.setData(item);
                if (column == 3) {
                    column = 0;
                    row++;
                }
                libraryCatalog.add(anchorPane, column++, row);

                anchorPane.setOnMouseClicked(mouseEvent -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/book_modal.fxml"));
                    Parent root;

                    Node node = (Node) mouseEvent.getSource();
                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL); //toto zabrani klikat na ine miesta v aplikacii, pokym sa nezavrie toto okno
                    BookModalController bookModalController = fxmlLoader.getController();  //ziskame BookModal controller cez fxmlLoader
                    bookModalController.setData(item); //posleme data do BookModal controllera, ktory je vlastne v novom okne
                    stage.show();

                });


            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
