package mobi.ja.ru.translator;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import mobi.ja.ru.translator.db.DbFactory;
import mobi.ja.ru.translator.db.PhraseDAO;
import mobi.ja.ru.translator.db.PhraseWithTranslation;

/**
 * Основная activity для перевода. Содержит поле ввода, которое чере WAIT_INPUT миллисекунд
 * без изменения ввода осуществляет переводтекста.Исходный язык определяется автоматически
 * в процессе перевода (сорри, на более пространный функционал просто не хватило времени),
 * язык назначения берется из настроек (синглтон Config).
 * Также присутствуют 4 кнопки:
 * @changeLangButton - вызывает активити изменения языка перевода
 * @toFavoriteButton - добавляет текущий перевод в избранное
 * @favoriteButton - вызывает активити со списком избранного
 * @historyButton - вызывает активити со списком истории переводов
 */
public class TranslateActivity extends AppCompatActivity {
    /**
     * время задержки между вводом последней буквы и запросом на перевод
     */
    private static final long WAIT_INPUT = 1500;

    private WebView translated;
    private TextView phrase;
    private Button changeLangButton, toFavoriteButton, favoriteButton, historyButton;

    private boolean translationFinished = false;

    private static TranslateActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_translate);

        initComponents();
        loadLanguages();
    }

    @Override
    protected void onResume() {
        updateTranslateButtonText();
        makeTranslation();
        super.onResume();
    }

    /**
     * загрузка языков с /getLangs url
     */
    private void loadLanguages() {
        YandexRequests.loadLanguages();
    }

    /**
     * Установка контента для окна с переводом
     * @param text - html-контент для установки
     */
    public void setTranslatedText(String text) {
        if(text.contentEquals(""))
            toFavoriteButton.setEnabled(false);
        translated.loadDataWithBaseURL(null, "<big>" + text.replaceAll("\n", "<br>") + "</big>", "text/html", "UTF-8", null);
    }

    /**
     * изменить фразу для перевода в UI
     * @param text -фраза для установки
     */
    public void setPhrase(String text) {
        phrase.setText(text);
    }

    public String getPhrase() {
        return phrase.getText().toString();
    }

    /**
     * инициализация UI компонент
     */
    private void initComponents() {
        translated = (WebView) findViewById(R.id.translated);
        phrase = (TextView) findViewById(R.id.phrase);
        changeLangButton = (Button) findViewById(R.id.langChangeButton);
        toFavoriteButton = (Button) findViewById(R.id.tofavoriteBtn);
        favoriteButton = (Button) findViewById(R.id.favoriteBtn);
        historyButton = (Button) findViewById(R.id.historyBtn);

        phrase.setEnabled(false);
        initListeners();
    }

    /**
     * инициализация обработчиков событий
     */
    private void initListeners() {
        changeLangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(instance, ChangeLangActivity.class));
            }
        });
        phrase.addTextChangedListener(new PhraseWatcher());
        toFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentToFavorites();
            }
        });
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(instance, ListPhrasesActivity.class);
                intent.putExtra("workType", ListPhrasesActivity.LISTTYPE_FAVORITES);
                startActivity(intent);
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(instance, ListPhrasesActivity.class);
                intent.putExtra("workType", ListPhrasesActivity.LISTTYPE_HISTORY);
                startActivity(intent);
            }
        });
    }

    /**
     * добавить введенную и переведенную фразу в избранное
     */
    private void addCurrentToFavorites() {

        try {
            PhraseDAO phraseDAO = DbFactory.getHelper().getPhraseDAO();
            PhraseWithTranslation cachedPhrase = phraseDAO.queryForPhraseAndLangTo(
                    phrase.getText().toString(), Config.getConfig().getLangTo());
            if(cachedPhrase == null)
                Toast.makeText(this, "Фраза еще не переведена", Toast.LENGTH_SHORT).show();
            else {
                cachedPhrase.setInFavorites(true);
                phraseDAO.update(cachedPhrase);
                Utils.MessageDialog("Добавлено",
                        "Перевод \"" + cachedPhrase.getPhrase() + "\" добавлен в избранное",
                        this, null);
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Ошибка при запросе к внутренней БД", Toast.LENGTH_SHORT).show();
        }
    }

    public void enable() {
        phrase.setEnabled(true);
    }

    public static TranslateActivity getInstance() {
        return instance;
    }

    /**
     * установить флаг окончания перевода и заодно включить кнопку "добавить в избранное"
      * @param translationFinished
     */
    public void setTranslationFinished(boolean translationFinished) {
        this.translationFinished = translationFinished;
        toFavoriteButton.setEnabled(translationFinished);
    }

    /**
     * запустить перевод
     */
    public void makeTranslation(){
        if(phrase.getText().toString().contentEquals(""))
            setTranslatedText("");
        else
            YandexRequests.translate(phrase.getText().toString());
    }

    public void updateTranslateButtonText() {
        changeLangButton.setText(Config.getConfig().getLangNameFrom() + " -> " + Config.getConfig().getLangNameTo());
    }

    /**
     * обработчик TextEdit для событий изменения ввода
     */
    class PhraseWatcher implements TextWatcher {
        Handler translationHandler = new Handler();
        Runnable translationTask = new Runnable() {
            @Override
            public void run() {
                makeTranslation();
            }
        };
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            /**
             * Запуск обработки ввода с задержкой @WAIT_INPUT, чтобы с вводом очередной
             * промежуточной буквы не осуществлялся запрос перевода
             */
            setTranslationFinished(false);
            translationHandler.removeCallbacks(translationTask);
            if(!YandexRequests.isTranslationActive())
                translationHandler.postDelayed(translationTask, WAIT_INPUT);
        }

    }
}
