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
 * Класс со статическими методами, содержащими запросы к yandex
 */
public class YandexRequests {
    public static final String KEY = "key=trnsl.1.1.20170421T125127Z.8a4e09e245ab7cb6.9852a28c47a73fc84475ef04a843f1f12fdd89cf";
    public static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

    public static final String LANGUAGE_QUERY = BASE_URL + "getLangs?" + KEY + "&ui=ru";

    public static final String TRANSLATE_QUERY = BASE_URL + "translate?" + KEY
            + "&text=%s&lang=%s";

    private static boolean translationActive = false;
    public static boolean isTranslationActive() {return translationActive;}
    public static void setTranslationActive(boolean translationActive) {
        YandexRequests.translationActive = translationActive;}

    /**
     * запрос на получение списка языков и направлений перевода
     */
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

    /**
     * Запрос перевода текста. Перед запросом ищет такую же фразу в кэше (находится в локальной БД),
     * и если находит, запрос не делает.
     * @param phrase - фраза для перевода
     */
    public static void translate(final String phrase) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    translationActive = true;
                    Config config = Config.getConfig();
                    PhraseDAO phraseDAO = DbFactory.getHelper().getPhraseDAO();
                    String langFrom, langTo = Config.getConfig().getLangTo();

                    // ищем в кэше соответствующую фразу и направление перевода
                    PhraseWithTranslation cachedPhrase = phraseDAO.queryForPhraseAndLangTo(phrase, langTo);
                    final String translatedString;
                    if(cachedPhrase == null) {
                        // Если в кэше отсутствует, делаем запрос
                        String url = String.format(TRANSLATE_QUERY,
                                URLEncoder.encode(phrase, "UTF-8"), config.getLangTo());
                        String response = loadStringFromUrl(url);
                        // и парсим его

                        JSONObject jsonResponse = new JSONObject(response);
                        String dir = jsonResponse.getString("lang");
                        langFrom = dir.substring(0, 2);
                        JSONArray translated = jsonResponse.getJSONArray("text");

                        // формируем ответ, если он не из одного варианта
                        StringBuilder translatedBuilder = new StringBuilder();
                        for(int i=0 ;i<translated.length(); i++)
                            translatedBuilder.append(
                                    translated.getString(i)).append("\n");
                        translatedString = translatedBuilder.toString();
                        cachedPhrase = new PhraseWithTranslation(phrase, translatedString, langFrom, langTo);

                        // результат сохраняем в кэше
                        phraseDAO.create(cachedPhrase);
                    } else {
                        Log.e("Found", cachedPhrase.getPhrase() + " .... " + cachedPhrase.getLangTo());
                        langFrom = cachedPhrase.getLangFrom();
                        langTo = cachedPhrase.getLangTo();
                        translatedString = cachedPhrase.getTranslatedText();
                    }

                    // обновляем направление перевода в конфигурации
                    config.setLangFrom(langFrom);
                    config.setLangTo(langTo);

                    // выводим результат в активити перевода
                    TranslateActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TranslateActivity.getInstance().setTranslatedText(translatedString.toString());
                            TranslateActivity.getInstance().setTranslationFinished(true);
                            TranslateActivity.getInstance().updateTranslateButtonText();
                        }
                    });
                } catch (IOException | JSONException e) {
                    String errMsg;
                    if(e.getMessage().contentEquals("404"))
                        errMsg = "Превышено суточное ограничение на объем переведенного текста";
                    else if(e.getMessage().contentEquals("413"))
                        errMsg = "Превышен максимально допустимый размер текста";
                    else if(e.getMessage().contentEquals("422"))
                        errMsg = "Текст не может быть переведен";
                    else if(e.getMessage().contentEquals("501"))
                        errMsg = "Заданное направление перевода не поддерживается";
                    else
                        errMsg = "Ошибка получения данных с сайта yandex";
                    Utils.MessageDialog("Ошибка", errMsg,
                            TranslateActivity.getInstance(), null);
                } catch (SQLException e) {
                    Log.e("SQL error", "Ошибка при работе с внутренней БД приложения при переводе фразы");
                }

                setTranslationActive(false);
            }
        }).start();
    }

    /**
     * Процедура GET запроса по url и вывод тела ответа в строку
     * @param url - запрашиваемый адрес
     * @return - строка-ответ
     * @throws IOException - если ответ не 200, содержит код HTTP-ответа
     */
    public static String loadStringFromUrl(String url) throws IOException {
        URL getLangsUrl = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) getLangsUrl.openConnection();
        connection.connect();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException(String.valueOf(connection.getResponseCode()));
        return Utils.convertStreamToString(connection.getInputStream());
    }
}
