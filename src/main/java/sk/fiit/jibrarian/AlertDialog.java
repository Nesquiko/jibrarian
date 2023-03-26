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

    public static void showDialog(String message, String alertType){
        Alert.AlertType type;
        switch (alertType){
            case "INFO": type = Alert.AlertType.INFORMATION; break;
            case "CONFIRMATION": type = Alert.AlertType.CONFIRMATION; break;
            case "WARNING": type = Alert.AlertType.WARNING; break;
            case "ERROR": type = Alert.AlertType.ERROR; break;
            default: type = Alert.AlertType.NONE; break;
        }
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

}
