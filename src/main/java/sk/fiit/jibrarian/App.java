package sk.fiit.jibrarian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.fiit.jibrarian.controllers.LibrarianScreenController;
import sk.fiit.jibrarian.controllers.UserScreenController;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("views/user_auth"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));



        Parent root = fxmlLoader.load();

        User user = new User();
        switch (fxml) {

            case ("views/user_screen") -> {
                UserScreenController userScreenController = fxmlLoader.getController();
                user.setRole(Role.MEMBER);
                userScreenController.setUser(user);
            }
            case ("views/librarian_screen") -> {
                LibrarianScreenController librarianScreenController = fxmlLoader.getController();
                user.setRole(Role.LIBRARIAN);
                librarianScreenController.setUser(user);
            }
        }

        return root;
    }

    public static void maximizeScreen() { // Method Maximizes Screen
        Stage stage = (Stage) scene.getWindow();
        stage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch();
    }

}