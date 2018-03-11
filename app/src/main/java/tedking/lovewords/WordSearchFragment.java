package tedking.lovewords;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/3/4.
 */

public class WordSearchFragment extends android.support.v4.app.Fragment {
    public Button search;
    private EditText searchWord;
    private TextView wordItself, pronunciation, meaning;
    private SharedPreferences preferences;
    private String dirName, firstOpenApp = "firstOpenApp";
    private boolean firstOpen;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.word_search_layout, container, false);
        findView(view);
        isFirstOpenApp();
        return view;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //method to be implemented
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),editText.getText(),Toast.LENGTH_LONG).show();
                /*Intent intent = new Intent();
                intent.setClass(getActivity(),LoginActivity.class);
                startActivity(intent);*/
                if (searchWord.getText().toString().equals("")){
                    Toast.makeText(getContext(),"请输入你要查询的单词",Toast.LENGTH_LONG).show();
                }else {
                    String [] result = searchFromDatabase();
                    wordItself.setText(result[0]);
                    pronunciation.setText(result[1]);
                    meaning.setText(result[2]);
                }
            }
        });
    }

    public void findView(View view){
        search = view.findViewById(R.id.btn_search);
        searchWord = view.findViewById(R.id.edit_query);
        wordItself = view.findViewById(R.id.word_itself);
        pronunciation = view.findViewById(R.id.pronunciation);
        meaning = view.findViewById(R.id.meaning);
    }

    public void isFirstOpenApp(){
        preferences = getContext().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        firstOpen = preferences.getBoolean(firstOpenApp,true);
        if (firstOpen){
            importDatabase();
            editor = preferences.edit();
            editor.putBoolean(firstOpenApp, false).commit();
            //Toast.makeText(getContext(), "yes", Toast.LENGTH_LONG).show();
        }
    }

    public void importDatabase(){
        dirName = getContext().getFilesDir() + "/databases";
        File dir = new File(dirName);
        if (!dir.exists()){
            dir.mkdir();
        }
        dirName += "/data.db";
        File file = new File(dirName);
        if (!file.exists()) {
            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;
            inputStream = getResources().openRawResource(R.raw.data);
            try {
                fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[128];
                int len = 0;
                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    buffer = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String[] searchFromDatabase(){
        String []translation = {"","",""};
        File file = new File(getContext().getFilesDir()+"/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = database.rawQuery("select * from words where english = ?",new String[]{searchWord.getText().toString()});
        int column = 3;
        while (cursor.moveToNext()){
            for (int i = 0; i < column; i ++) {
                String columnname = cursor.getColumnName(i);
                String columnvalue = cursor.getString(cursor.getColumnIndex(columnname));
                translation[i] = columnvalue;
            }
        }
        if (database != null)
            database.close();
        if (translation[0].equals(""))
        {
            translation[0] = "没找到";
            return translation;
        }
        return translation;
    }
}
