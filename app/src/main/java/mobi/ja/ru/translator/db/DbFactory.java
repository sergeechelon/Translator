package mobi.ja.ru.translator.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Serg on 23.04.2017.
 */
public class DbFactory {

        private static DbHelper databaseHelper;

        public static DbHelper getHelper(){
            return databaseHelper;
        }
        public static void setHelper(Context context){
            databaseHelper = OpenHelperManager.getHelper(context, DbHelper.class);
        }
        public static void releaseHelper(){
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
}
