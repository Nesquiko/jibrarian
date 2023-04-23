package sk.fiit.jibrarian.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.UtilAuth;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.data.UserRepository.AlreadyExistingUserException;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;

public class SignupController {

    @FXML
    private Label label;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField passwordConfirm;

    @FXML
    private Button signUpButton;

    @FXML
    private Label errorMsg;

    private int lastErrorMsg;

    private final UserRepository userRepo = RepositoryFactory.getUserRepository();

    private static final Logger LOG = Logger.getLogger(SignupController.class.getName());

    public UserRepository getRepo() {
        return userRepo;
    }

    public Logger getLog() {
        return SignupController.LOG;
    }

    @FXML
    void cancel(ActionEvent event) throws IOException { //loads back the login stage
        App.setRoot("views/Login");
        getLog().info("Opening login interface");
        LoginController controller = App.getLoader().getController();
        controller.switchLocals();
    }

    @FXML
    void signUp(ActionEvent event) { //sign up to the application
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle()); //localization
        // sign up checking for formats & lengths
        if (email.getText().length() < 8) {
            setErrorMsgColor("#FF0000");
            setErrorMsg(rs.getString("invalid_email_length"));
            getLog().warning("Invalid email address");
            return;
        } else if (!UtilAuth.emailValidityCheck(email.getText())) {
            setErrorMsgColor("#FF0000");
            setErrorMsg(rs.getString("invalid_email_format"));
            getLog().warning("Invalid email address");
            return;
        } else if (password.getText().length() < 8) {
            setErrorMsgColor("#FF0000");
            setErrorMsg(rs.getString("invalid_password_length"));
            getLog().warning("Invalid password");
            return;
        } else if (password.getText().compareTo(passwordConfirm.getText()) != 0) {
            setErrorMsgColor("#FF0000");
            setErrorMsg(rs.getString("confirm_password_failure"));
            getLog().warning("Passwords do not match");
            return;
        }

        //pushing new member into database
        String hashedPass = UtilAuth.hashPassword(this.password.getText());
        User user = new User(UUID.randomUUID(), this.email.getText(), hashedPass, Role.MEMBER);
        try {
            getRepo().saveUser(user);
        } catch (AlreadyExistingUserException error) {
            setErrorMsgColor("#FF0000");
            setErrorMsg(rs.getString("user_exists"));
            getLog().warning("User with this email already exists");
            return;
        }
        getLog().info("User successfully created");
        setErrorMsgColor("#00c900");
        setErrorMsg(rs.getString("signed_in"));
    }

    public void setErrorMsgColor(String color) {
        this.errorMsg.setTextFill(Color.web(color));
    }

    public void setLastErrorMsg(int id) {
        this.lastErrorMsg = id;
    }

    public int getLastErrorMsg() {
        return this.lastErrorMsg;
    }

    public void setErrorMsgNull() {
        this.errorMsg.setText(" ");
    }

    public void setErrorMsg(String info) {
        int id = new Random().nextInt(1000000);
        this.setLastErrorMsg(id);
        this.errorMsg.setText(info);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (id == getLastErrorMsg()) setErrorMsgNull();
                });
            }
        };
        timer.schedule(task, 3000);
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        label.setText(rs.getString("signup"));
        password.setPromptText(rs.getString("password"));
        passwordConfirm.setPromptText(rs.getString("confirm_password"));
        cancelButton.setText(rs.getString("login"));
        signUpButton.setText(rs.getString("signup"));
    }
}
