package mobi.ja.ru.translator.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import mobi.ja.ru.translator.Config;

/**
 * Created by Serg on 23.04.2017.
 */
public class ConfigDAO extends BaseDaoImpl<Config, Integer> {
    protected ConfigDAO(ConnectionSource connectionSource,
                        Class<Config> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
