package sk.fiit.jibrarian;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UtilAuth {

    public static boolean emailValidityCheck(String email) {
        String emailRegex = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean comparePassword(String thisPass, String userPass) {
        return BCrypt.verifyer().verify(thisPass.toCharArray(), userPass).verified;
    }

}
