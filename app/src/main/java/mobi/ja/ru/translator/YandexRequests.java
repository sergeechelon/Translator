package mobi.ja.ru.translator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import mobi.ja.ru.translator.db.DbFactory;
import mobi.ja.ru.translator.db.DbHelper;
import mobi.ja.ru.translator.db.PhraseDAO;
import mobi.ja.ru.translator.db.PhraseWithTranslation;

/**
 * Created by Serg on 21.04.2017.
 */
public class YandexRequests {
    public static final String KEY = "key=trnsl.1.1.20170421T125127Z.8a4e09e245ab7cb6.9852a28c47a73fc84475ef04a843f1f12fdd89cf";
    public static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

    public static final String LANGUAGE_QUERY = BASE_URL + "getLangs?" + KEY + "&ui=ru";

    // TODO 4Delete
    public static final String DETECT_LANGUAGE_QUERY = BASE_URL + "detect?"+ KEY + "&text=%s";

    public static final String TRANSLATE_QUERY = BASE_URL + "translate?" + KEY
            + "&text=%s&lang=%s";

    private static boolean translationActive = false;
    public static boolean isTranslationActive() {return translationActive;}
    public static void setTranslationActive(boolean translationActive) {
        YandexRequests.translationActive = translationActive;}

    public static void loadLanguages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = loadStringFromUrl(LANGUAGE_QUERY);
                    JSONObject langs = new JSONObject(response);

                    Config.getConfig().initDirs(langs.getJSONArray("dirs"));
                    Config.getConfig().initLangs(langs.getJSONObject("langs"));

                    TranslateActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Ok", "Languages loaded");
                            TranslateActivity.getInstance().enable();
                        }
                    });
                } catch (IOException e) {
                    Utils.MessageDialog("Ошибка связи",
                            "Ошибка при попытке соединения с translate.yandex.net", TranslateActivity.getInstance(),
                            Utils.exitRunnable);
                } catch (JSONException e) {
                    Utils.MessageDialog("Ошибка связи",
                            "Ошибка при расшифровке ответа с translate.yandex.net", TranslateActivity.getInstance(),
                            Utils.exitRunnable);
                }
            }
        }).start();
    }

    public static void translate(final String phrase) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    translationActive = true;
                    Config config = Config.getConfig();
                    PhraseDAO phraseDAO = DbFactory.getHelper().getPhraseDAO();
                    String langFrom, langTo = Config.getConfig().getLangFrom();
                    PhraseWithTranslation cachedPhrase = phraseDAO.queryForPhraseAndLangTo(phrase, langTo);
                    final String translatedString;
                    if(cachedPhrase == null) {
                        String url = String.format(TRANSLATE_QUERY,
                                URLEncoder.encode(phrase, "UTF-8"), config.getLangTo());
                        String response = loadStringFromUrl(url);
                        JSONObject jsonResponse = new JSONObject(response);
                        String dir = jsonResponse.getString("lang");
                        langFrom = dir.substring(0, 2);
                        JSONArray translated = jsonResponse.getJSONArray("text");
                        StringBuilder translatedBuilder = new StringBuilder();
                        for(int i=0 ;i<translated.length(); i++)
                            translatedBuilder.append(
                                    translated.getString(i)).append("\n");
                        translatedString = translatedBuilder.toString();
                        cachedPhrase = new PhraseWithTranslation(phrase, translatedString, langFrom, langTo);
                        phraseDAO.create(cachedPhrase);
                    } else {
                        Log.e("Found", cachedPhrase.getPhrase() + " .... " + cachedPhrase.getLangTo());
                        langFrom = cachedPhrase.getLangFrom();
                        langTo = cachedPhrase.getLangTo();
                        translatedString = cachedPhrase.getTranslatedText();
                    }

                    config.setLangFrom(langFrom);
                    config.setLangTo(langTo);

                    TranslateActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TranslateActivity.getInstance().setTranslatedText(translatedString.toString());
                            TranslateActivity.getInstance().setTranslationFinished(true);
                        }
                    });
                } catch (IOException | JSONException e) {
                    Utils.MessageDialog("Ошибка связи", "Ошибка получения данных с сайта yandex",
                            TranslateActivity.getInstance(), Utils.nullRunnable);
                } catch (SQLException e) {
                    Utils.MessageDialog("Ошибка данных", "Ошибка при работе с внутренней БД приложения",
                            TranslateActivity.getInstance(), Utils.nullRunnable);
                }
                /*
                catch(RuntimeException ex) {
                    Utils.MessageDialog("Ошибка", "Ошибка времени выполнения",
                            TranslateActivity.getInstance(), Utils.nullRunnable);
                }*/
                setTranslationActive(false);
            }
        }).start();
    }

    // TODO может быть, и не нужно
    protected static String identifyLanguage(String phrase) {
        try {
            String url = String.format(DETECT_LANGUAGE_QUERY,
                    URLEncoder.encode(phrase, "UTF-8"));
            String response = loadStringFromUrl(url);
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("lang");
        } catch (IOException | JSONException e) {
            Utils.MessageDialog("Ошибка связи", "Ошибка получения данных с сайта yandex",
                    TranslateActivity.getInstance(), Utils.nullRunnable);
        }
        return "en";
    }

    /**
     * Процедура GET запроса по url и вывод тела ответа в строку
     * @param url - запрашиваемый адрес
     * @return - строка-ответ
     * @throws IOException - если ответ не 200, содержит код HTTP-ответа
     */
    private static String loadStringFromUrl(String url) throws IOException {
        URL getLangsUrl = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) getLangsUrl.openConnection();
        connection.connect();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException(String.valueOf(connection.getResponseCode()));
        return Utils.convertStreamToString(connection.getInputStream());
    }
}
