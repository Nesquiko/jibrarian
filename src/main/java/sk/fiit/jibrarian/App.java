package sk.fiit.jibrarian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.RepositoryFactory.EnvironmentSetupException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    static {
        try (InputStream loggingProperties = App.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(loggingProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    public static final String APP_CLASSPATH = "/sk/fiit/jibrarian";
    private static final Locale LOCALE_SK = new Locale("sk", "SK");
    private static final String RESOURCE_BUNDLE = "sk.fiit.jibrarian.localization.strings";
    private static FXMLLoader loader;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("views/Login"), 800, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        loader = fxmlLoader;
        return fxmlLoader.load();
    }

    public static void maximizeScreen() { // Method Maximizes Screen
        Stage stage = (Stage) scene.getWindow();
        stage.setMaximized(true);
        stage.setResizable(true);
    }

    public static void minimizeScreen() { // Method Minimizes Screen
        Stage stage = (Stage) scene.getWindow();
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setWidth(810);
        stage.setHeight(600);
    }

    public static Locale getSk() {
        return LOCALE_SK;
    }

    public static String getResourceBundle() {
        return RESOURCE_BUNDLE;
    }

    public static FXMLLoader getLoader() {
        return loader;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        try {
            RepositoryFactory.initializeEnvironment();
        } catch (EnvironmentSetupException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize environment", e);
            System.exit(1);
        }

        launch();
    }
