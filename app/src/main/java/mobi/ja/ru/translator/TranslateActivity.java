package mobi.ja.ru.translator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;


public class TranslateActivity extends AppCompatActivity {
    private WebView translated;
    private TextView phrase;
    private Button changeLangButton;

    private String langs = "langJson({\"dirs\":[\"az-ru\",\"be-bg\",\"be-cs\",\"be-de\",\"be-en\",\"be-es\",\"be-fr\",\"be-it\",\"be-pl\",\"be-ro\",\"be-ru\",\"be-sr\",\"be-tr\",\"bg-be\",\"bg-ru\",\"bg-uk\",\"ca-en\",\"ca-ru\",\"cs-be\",\"cs-en\",\"cs-ru\",\"cs-uk\",\"da-en\",\"da-ru\",\"de-be\",\"de-en\",\"de-es\",\"de-fr\",\"de-it\",\"de-ru\",\"de-tr\",\"de-uk\",\"el-en\",\"el-ru\",\"en-be\",\"en-ca\",\"en-cs\",\"en-da\",\"en-de\",\"en-el\",\"en-es\",\"en-et\",\"en-fi\",\"en-fr\",\"en-hu\",\"en-it\",\"en-lt\",\"en-lv\",\"en-mk\",\"en-nl\",\"en-no\",\"en-pt\",\"en-ru\",\"en-sk\",\"en-sl\",\"en-sq\",\"en-sv\",\"en-tr\",\"en-uk\",\"es-be\",\"es-de\",\"es-en\",\"es-ru\",\"es-uk\",\"et-en\",\"et-ru\",\"fi-en\",\"fi-ru\",\"fr-be\",\"fr-de\",\"fr-en\",\"fr-ru\",\"fr-uk\",\"hr-ru\",\"hu-en\",\"hu-ru\",\"hy-ru\",\"it-be\",\"it-de\",\"it-en\",\"it-ru\",\"it-uk\",\"lt-en\",\"lt-ru\",\"lv-en\",\"lv-ru\",\"mk-en\",\"mk-ru\",\"nl-en\",\"nl-ru\",\"no-en\",\"no-ru\",\"pl-be\",\"pl-ru\",\"pl-uk\",\"pt-en\",\"pt-ru\",\"ro-be\",\"ro-ru\",\"ro-uk\",\"ru-az\",\"ru-be\",\"ru-bg\",\"ru-ca\",\"ru-cs\",\"ru-da\",\"ru-de\",\"ru-el\",\"ru-en\",\"ru-es\",\"ru-et\",\"ru-fi\",\"ru-fr\",\"ru-hr\",\"ru-hu\",\"ru-hy\",\"ru-it\",\"ru-lt\",\"ru-lv\",\"ru-mk\",\"ru-nl\",\"ru-no\",\"ru-pl\",\"ru-pt\",\"ru-ro\",\"ru-sk\",\"ru-sl\",\"ru-sq\",\"ru-sr\",\"ru-sv\",\"ru-tr\",\"ru-uk\",\"sk-en\",\"sk-ru\",\"sl-en\",\"sl-ru\",\"sq-en\",\"sq-ru\",\"sr-be\",\"sr-ru\",\"sr-uk\",\"sv-en\",\"sv-ru\",\"tr-be\",\"tr-de\",\"tr-en\",\"tr-ru\",\"tr-uk\",\"uk-bg\",\"uk-cs\",\"uk-de\",\"uk-en\",\"uk-es\",\"uk-fr\",\"uk-it\",\"uk-pl\",\"uk-ro\",\"uk-ru\",\"uk-sr\",\"uk-tr\"],\"langs\":{\"af\":\"Африкаанс\",\"am\":\"Амхарский\",\"ar\":\"Арабский\",\"az\":\"Азербайджанский\",\"ba\":\"Башкирский\",\"be\":\"Белорусский\",\"bg\":\"Болгарский\",\"bn\":\"Бенгальский\",\"bs\":\"Боснийский\",\"ca\":\"Каталанский\",\"ceb\":\"Себуанский\",\"cs\":\"Чешский\",\"cy\":\"Валлийский\",\"da\":\"Датский\",\"de\":\"Немецкий\",\"el\":\"Греческий\",\"en\":\"Английский\",\"eo\":\"Эсперанто\",\"es\":\"Испанский\",\"et\":\"Эстонский\",\"eu\":\"Баскский\",\"fa\":\"Персидский\",\"fi\":\"Финский\",\"fr\":\"Французский\",\"ga\":\"Ирландский\",\"gd\":\"Шотландский (гэльский)\",\"gl\":\"Галисийский\",\"gu\":\"Гуджарати\",\"he\":\"Иврит\",\"hi\":\"Хинди\",\"hr\":\"Хорватский\",\"ht\":\"Гаитянский\",\"hu\":\"Венгерский\",\"hy\":\"Армянский\",\"id\":\"Индонезийский\",\"is\":\"Исландский\",\"it\":\"Итальянский\",\"ja\":\"Японский\",\"jv\":\"Яванский\",\"ka\":\"Грузинский\",\"kk\":\"Казахский\",\"km\":\"Кхмерский\",\"kn\":\"Каннада\",\"ko\":\"Корейский\",\"ky\":\"Киргизский\",\"la\":\"Латынь\",\"lb\":\"Люксембургский\",\"lo\":\"Лаосский\",\"lt\":\"Литовский\",\"lv\":\"Латышский\",\"mg\":\"Малагасийский\",\"mhr\":\"Марийский\",\"mi\":\"Маори\",\"mk\":\"Македонский\",\"ml\":\"Малаялам\",\"mn\":\"Монгольский\",\"mr\":\"Маратхи\",\"mrj\":\"Горномарийский\",\"ms\":\"Малайский\",\"mt\":\"Мальтийский\",\"my\":\"Бирманский\",\"ne\":\"Непальский\",\"nl\":\"Голландский\",\"no\":\"Норвежский\",\"pa\":\"Панджаби\",\"pap\":\"Папьяменто\",\"pl\":\"Польский\",\"pt\":\"Португальский\",\"ro\":\"Румынский\",\"ru\":\"Русский\",\"si\":\"Сингальский\",\"sk\":\"Словацкий\",\"sl\":\"Словенский\",\"sq\":\"Албанский\",\"sr\":\"Сербский\",\"su\":\"Сунданский\",\"sv\":\"Шведский\",\"sw\":\"Суахили\",\"ta\":\"Тамильский\",\"te\":\"Телугу\",\"tg\":\"Таджикский\",\"th\":\"Тайский\",\"tl\":\"Тагальский\",\"tr\":\"Турецкий\",\"tt\":\"Татарский\",\"udm\":\"Удмуртский\",\"uk\":\"Украинский\",\"ur\":\"Урду\",\"uz\":\"Узбекский\",\"vi\":\"Вьетнамский\",\"xh\":\"Коса\",\"yi\":\"Идиш\",\"zh\":\"Китайский\"}})";

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
        changeLangButton.setText(Config.getConfig().getLangNameFrom() + " -> " + Config.getConfig().getLangNameTo());
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
        translated.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
    }

    /**
     * инициализация UI компонент
     */
    private void initComponents() {
        translated = (WebView) findViewById(R.id.translated);
        phrase = (TextView) findViewById(R.id.phrase);
        changeLangButton = (Button) findViewById(R.id.langChangeButton);

        phrase.setEnabled(false);
        initListeners();
        setTranslatedText("Что то русское");
    }

    private void initListeners() {
        changeLangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(instance, ChangeLangActivity.class));
            }
        });
        phrase.addTextChangedListener(new PhraseWatcher());
    }

    public void enable() {
        phrase.setEnabled(true);
    }

    public static TranslateActivity getInstance() {
        return instance;
    }

    public Button getChangeLangButton() {
        return changeLangButton;
    }

    public void setChangeLangButton(Button changeLangButton) {
        this.changeLangButton = changeLangButton;
    }

    public void makeTranslation(){
        if(phrase.getText().toString().contentEquals(""))
            setTranslatedText("");
        else
            YandexRequests.translate(phrase.getText().toString());
    }

    class PhraseWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

           makeTranslation();
        }

    }
}
