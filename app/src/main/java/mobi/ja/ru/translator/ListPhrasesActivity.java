package mobi.ja.ru.translator;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

import mobi.ja.ru.translator.db.DbFactory;
import mobi.ja.ru.translator.db.PhraseDAO;
import mobi.ja.ru.translator.db.PhraseWithTranslation;

/**
 * acitvity для списка с историей или с избранным
 * Тип функционирования определяется extra "workType"
 * В обоих случаях формируется кастомный лист с исходной фразой и переводом,
 * при нажатии на который фраза ListPhrasesActivity закрывается
 * и фраза с переводом выдаются на основном TranslateActivity.
 * В случае LISTTYPE_HISTORY (история переводов) скрывается кнопка единичного удаления
 * и показывается кнопка очистки истории
 */
public class ListPhrasesActivity extends AppCompatActivity {

    /**
     * Флаг запуска списка избранного
     */
    public static final int LISTTYPE_FAVORITES = 0;
    /**
     * флаг запуска списка истории
     */
    public static final int LISTTYPE_HISTORY = 1;

    private int listType;

    private TextView titleText;
    private ListView phrasesList;

    private Button clearHistoryBtn;

    private PhraseAdapter phraseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_phrases);

        listType = getIntent().getIntExtra("workType", LISTTYPE_FAVORITES);

        initTitle();
        initList();
        initClearHistory();
    }

    private void initClearHistory() {
        clearHistoryBtn = (Button) findViewById(R.id.listPhrasesHistoryClean);
        if(listType == LISTTYPE_FAVORITES) { // убираем кнопку очистки истории в случае списка избранного
            clearHistoryBtn.setVisibility(View.GONE);
            return;
        }
        clearHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.OkCancelDialog("Очистить?", "Очистить историю переводов?", ListPhrasesActivity.this,
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    PhraseDAO phraseDAO = DbFactory.getHelper().getPhraseDAO();
                                    // убираем флаг inHistory
                                    phraseDAO.updateBuilder().updateColumnValue("inHistory", false).update();
                                    DeleteBuilder deleteUnused = phraseDAO.deleteBuilder();
                                    // удаляем все, что не в избранном
                                    deleteUnused.where().eq("inFavorites", false);
                                    deleteUnused.delete();
                                    ListPhrasesActivity.this.recreate();
                                } catch (SQLException e) {
                                    Log.e("SQL error", "Ошибка очистки истории");
                                }
                            }
                        });
            }
        });
    }

    private void initTitle() {
        titleText = (TextView) findViewById(R.id.listPhrasesTitle);

        if(listType == LISTTYPE_FAVORITES)
            titleText.setText("Избранное");
        else if(listType == LISTTYPE_HISTORY)
            titleText.setText("История");
    }

    private void initList() {
        phrasesList = (ListView) findViewById(R.id.listPhrasesList);

        List<PhraseWithTranslation> phrasesArray = null;
        try {
            if(listType == LISTTYPE_FAVORITES)
                phrasesArray = DbFactory.getHelper().getPhraseDAO().
                    queryForEq("inFavorites", true);
            else if(listType == LISTTYPE_HISTORY)
                phrasesArray = DbFactory.getHelper().getPhraseDAO().
                        queryForEq("inHistory", true);
        } catch (SQLException e) {
            Log.e("SQL error", "Ошибка получения списка истории/избранного");
            finish();
        }

        phraseAdapter = new PhraseAdapter(this, phrasesArray);

        phrasesList.setAdapter(phraseAdapter);

        phrasesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    /**
     * обработчик нажатия на элемент списка истории/избранного
     */
    private class ChooseAndCloseListener implements View.OnClickListener {
        private int position;
        public ChooseAndCloseListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            PhraseWithTranslation item = phraseAdapter.getItem(position);
            // изменяем настройки
            TranslateActivity.getInstance().setPhrase(item.getPhrase());
            TranslateActivity.getInstance().setTranslatedText(item.getTranslatedText());
            // изменяем текст на главном активити
            Config.getConfig().setLangFrom(item.getLangFrom());
            Config.getConfig().setLangTo(item.getLangTo());
            ListPhrasesActivity.this.finish();
        }
    };

    /**
     * обработчик нажатия кнопки удаления из избранного
     */
    private class DeleteListener implements View.OnClickListener {
        private int position;
        public DeleteListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            Utils.OkCancelDialog("Удалить?", "Удалить из избранного?", ListPhrasesActivity.this,
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PhraseWithTranslation phrase = phraseAdapter.getItem(position);
                                phrase.setInFavorites(false);
                                DbFactory.getHelper().getPhraseDAO().update(phrase);
                            } catch (SQLException e) {
                                Log.e("SQL error", "Ошибка удаления из избранного");
                            }
                            ListPhrasesActivity.this.recreate();
                        }
                    });
        }
    }


    /**
     * адаптер между списком фраз с переводами и кастомным листом
     */
    private class PhraseAdapter extends ArrayAdapter<PhraseWithTranslation> {
        public PhraseAdapter(Context context, List<PhraseWithTranslation> phrases) {
            super(context, R.layout.list_row, phrases);
         }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PhraseWithTranslation phrase = getItem(position);

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, null);

            TextView phraseView = ((TextView) convertView.findViewById(R.id.listRowPhrase));
            TextView translationView = ((TextView) convertView.findViewById(R.id.listRowTranslation));

            // создаем обработчик нажатия на элемент списка
            ChooseAndCloseListener chooseAndCloseListener = new ChooseAndCloseListener(position);

            // устанавливаем на оба элемента
            phraseView.setText("(" + phrase.getLangFrom() + ") " + phrase.getPhrase());
            phraseView.setOnClickListener(chooseAndCloseListener);
            translationView.setText("(" + phrase.getLangTo() + ") " + phrase.getTranslatedText());
            translationView.setOnClickListener(chooseAndCloseListener);

            if(listType == LISTTYPE_FAVORITES)
                // скрытие кнопки удаления для истории
                ((ImageButton) convertView.findViewById(R.id.listGarbageBtn)).
                        setOnClickListener(new DeleteListener(position));
            else if(listType == LISTTYPE_HISTORY)
                // или установка обработчика нажатия кнопки удаления для избранного
                ((ImageButton) convertView.findViewById(R.id.listGarbageBtn)).setVisibility(View.INVISIBLE);

            return convertView;
        }

    }
}
