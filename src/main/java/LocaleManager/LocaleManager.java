package LocaleManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Управляет локалью приложения
 */
public class LocaleManager {
    private static LocaleManager instance;
    private static Locale currentLocale = new Locale("ru");
    private static ResourceBundle data = ResourceBundle.getBundle("text", currentLocale);
    private final PropertyChangeSupport propertyChangeSupport =
            new PropertyChangeSupport(this);
    private final Path localePath = Paths.get(System.getProperty("user.home"), "Ogorodnikov", "Locale.txt");
    public final static String LOCALE_CHANGED = "Locale changed";

    private LocaleManager(){}

    /**
     * Возвращает единственный экземпляр управления локалью
     */
    public static synchronized LocaleManager getInstance(){
        if (instance == null){
            instance = new LocaleManager();
        }
        return instance;
    }

    /**
     * Возвращает строку на нужном языке
     */
    public static String getString(String key){
        return data.getString(key);
    }

    /**
     * Позволяет поменять локаль
     */
    public void setLocale(Locale locale){
        String old_locale = locale.getLanguage();
        currentLocale = locale;
        data = ResourceBundle.getBundle("text", currentLocale);
        propertyChangeSupport.firePropertyChange(LOCALE_CHANGED, old_locale, currentLocale);
    }

    /**
     * Сохраняет локаль
     */
    public void saveLocale(){
        try {
            Files.createDirectories(localePath.getParent());
            Files.writeString(localePath, currentLocale.getLanguage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Загружает локаль из файла, если файла нет, то по умолчанию русский язык
     */
    public void loadLocale(){
        if (!Files.exists(localePath)) {
            return;
        }

        try {
            String localeCode = Files.readString(localePath);
            if (localeCode.isEmpty()){
                return;
            }
            Locale oldLocal = currentLocale;
            currentLocale = new Locale(localeCode);
            data = ResourceBundle.getBundle("text", currentLocale);
            propertyChangeSupport.firePropertyChange(LOCALE_CHANGED,oldLocal, currentLocale);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
