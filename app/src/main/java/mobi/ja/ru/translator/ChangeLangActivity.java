package mobi.ja.ru.translator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

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
        final JSONObject langsJson = Config.getConfig().getLangs();
        String[] langsArray = new String[langsJson.length()];
        try {
            for(int i=0; i<langsArray.length; i++) {
                langsArray[i] = langsJson.get(langsJson.names().get(i).toString()).toString();
            }
         } catch (JSONException e) {
            Log.e("Error", "Parsing names error!");
         }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, langsArray);

        langList.setAdapter(adapter);

        langList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Config.getConfig().setLangTo(langsJson.names().get(position).toString());
                    instance.finish();
                } catch (JSONException e) {
                }
            }
        });
    }


    public ChangeLangActivity getInstance() {
        return instance;
    }

}
