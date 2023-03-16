package sk.fiit.jibrarian.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class LibraryCatalogController implements Initializable {
    @FXML
    private GridPane library_catalog;
    @FXML
    private HBox catalog_item;
    public List<Item> itemList = new ArrayList<>();
    private List<Item> getData(){
        List<Item> itemList = new ArrayList<>();
        Item item;
        for(int i=0; i<12; i++){
            item = new Item();
            item.setAuthor("Lev Nikolajevic Tolstoy");
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
            for (int i = 0; i< 12; i++){
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/catalog_item.fxml"));
                AnchorPane anchorPane = loader.load();
                ItemController itemController = loader.getController();
                itemController.setData(itemList.get(i));
                if(column == 3){
                    column = 0;
                    row++;
                }
                library_catalog.add(anchorPane, column++, row);
            }
        }catch (IOException exception){
            exception.printStackTrace();
        }
    }

}
