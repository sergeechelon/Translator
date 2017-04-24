package mobi.ja.ru.translator.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import mobi.ja.ru.translator.Utils;

/**
 * DAO для доступа к таблице с PhraseWithTranslation
 */
public class PhraseDAO extends BaseDaoImpl<PhraseWithTranslation, Integer> {
    protected PhraseDAO(ConnectionSource connectionSource,
                      Class<PhraseWithTranslation> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public PhraseWithTranslation queryForPhraseAndLangTo(String phrase, String langTo) throws SQLException {
        List<PhraseWithTranslation> phraseList = this.queryBuilder().where().
                eq("phrase", Utils.trimPhrase(phrase)).and().eq("langTo", langTo).query();
        if(phraseList.size() == 0)
            return null;
        return phraseList.get(0);
    }

}
