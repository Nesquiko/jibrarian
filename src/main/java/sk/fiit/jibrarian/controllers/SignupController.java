package sk.fiit.jibrarian.controllers;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.UtilAuth;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.data.UserRepository.AlreadyExistingUserException;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

public class SignupController {

    @FXML
    private ImageView enLocal;

    @FXML
    private ImageView skLocal;

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

    private UserRepository userRepo = RepositoryFactory.getUserRepository();

    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

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
    }
    
    @FXML
    void signUp(ActionEvent event) throws SQLException, IOException, ConnectException { //sign up to the application 
        // sign up checking for formats & lengths
		if (email.getText().length() < 8) {
        	setErrorMsg("Email has to have at least 8 characters...");
            getLog().warning("Invalid email address");
    		return;
        }
        else if (UtilAuth.emailValidityCheck(email.getText()) == false) {
        	setErrorMsg("Invalid email format...");
            getLog().warning("Invalid email address");
    		return;
        }
        else if (password.getText().length() < 8) {
        	setErrorMsg("Password has to have at least 8 characters...");
            getLog().warning("Invalid password");
        	return;
        }
        else if (password.getText().compareTo(passwordConfirm.getText()) != 0) {
        	setErrorMsg("Passwords do not match...");
            getLog().warning("Passwords do not match");
        	return;
        }

        //pushing new member into database
        String hashedPass = UtilAuth.hashPassword(this.password.getText());
        User user = new User(UUID.randomUUID(), this.email.getText(), hashedPass, Role.MEMBER);
        try {
            getRepo().saveUser(user);
        } catch (AlreadyExistingUserException error) {
            setErrorMsg("User with this email already exists...");
            getLog().warning("User with this email already exists");
            return;
        }
        getLog().info("User successfully created");
        setErrorMsgColor("#00c900");
        setErrorMsg("You have succesfully signed in!");
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

    @FXML
    void switchToEN(MouseEvent event) {  //switch local to english
        getLog().info("Setting local to EN");
        Locale.setDefault(Locale.US);
        switchLocals("sk.fiit.jibrarian.localization.default", Locale.getDefault());
    }

    @FXML
    void switchToSK(MouseEvent event) {  //switch local to slovak
        getLog().info("Setting local to SK");
        Locale.setDefault(App.getSk());
        switchLocals("sk.fiit.jibrarian.localization.default_sk_SK", Locale.getDefault());
    }

    void switchLocals(String local, Locale locale) { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(local, locale);
        label.setText(rs.getString("signup"));
        password.setPromptText(rs.getString("password"));
        passwordConfirm.setPromptText(rs.getString("confirm_password"));
        cancelButton.setText(rs.getString("signup"));
        signUpButton.setText(rs.getString("login"));
    }
}
