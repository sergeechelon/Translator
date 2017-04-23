package mobi.ja.ru.translator.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import mobi.ja.ru.translator.Utils;

/**
 * Created by Serg on 23.04.2017.
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

    public PhraseWithTranslation(String phrase, String translation, String langFrom, String langTo) {
        this.phrase = Utils.trimPhrase(phrase);
        this.translatedText = translation;
        this.inFavorites = false;
        this.langFrom = langFrom;
        this.langTo = langTo;
    }

    public PhraseWithTranslation() {
        this.phrase = "";
        this.translatedText = "";
        this.inFavorites = false;
        this.langFrom = "en";
        this.langTo = "en";
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

}
