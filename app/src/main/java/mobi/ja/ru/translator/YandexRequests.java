package mobi.ja.ru.translator;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Serg on 21.04.2017.
 */
public class YandexRequests {
    public static final String KEY = "key=trnsl.1.1.20170421T125127Z.8a4e09e245ab7cb6.9852a28c47a73fc84475ef04a843f1f12fdd89cf";
    public static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

    public static final String LANGUAGE_QUERY = BASE_URL + "getLangs?" + KEY + "&ui=ru";

    public static final String DETECT_LANGUAGE_QUERY = BASE_URL + "detect?"+ KEY + "&text=%s";

    public static void loadLanguages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = loadStringFromUrl(LANGUAGE_QUERY);
                    JSONObject langs = new JSONObject(response);

                    Config.getConfig().setDirs(langs.getJSONArray("dirs"));
                    Config.getConfig().setLangs(langs.getJSONObject("langs"));

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
                Log.d("!!!", identifyLanguage(phrase));
            }
        }).start();
    }

    protected static String identifyLanguage(String phrase) {
        try {
            String url = String.format(DETECT_LANGUAGE_QUERY,
                    URLEncoder.encode(phrase, "UTF-8"));
            Log.d("URL", url);
            String response = loadStringFromUrl(url);
            Log.d("Response", response);
        } catch (IOException e) {
            Utils.MessageDialog("Ошибка связи", "Ошибка получения данных с сайта yandex",
                    TranslateActivity.getInstance(), Utils.nullRunnable);
        }
        return "qq";
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
