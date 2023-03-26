package sk.fiit.jibrarian;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertDialog {

    public static void showDialog(String message){
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    public static void showDialog(String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

}
