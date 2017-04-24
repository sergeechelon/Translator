package mobi.ja.ru.translator.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import mobi.ja.ru.translator.Config;

/**
 * DAO для класса Config
 */
public class ConfigDAO extends BaseDaoImpl<Config, Integer> {
    protected ConfigDAO(ConnectionSource connectionSource,
                        Class<Config> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
