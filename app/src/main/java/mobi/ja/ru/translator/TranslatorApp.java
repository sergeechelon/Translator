package mobi.ja.ru.translator;

import android.app.Application;

import java.sql.SQLException;

import mobi.ja.ru.translator.db.DbFactory;
import mobi.ja.ru.translator.db.DbHelper;

/**
 * Created by Serg on 23.04.2017.
 */
public class TranslatorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DbFactory.setHelper(getApplicationContext());
        try {
            Config.loadConfig();
        } catch (SQLException e) {
            // как-то реагировать здесь на исключение - бессмысленно
        }
    }
    @Override
    public void onTerminate() {
        try {
            Config.saveConfig();
        } catch (SQLException e) {
            // здесь - тем более
        }
        DbFactory.releaseHelper();
        super.onTerminate();
    }
}