package mobi.ja.ru.translator;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.ja.ru.translator.db.DbFactory;

/**
 * синглтон с конфигурацией
 * Created by Serg on 21.04.2017.
 */
@DatabaseTable
public class Config {
    transient private static Config instance = null;
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> dirs = null;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> langs = null;

    @DatabaseField
    private String langFrom = "en";
    @DatabaseField
    private String langTo = "en";

    //private
    // потому что ORMLite, видите ли, нужен конструктор без аргументов
    public Config(){
    }

    public static Config getConfig() {
        if(instance != null)
            return instance;
        return instance = new Config();
    }

    /**
     * @return - JSON массив из getLangs-запроса
     */
    public ArrayList<String> getDirs() {
        return dirs;
    }

    /**
     * @param dirs - JSON массив из getLangs-запроса
     */
    public void setDirs(ArrayList<String> dirs) {
        this.dirs = dirs;
    }

    /**
     * @return JSON объект lang из getLangs-запроса
     */
    public HashMap<String, String> getLangs() {
        return langs;
    }

    /**
     * @param langs - JSON объект lang из getLangs-запроса
     */
    public void setLangs(HashMap<String, String> langs) {
        this.langs = langs;
    }

    /**
     * @return - с какого языка переводить
     */
    public String getLangFrom() {
        return langFrom;
    }

    /**
     * @param langFrom - - с какого языка переводить
     */
    public void setLangFrom(String langFrom) {
        this.langFrom = langFrom;
    }

    /**
     * @return - на какой язык переводить
     */
    public String getLangTo() {
        return langTo;
    }

    /**
     * @param langTo - на какой язык переводить
     */
    public void setLangTo(String langTo) {
        this.langTo = langTo;
    }

    public String getLangNameTo() {
        if(langs == null)
            return "English";
        return langs.get(langTo);
    }

    public String getLangNameFrom() {
        if(langs == null)
            return "English";
        return langs.get(langFrom);
    }

    public void initDirs(JSONArray dirs) throws JSONException {
        int len = dirs.length();
        this.dirs = new ArrayList<>(len);
        for(int i=0; i<len; i++)
            this.dirs.add(dirs.getString(i));
    }

    public void initLangs(JSONObject langs) throws JSONException {
        int len = langs.length();
        this.langs = new HashMap<>(len);
        for(int i=0; i<len; i++) {
            String shortName = langs.names().getString(i);
            this.langs.put(shortName, langs.getString(shortName));
        }
    }

    public static void loadConfig() throws SQLException {
        List<Config> configList = DbFactory.getHelper().getConfigDAO().queryForAll();
        if(configList.size() == 0) {
            instance = new Config();
            DbFactory.getHelper().getConfigDAO().create(instance);
        }
        else
            instance = configList.get(0);
    }

    public static void saveConfig() throws SQLException {
        DbFactory.getHelper().getConfigDAO().update(instance);
    }
}
