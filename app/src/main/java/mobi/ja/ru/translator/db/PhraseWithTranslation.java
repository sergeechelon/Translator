package mobi.ja.ru.translator.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mobi.ja.ru.translator.Utils;

/**
 * Единственный класс, маппирующийся в БД. Содержит фразу для перевода, ее перевод,
 * направление перевода, а также нахождение в истории и избранном.
 * Таблица с этими объектами по совместительству является кэшем, и если в ней находится
 * конкретная фраза с конкретным направлением, то запрос к серверу не осуществляется,
 * а просто выводится результат.
 */
@DatabaseTable(tableName = "PhraseWithTranslation")
public class PhraseWithTranslation {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String phrase;
    @DatabaseField
    private String langFrom;
    @DatabaseField
    private String langTo;
    @DatabaseField
    private String translatedText;
    @DatabaseField
    private boolean inFavorites;
    @DatabaseField
    private boolean inHistory;

    public PhraseWithTranslation(String phrase, String translation, String langFrom, String langTo) {
        this.phrase = Utils.trimPhrase(phrase);
        this.translatedText = translation;
        this.inFavorites = false;
        this.langFrom = langFrom;
        this.langTo = langTo;
        this.inFavorites = false;
        this.inHistory = true;
    }

    public PhraseWithTranslation() {
        this.phrase = "";
        this.translatedText = "";
        this.inFavorites = false;
        this.langFrom = "en";
        this.langTo = "en";
        this.inFavorites = false;
        this.inHistory = true;
    }

    public boolean isInFavorites() {
        return inFavorites;
    }

    public void setInFavorites(boolean inFavorites) {
        this.inFavorites = inFavorites;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
    public String getLangTo() {
        return langTo;
    }

    public void setlangTo(String langTo) {
        this.langTo = langTo;
    }

    public String getLangFrom() {
        return langFrom;
    }

    public void setLangFrom(String langFrom) {
        this.langFrom = langFrom;
    }

    public boolean isInHistory() {
        return inHistory;
    }

    public void setInHistory(boolean inHistory) {
        this.inHistory = inHistory;
    }

}
