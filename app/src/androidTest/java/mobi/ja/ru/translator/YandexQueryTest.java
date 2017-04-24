package mobi.ja.ru.translator;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import dalvik.annotation.TestTargetClass;

/**
 * Тест простых запросов к yandex.
 */
public class YandexQueryTest {
    @Test
    public static void main(String[] args) {
        try {
            String result = YandexRequests.loadStringFromUrl(YandexRequests.LANGUAGE_QUERY);
            Assert.assertEquals("Не найден английский язык", result.contains("Английский"), true);
            Assert.assertEquals("Не найден русский язык", result.contains("Русский"), true);
        } catch (IOException e) {
            Assert.assertTrue(true);
        }
    }
}
