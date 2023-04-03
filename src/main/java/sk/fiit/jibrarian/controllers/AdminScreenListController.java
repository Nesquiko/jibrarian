package sk.fiit.jibrarian.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;

public class AdminScreenListController implements Initializable {
    public UserRepository userRepository = RepositoryFactory.getUserRepository();
    @FXML
    private VBox listOfUsers = null;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        loadList();
    }

    private List<User> getData() {
//        User user;
//        for (int i = 0; i < 12; i++) {
//            user = new User();
//            user.setEmail("123@123.com"+i);
//            user.setId(UUID.randomUUID());
//            user.setRole(Role.LIBRARIAN);
//            user.setPassHash("");
//
//            try {
//                userRepository.saveUser(user);
//            } catch (UserRepository.AlreadyExistingUserException e) {
//                e.printStackTrace();
//            }
//       }
        return userRepository.getAllLibrarians();
    }

    private void loadList(){
        listOfUsers.getChildren().clear();
        List<User> users = getData();
        for (int i=0; i<users.size();i++){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/admin_screen_user.fxml"));
                AnchorPane anchorPane = loader.load();
                User user = users.get(i);
                AdminScreenUserController adminScreenUserController = loader.getController();
                adminScreenUserController.setData(user);
                listOfUsers.getChildren().add(anchorPane);
            } catch (IOException e) {
                e.printStackTrace();

            }

        }
    }
    public void addClick(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/add_librarian_modal_admin.fxml"));
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
    }

    public void refreshClick(ActionEvent actionEvent) {
        loadList();
    }
}
