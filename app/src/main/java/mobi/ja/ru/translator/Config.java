package mobi.ja.ru.translator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * синглтон с конфигурацией
 * Created by Serg on 21.04.2017.
 */
public class Config {
    private static Config instance = null;

    private JSONArray dirs = null;
    private JSONObject langs = null;

    private String langFrom = "en";
    private String langTo = "en";

    private Config(){
    }

    public static Config getConfig() {
        if(instance != null)
            return instance;
        return instance = new Config();
    }

    /**
     * @return - JSON массив из getLangs-запроса
     */
    public JSONArray getDirs() {
        return dirs;
    }

    /**
     * @param dirs - JSON массив из getLangs-запроса
     */
    public void setDirs(JSONArray dirs) {
        this.dirs = dirs;
    }

    /**
     * @return JSON объект lang из getLangs-запроса
     */
    public JSONObject getLangs() {
        return langs;
    }

    /**
     * @param langs - JSON объект lang из getLangs-запроса
     */
    public void setLangs(JSONObject langs) {
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
        try {
            return langs.get(langTo).toString();
        } catch (JSONException e) {}
        return "???";
    }

    public String getLangNameFrom() {
        if(langs == null)
            return "English";
        try {
            return langs.get(langFrom).toString();
        } catch (JSONException e) {}
        return "???";
    }
}
