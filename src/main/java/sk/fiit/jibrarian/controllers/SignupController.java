package sk.fiit.jibrarian.controllers;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;
import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.UserRepository.AlreadyExistingUserException;
import sk.fiit.jibrarian.data.impl.PostgresUserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

public class SignupController {

    @FXML
    private Button CancelButton;

    @FXML
    private TextField Email;

    @FXML
    private PasswordField Password;

    @FXML
    private PasswordField PasswordConfirm;

    @FXML
    private Button SignUpButton;
    
    @FXML
    private Label ErrorMsg;
    
    private int LastErrorMsg;

    private static final Logger log = Logger.getLogger(SignupController.class.getName());
    
    public Logger getLog() {
        return SignupController.log;
    }
    
    @FXML
    void Cancel(ActionEvent event) throws IOException { //loads back the login stage
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/Login.fxml"));
        Parent root = (Parent) loader.load();
        Stage current_stage = (Stage) SignUpButton.getScene().getWindow();
        current_stage.close();

        Stage log_in = new Stage();
        Image icon = new Image(getClass().getResourceAsStream("../views/icon.png"));
        log_in.getIcons().add(icon);
        log_in.setScene(new Scene(root));
        log_in.setResizable(false);
        log_in.show();
    }
    
    public boolean EmailValidityCheck(String Email){
        String EmailRegex = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
        return Email.matches(EmailRegex);
    }
    
    @FXML
    void SignUp(ActionEvent event) throws SQLException, IOException, ConnectException { //sign up to the application 
        // sign up checking for formats & lengths
		if (Email.getText().length() < 8) {
        	SetErrorMsg("Email has to have at least 8 characters...");
            getLog().warning("Invalid email address");
    		return;
        }
        else if (EmailValidityCheck(Email.getText()) == false) {
        	SetErrorMsg("Invalid email format...");
            getLog().warning("Invalid email address");
    		return;
        }
        else if (Password.getText().length() < 8) {
        	SetErrorMsg("Password has to have at least 8 characters...");
            getLog().warning("Invalid password");
        	return;
        }
        else if (Password.getText().compareTo(PasswordConfirm.getText()) != 0) {
        	SetErrorMsg("Passwords do not match...");
            getLog().warning("Passwords do not match");
        	return;
        }
        
        //connecting to db
        ConnectionPool connectionPool;
        try {
            connectionPool = new ConnectionPool.ConnectionPoolBuilder().setHost("localhost").setPort(42069)
        .setDatabase("jibrarian").setUser("jibrarian").setPassword("password").build();
        } catch (PSQLException error) {
            SetErrorMsg("Could not connect to DB...");
            getLog().severe("Could not connect to DB");
            return;
        }
        PostgresUserRepository db = new PostgresUserRepository(connectionPool);

        //pushing new member into database
        String hashedPass = BCrypt.withDefaults().hashToString(12, this.Password.getText().toCharArray());
        User user = new User(new UUID(100000, 10000000), this.Email.getText(), hashedPass, Role.MEMBER);
        try {
            db.saveUser(user);
        } catch (AlreadyExistingUserException error) {
            SetErrorMsg("User with this email already exists...");
            getLog().warning("User with this email already exists");
            return;
        }
        connectionPool.close();
        getLog().info("User successfully created");

		//successfully signed in
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/Login.fxml")); //redirecting back to the login page
        Parent root = (Parent) loader.load();
        Stage current_stage = (Stage) SignUpButton.getScene().getWindow();
        current_stage.close();
        
        LoginController Login = loader.getController();
        Login.setErrorMsgColor("#00c900");
        Login.setErrorMsg("You have succesfully signed in!");

        Stage LogIn = new Stage();
        Image icon = new Image(getClass().getResourceAsStream("../views/icon.png"));
        LogIn.getIcons().add(icon);
        LogIn.setScene(new Scene(root));
        LogIn.setResizable(false);
        LogIn.show();
    }
    
    @FXML
    void CursorHand(MouseEvent event) {
    	Scene scene = (Scene) CancelButton.getScene();
        scene.setCursor(Cursor.HAND);
    }

    @FXML
    void NormalCursor(MouseEvent event) {
    	Scene scene = (Scene) CancelButton.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }
    
    public void SetLastErrorMsg(int id) {
    	this.LastErrorMsg = id;
    }
    
    public int GetLastErrorMsg() {
    	return LastErrorMsg;
    }
    
    public void SetErrorMsgNull() {
        this.ErrorMsg.setText(" ");
    }
    
    public void SetErrorMsg(String info) {
    	int id = new Random().nextInt(1000000);
    	this.SetLastErrorMsg(id);
    	this.ErrorMsg.setText(info);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                	if (id == GetLastErrorMsg()) SetErrorMsgNull();
                });
            }
        };
        timer.schedule(task, 3000);
    }

}
