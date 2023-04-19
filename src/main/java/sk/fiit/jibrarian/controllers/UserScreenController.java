package sk.fiit.jibrarian.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import sk.fiit.jibrarian.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserScreenController implements Initializable {

    @FXML
    private BorderPane bp;
    private FXMLLoader loader;
    @FXML
    private ToggleButton libBtn, borrowBtn;
    private boolean playedFirstTime = false;

    private String lastPart;

    private Parent root;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        libBtn.setSelected(true);
        libBtn.setDisable(true);
        loadScreenPart("../views/library_catalog_screen.fxml");
    }

    @FXML
    public void library() {
        loadScreenPart("../views/library_catalog_screen.fxml");
        libBtn.setDisable(true);
        borrowBtn.setDisable(false);
        borrowBtn.setSelected(false);
    }

    @FXML
    public void borrowed_books() {
        loadScreenPart("../views/borrowed_books.fxml");
        borrowBtn.setDisable(true);
        libBtn.setDisable(false);
        libBtn.setSelected(false);
    }

    @FXML
    public void exit() throws IOException {
        App.setRoot("views/user_auth");
    }

    private void loadScreenPart(String part) {
        if(playedFirstTime){
            int swipe = -2000;
            if (lastPart != null) {
                if (swipeLeft()) {
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
            tt.setOnFinished(v ->{
                if(!played.get()) {
                    root = null;
                    played.set(true);
                    try {
                        URL fxmlLocation = getClass().getResource(part);
                        loader = new FXMLLoader(fxmlLocation);
                        root = loader.load();

                    } catch (IOException error) {
                        Logger.getLogger(LibrarianScreenController.class.getName()).log(Level.SEVERE, null, error);
                    }
                    bp.setCenter(root);
                    root.setTranslateX(finalSwipe *(-1));
                    tt.setNode(root);
                    tt.setByX(finalSwipe);
                    tt.play();
                }
            });
            tt.play();
        }else{
            playedFirstTime = true;
            root = null;
            try {
                URL fxmlLocation = getClass().getResource(part);
                loader = new FXMLLoader(fxmlLocation);
                root = loader.load();

            } catch (IOException error) {
                Logger.getLogger(LibrarianScreenController.class.getName()).log(Level.SEVERE, null, error);
            }
            bp.setCenter(root);
        }
    }

    private boolean swipeLeft() {
        return switch (lastPart) {
            case "../views/borrowed_books.fxml" -> true;
            default -> false;
        };
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        libBtn.setText(rs.getString("libBtn"));
        borrowBtn.setText(rs.getString("borrowBtn"));
    }

}
