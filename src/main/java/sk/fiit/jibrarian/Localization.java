package sk.fiit.jibrarian;

import java.util.Locale;

public class Localization {
    private static String STRING_RESOURCE = "sk.fiit.jibrarian.localization.default";

    private static final Locale LOCALE_SK = new Locale("sk", "SK");

    public static void setLocal(String local) {
       Localization.STRING_RESOURCE = local;
    }

    public static String getLocal() {
        return Localization.STRING_RESOURCE;
    }

    public static Locale getLocaleSk() {
        return Localization.LOCALE_SK;
    } 
}
