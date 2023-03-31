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
        scene = new Scene(loadFXML("views/Login"), 800, 600);
        //Image icon = new Image(getClass().getResourceAsStream("../views/icon.png"));
        //stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setResizable(false);
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