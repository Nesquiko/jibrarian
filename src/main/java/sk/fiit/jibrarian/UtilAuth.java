package sk.fiit.jibrarian;

import java.util.logging.Level;
import java.util.logging.Logger;
import at.favre.lib.crypto.bcrypt.BCrypt;
import sk.fiit.jibrarian.data.RepositoryFactory;

public class UtilAuth {

    public static boolean emailValidityCheck(String email) {
        String emailRegex = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}\\.[a-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean comparePassword(String thisPass, String userPass) {
        return BCrypt.verifyer().verify(thisPass.toCharArray(), userPass).verified;
    }

    public static String getEmail() {
        var user = RepositoryFactory.getUserRepository().getCurrentlyLoggedInUser();
        if (user.isEmpty()) {
            Logger.getLogger(UtilAuth.class.getName()).log(Level.SEVERE, "Currently logged in user object was not found");
            return "";
        }
        else
            return user.get().getEmail(); 
    }

}
