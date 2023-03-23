package sk.fiit.jibrarian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.RepositoryFactory.EnvironmentSetupException;

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
        return fxmlLoader.load();
    }

    public static void maximizeScreen() { // Method Maximizes Screen
        Stage stage = (Stage) scene.getWindow();
        stage.setMaximized(true);
    }

    public static void main(String[] args) {
        try {
            RepositoryFactory.initializeEnvironment();
        } catch (EnvironmentSetupException e) {
            System.out.println("Failed to initialize environment: " + e.getMessage());
            System.exit(1);
        }

        launch();
    }
}