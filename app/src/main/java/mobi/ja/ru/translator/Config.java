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
 * Cинглтон с настройками. Содержит Map-соответствие между краткими и полными названиями языков,
 * направления переводов, текущий исходный язык и текущий язык для перевода.
 */
@DatabaseTable
public class Config {
    transient private static Config instance = null;
    @DatabaseField(generatedId = true)
    private int id;
    /**
     * список направлений перевода
     */
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> dirs = null;
    /**
     * мэп между сокращенными и полными названиями языков
     */
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, String> langs = null;
    /**
     * текущий язык, с которого осуществляется перевод. Определяется автоматически.
     */
    @DatabaseField
    private String langFrom = "en";
    /**
     * текущий язык, на который осуществляется перевод. Определяется пользователем.
     */
    @DatabaseField
    private String langTo = "en";

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

    /**
     * получить полное название языка, на который переводить
     * @return
     */
    public String getLangNameTo() {
        if(langs == null)
            return "English";
        return langs.get(langTo);
    }
    /**
     * получить полное название языка, с которого переводить
     * @return
     */

    public String getLangNameFrom() {
        if(langs == null)
            return "English";
        return langs.get(langFrom);
    }

    /**
     * инициализация по запросу на /getLangs
     */

    public void initDirs(JSONArray dirs) throws JSONException {
        int len = dirs.length();
        this.dirs = new ArrayList<>(len);
        for(int i=0; i<len; i++)
            this.dirs.add(dirs.getString(i));
    }

    /**
     * инициализация по запросу на /getLangs
     */
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
