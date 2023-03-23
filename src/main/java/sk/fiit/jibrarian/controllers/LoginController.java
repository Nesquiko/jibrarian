package sk.fiit.jibrarian.controllers;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;
import at.favre.lib.crypto.bcrypt.BCrypt;
import java.util.Random;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.impl.PostgresUserRepository;
import sk.fiit.jibrarian.model.Role;

public class LoginController {

    @FXML
    private TextField Email;

    @FXML
    private Label ErrorMsg;
    
    private int LastErrorMsg;

    @FXML
    private Label ForgotPassword;

    @FXML
    private Button LogInButton;

    @FXML
    private PasswordField Password;

    @FXML
    private CheckBox RememberMe;

    @FXML
    private Button SignUpButton;

    private static final Logger log = Logger.getLogger(LoginController.class.getName());
    
    public Logger getLog() {
        return LoginController.log;
    }

    public void switchToUserScreen() throws IOException {
        getLog().info("Opening user interface");
        App.setRoot("views/user_screen");
        App.maximizeScreen();
    }

    public void switchToLibrarianScreen() throws IOException {
        getLog().info("Opening librarian interface");
        App.setRoot("views/librarian_screen");
        App.maximizeScreen();
    }

    public void switchToAdminScreen() throws IOException {
        getLog().info("Opening admin interface");
        App.setRoot("views/admin_screen");
        App.maximizeScreen();
    }

    @FXML
    void LogIn(ActionEvent event) throws SQLException, IOException, ConnectException {	//user login
        //connecting to db
        ConnectionPool connectionPool;
        try {
            connectionPool = new ConnectionPool.ConnectionPoolBuilder().setHost("localhost").setPort(42069)
        .setDatabase("jibrarian").setUser("jibrarian").setPassword("password").build();
        } catch (PSQLException error) {
            setErrorMsg("Could not connect to DB");
            getLog().severe("Could not connect to DB\n" + error);
            return;
        }
        PostgresUserRepository db = new PostgresUserRepository(connectionPool);
        var user = db.getUserByEmail(this.Email.getText());
        connectionPool.close();

        //If there are no users with this email
        if (user.isPresent() == false) {
            setErrorMsg("Incorrect email or password...");
            getLog().info("User was not found");
            return;
        }
        //if passwords match
        else if (BCrypt.verifyer().verify(this.Password.getText().toCharArray(), user.get().getPassHash()).verified == false) {
            setErrorMsg("Incorrect email or password...");
            getLog().info("User entered incorrect password");
            return;
        }
        
        //opening the proper UI depending on users role
        Role userRole = user.get().getRole();
        if (userRole == Role.MEMBER) switchToUserScreen();
        else if (userRole == Role.LIBRARIAN) switchToLibrarianScreen();
        else if (userRole == Role.ADMIN) switchToAdminScreen();
        else {
            setErrorMsg("Something went wrong...");
            getLog().severe("Role not found");
        }

    }

    @FXML
    void SignUp(ActionEvent event) throws IOException { //redirect to sign up window
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/SignUp.fxml"));
        Parent root = (Parent) loader.load();
        Stage current_stage = (Stage) SignUpButton.getScene().getWindow();
        current_stage.close();

        Stage sign_up = new Stage();
        Image icon = new Image(getClass().getResourceAsStream("../views/icon.png"));
        sign_up.getIcons().add(icon);
        sign_up.setScene(new Scene(root));
        sign_up.setResizable(false);
        sign_up.show();
    }
    
    @FXML
    void RedirectForgotPassword(MouseEvent event) {  //redirect to forgot password window
    	//redirect to forgot password
    }
    
    public void setLastErrorMsg(int id) {
    	this.LastErrorMsg = id;
    }
    
    public int getLastErrorMsg() {
    	return LastErrorMsg;
    }
    
    public void setErrorMsgNull() {
        this.ErrorMsg.setText(" ");
        this.ErrorMsg.setTextFill(Color.web("#ff0000"));
    }
    
    public void setErrorMsgColor(String color) {
    	this.ErrorMsg.setTextFill(Color.web(color));
    }
    
    public void setErrorMsg(String info) {
    	int id = new Random().nextInt(1000000);
    	this.setLastErrorMsg(id);
    	this.ErrorMsg.setText(info);
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

}
