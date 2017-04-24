package mobi.ja.ru.translator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import mobi.ja.ru.translator.Config;

/**
 * класс, реализующий доступ к внутренней БД. Используется библиотека ORMLite.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME ="translator.db";

    private static final int DATABASE_VERSION = 1;

    private PhraseDAO phraseDao = null;
    private ConfigDAO configDao = null;

    public DbHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, PhraseWithTranslation.class);
            TableUtils.createTable(connectionSource, Config.class);
        }
        catch (SQLException e){
            Log.e("БД", "Ошибка создания БД" + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
            //одна версия, поэтому ничего не делаем
    }

    public PhraseDAO getPhraseDAO() throws java.sql.SQLException{
        if(phraseDao == null){
            phraseDao = new PhraseDAO(getConnectionSource(), PhraseWithTranslation.class){
            };
        }
        return phraseDao;
    }
    public ConfigDAO getConfigDAO() throws java.sql.SQLException{
        if(configDao == null){
            configDao = new ConfigDAO(getConnectionSource(), Config.class){
            };
        }
        return configDao;
    }
    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
        phraseDao = null;
        configDao = null;
    }
}