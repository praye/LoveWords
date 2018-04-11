package tedking.lovewords;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WordsActivity extends Activity implements LoadmoreListView.IloadListener {
    private LoadmoreListView listView;
    private TextView loadNotice;
    private File file;
    private SQLiteDatabase database;
    private List<WordMeaning> listItem = new ArrayList<>();
    private WordAdapter adapter;
    private String status;
    private int times = 0;
    private boolean noMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_words);
        status = getIntent().getStringExtra("status");
        listView = findViewById(R.id.loadMoreListView);
        loadNotice = findViewById(R.id.loadNotice);
        file = new File(this.getFilesDir()+"/databases/data.db");
        database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        initData();
        adapter = new WordAdapter(listItem,this);
        listView.setAdapter(adapter);
        listView.setInterface(this);
    }

    private void initData(){
        Cursor cursor = database.rawQuery("select english, chinese from words where status = ? limit 100", new String[]{status});
        if (cursor.getCount() == 0){
            loadNotice.setVisibility(View.VISIBLE);
        }else{
            loadNotice.setVisibility(View.GONE);
            while (cursor.moveToNext()){
                listItem.add(new WordMeaning(cursor.getString(0),cursor.getString(1)));
            }
            if (cursor.getCount() < 100){
                noMore = true;
            }
        }
        cursor.close();
    }

    @Override
    public void onLoad() {
        if (! noMore){
            times ++;
            Cursor cursor = database.rawQuery("select english, chinese from words where status = ? limit ?, 100", new String[]{status,String.valueOf(times*100)});
            while (cursor.moveToNext()){
                listItem.add(new WordMeaning(cursor.getString(0),cursor.getString(1)));
            }
            if (cursor.getCount() < 100){
                noMore = true;
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }else {
            Toast.makeText(WordsActivity.this,"No more data.",Toast.LENGTH_SHORT).show();
        }
        listView.onLoadComplete();
    }

    @Override
    public void onDestroy(){
        database.close();
        super.onDestroy();
    }
}
