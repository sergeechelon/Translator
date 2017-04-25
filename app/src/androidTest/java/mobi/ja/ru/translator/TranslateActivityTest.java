package mobi.ja.ru.translator;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import mobi.ja.ru.translator.db.DbFactory;
import mobi.ja.ru.translator.db.DbHelper;

/**
 * Тест-кейс основной activity
 */
public class TranslateActivityTest extends ActivityInstrumentationTestCase2<TranslateActivity> {
    public TranslateActivityTest(Class<TranslateActivity> activityClass) {super(activityClass);}

    private TranslateActivity translateActivity;

    public TranslateActivityTest(){super(TranslateActivity.class);}

    @Override
    public void setUp() throws Exception {
        super.setUp();
        translateActivity = getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * проверка наличия языков и направлений перевода
     */
    @Test
    public void testLanguagePresence() {
        String languagesResponse = null;
        try {
            languagesResponse = YandexRequests.loadStringFromUrl(YandexRequests.LANGUAGE_QUERY);
        } catch (IOException e) {
            Assert.assertTrue("Ошибка при запросе языков", true);
        }
        assertTrue("Русский язык не найден", languagesResponse.contains("Русский"));
        assertTrue("Английский язык не найден", languagesResponse.contains("Английский"));
        assertTrue("Не найдено направление перевода с английского на русский", languagesResponse.contains("en-ru"));
        assertTrue("Не найдено направление перевода с русского на английский", languagesResponse.contains("ru-en"));
    }

    /**
     * проверка правильности перевода фразы
     */
    @Test
    public void testTranslation() {
        final String phrase2translate = "Мама мыла раму";
        translateActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                translateActivity.setPhrase(phrase2translate);
                translateActivity.makeTranslation();
            }
        });

        try {
        while(YandexRequests.isTranslationActive())
                Thread.sleep(1000,0);
            } catch (InterruptedException e) {}

        try {
            Assert.assertEquals("Ошибка перевода фразы " + phrase2translate,
                    DbFactory.getHelper().getPhraseDAO().
                            queryForPhraseAndLangTo(phrase2translate, Config.getConfig().getLangTo()).getTranslatedText(),
                    "Mom soap frame\n");

        } catch (SQLException e) {
            assertTrue("Ошибка доступа к БД", true);
        }
    }
}
