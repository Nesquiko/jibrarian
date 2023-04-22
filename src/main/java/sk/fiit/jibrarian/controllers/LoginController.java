package sk.fiit.jibrarian.controllers;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import sk.fiit.jibrarian.App;
import sk.fiit.jibrarian.UtilAuth;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.data.RepositoryFactory;
import sk.fiit.jibrarian.model.Role;

public class LoginController {

    @FXML
    private Label label;

    @FXML
    private ImageView enLocal;

    @FXML
    private ImageView skLocal;

    @FXML
    private TextField email;

    @FXML
    private Label errorMsg;
    
    private int lastErrorMsg;

    @FXML
    private Label forgotPassword;

    @FXML
    private Button logInButton;

    @FXML
    private PasswordField password;

    @FXML
    private CheckBox rememberMe;

    @FXML
    private Button signUpButton;

    private UserRepository userRepo = RepositoryFactory.getUserRepository();

    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

    public UserRepository getRepo() {
        return userRepo;
    }
    
    public Logger getLog() {
        return LoginController.LOG;
    }

    public void switchToUserScreen() throws IOException {
        getLog().info("Opening user interface");
        App.setRoot("views/user_screen");
        App.maximizeScreen();
        UserScreenController controller = App.getLoader().getController();
        controller.switchLocals();
        
    }

    public void switchToLibrarianScreen() throws IOException {
        getLog().info("Opening librarian interface");
        App.setRoot("views/librarian_screen");
        App.maximizeScreen();
        LibrarianScreenController controller = App.getLoader().getController();
        controller.switchLocals();
    }

    public void switchToAdminScreen() throws IOException {
        getLog().info("Opening admin interface");
        App.setRoot("views/admin_screen");
        App.maximizeScreen();
        AdminScreenController controller = App.getLoader().getController();
        controller.switchLocals();
    }

    @FXML
    void logIn(ActionEvent event) throws SQLException, IOException, ConnectException {	//user login
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle()); //localization
        //getting user
        var user = getRepo().getUserByEmail(this.email.getText());

        //If there are no users with this email
        if (user.isPresent() == false) {
            setErrorMsg(rs.getString("incorrect_login"));
            getLog().info("User was not found");
            this.email.setText("");
            this.password.setText("");
            return;
        }
        //if passwords match
        else if (UtilAuth.comparePassword(this.password.getText(), user.get().getPassHash()) == false) {
            setErrorMsg(rs.getString("incorrect_login"));
            getLog().info("User entered incorrect password");
            this.email.setText("");
            this.password.setText("");
            return;
        }
        
        //opening the proper UI depending on users role
        Role userRole = user.get().getRole();
        getRepo().saveCurrentlyLoggedInUser(user.get());
        if (userRole == Role.MEMBER) switchToUserScreen();
        else if (userRole == Role.LIBRARIAN) switchToLibrarianScreen();
        else if (userRole == Role.ADMIN) switchToAdminScreen();
        else {
            setErrorMsg(rs.getString("error"));
            getLog().severe("Role not found");
        }

    }

    @FXML
    void signUp(ActionEvent event) throws IOException { //redirect to sign up window
        getLog().info("Opening registration interface");
        App.setRoot("views/SignUp");
        SignupController controller = App.getLoader().getController();
        controller.switchLocals();
    }
    
    @FXML
    void redirectForgotPassword(MouseEvent event) {  //redirect to forgot password window
    	//redirect to forgot password
    }
    
    public void setLastErrorMsg(int id) {
    	this.lastErrorMsg = id;
    }
    
    public int getLastErrorMsg() {
    	return lastErrorMsg;
    }
    
    public void setErrorMsgNull() {
        this.errorMsg.setText(" ");
        this.errorMsg.setTextFill(Color.web("#ff0000"));
    }
    
    public void setErrorMsgColor(String color) {
    	this.errorMsg.setTextFill(Color.web(color));
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
        switchLocals();
    }

    @FXML
    void switchToSK(MouseEvent event) {  //switch local to slovak
        getLog().info("Setting local to SK");
        Locale.setDefault(App.getSk());
        switchLocals();
    }

    public void switchLocals() { //switch labels from local change
        ResourceBundle rs = ResourceBundle.getBundle(App.getResourceBundle());
        label.setText(rs.getString("login"));
        password.setPromptText(rs.getString("password"));
        logInButton.setText(rs.getString("login"));
        signUpButton.setText(rs.getString("signup"));
    }
}
