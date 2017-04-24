package mobi.ja.ru.translator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * activity для выбора языка, на который переводить
 */
public class ChangeLangActivity extends AppCompatActivity {
    private ChangeLangActivity instance;

    private ListView langList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_change_lang);

        initList();
    }

    private void initList() {
        langList = (ListView) findViewById(R.id.langList);
        final Map<String, String> langs = Config.getConfig().getLangs();
        final String[] langsArray = langs.values().toArray(new String[0]);
        final String[] shortArray = langs.keySet().toArray(new String[0]);

        // "лайфхак", переносящий русский и английский языки в начало списка
        int engPos = -1, ruPos = -1;
        for(int i=0; i<langs.size(); i++) {
            if(shortArray[i].contentEquals("en"))
                engPos = i;
            if(shortArray[i].contentEquals("ru"))
                ruPos = i;
        }
        xchg(langsArray, 0, ruPos);
        xchg(shortArray, 0, ruPos);
        xchg(langsArray, 1, engPos);
        xchg(shortArray, 1, engPos);
        // </лайфхак>

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, langsArray);

        langList.setAdapter(adapter);

        langList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Config.getConfig().setLangTo(shortArray[(int)id]);
                instance.finish();
            }
        });
    }

    private void xchg(String[] arr, int i, int j) {
        String tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }


    public ChangeLangActivity getInstance() {
        return instance;
    }

}
