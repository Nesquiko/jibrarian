package sk.fiit.jibrarian.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.UtilAuth;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sk.fiit.jibrarian.App.APP_CLASSPATH;


public class LibrarianScreenController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(LibrarianScreenController.class.getName());

    @FXML
    private BorderPane bp;
    @FXML
    private ToggleButton libBtn, addBookBtn, borrowBtn, logoutBtn;
    @FXML
    private Label email;

    private FXMLLoader loader;

    private boolean playedFirstTime = false;

    private String lastPart;

    private Parent root;

    public Label getEmail() {
        return email;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        libBtn.setSelected(true);
        libBtn.setDisable(true);
        loadScreenPart(APP_CLASSPATH + "/views/library_catalog_screen.fxml");
    }

    @FXML
    public void library() {
        loadScreenPart(APP_CLASSPATH + "/views/library_catalog_screen.fxml");
        libBtn.setDisable(true);
        addBookBtn.setDisable(false);
        borrowBtn.setDisable(false);
        addBookBtn.setSelected(false);
        borrowBtn.setSelected(false);

    }

    @FXML
    public void borrowedBooks() {
        loadScreenPart(APP_CLASSPATH + "/views/borrowed_books.fxml");
        borrowBtn.setDisable(true);
        libBtn.setDisable(false);
        addBookBtn.setDisable(false);
        libBtn.setSelected(false);
        addBookBtn.setSelected(false);
    }

    @FXML
    public void addBook() {
        loadScreenPart(APP_CLASSPATH + "/views/librarian_add_book_screen.fxml");
        addBookBtn.setDisable(true);
        libBtn.setDisable(false);
        borrowBtn.setDisable(false);
        libBtn.setSelected(false);
        borrowBtn.setSelected(false);
    }

    @FXML
    public void exit() throws IOException {
        App.setRoot("views/Login");
        LoginController controller = App.getLoader().getController();
        controller.switchLocals();
        App.minimizeScreen();
        Logger.getLogger(LoginController.class.getName()).log(Level.INFO, "User logged out");
    }

    private void loadScreenPart(String part) {
        if (playedFirstTime) {
            int swipe = -2000;
            if (lastPart != null) {
                if (swipeLeft(part)) {
                    swipe = swipe * (-1);
                }
            }
            this.lastPart = part;
            TranslateTransition tt = new TranslateTransition();
            AtomicBoolean played = new AtomicBoolean(false);
            tt.setDuration(Duration.millis(400));
            tt.setNode(root);
            tt.setByX(swipe);
            int finalSwipe = swipe;
            tt.setOnFinished(v -> {
                if (!played.get()) {
                    root = null;
                    played.set(true);
                    try {
                        URL fxmlLocation = getClass().getResource(part);
                        loader = new FXMLLoader(fxmlLocation);
                        root = loader.load();
                        if (part == "../views/librarian_add_book_screen.fxml") {
                            AddBookController controller = loader.getController();
                            controller.switchLocals();
                        }

                    } catch (IOException error) {
                        LOGGER.log(Level.SEVERE, "Error while loading screen part", error);
                    }
                    bp.setCenter(root);
                    root.setTranslateX(finalSwipe * (-1));
                    tt.setNode(root);
                    tt.setByX(finalSwipe);
                    tt.play();
                }
            });
            tt.play();
        } else {
            playedFirstTime = true;
            root = null;
            try {
                URL fxmlLocation = getClass().getResource(part);
                loader = new FXMLLoader(fxmlLocation);
                root = loader.load();
                if (part == "../views/librarian_add_book_screen.fxml") {
                    AddBookController controller = loader.getController();
                    controller.switchLocals();
                }

            } catch (IOException error) {
                LOGGER.log(Level.SEVERE, "Error while loading screen part", error);
            }
            bp.setCenter(root);
        }
    }

    private boolean swipeLeft(String part) {
        return switch (lastPart) {
            case APP_CLASSPATH + "/views/borrowed_books.fxml" -> true;
            case APP_CLASSPATH + "/views/librarian_add_book_screen.fxml" ->
                    part.equals(APP_CLASSPATH + "/views/library_catalog_screen.fxml");
            default -> false;
        };
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        libBtn.setText(rs.getString("libBtn"));
        borrowBtn.setText(rs.getString("borrowBtn"));
        addBookBtn.setText(rs.getString("addBookBtn"));
        logoutBtn.setText(rs.getString("logout"));
        getEmail().setText(UtilAuth.getEmail());
    }

}
