package tedking.lovewords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/3/4.
 */

public class WordSearchFragment extends android.support.v4.app.Fragment {
    private static final String url = "http://fanyi.youdao.com/openapi.do?keyfrom=WordAlarm&key=1428833977&type=data&doctype=xml&version=1.1&q=",
            search_failed_in_database = "not found in database",
            search_failed_in_Internet = "not found in Internet";
    public Button search,more;
    private EditText searchWord;
    private TextView wordItself, pronunciation, meaning;
    private SharedPreferences preferences;
    private String dirName;
    private boolean firstOpen;
    private ImageView imageSearch;
    private SharedPreferences.Editor editor;
    private static final int UPDATE_CONTENT = 0;
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
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchWordAndSetText();
                hideInputKeyboard(getContext());
            }
        });
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.performClick();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(getContext(), WordDetail.class);
                    intent.putExtra("word", wordItself.getText().toString());
                    intent.putExtra("pronunciation", pronunciation.getText().toString());
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(),"Please check if your Internet is available",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void findView(View view){
        search = view.findViewById(R.id.btn_search);
        more = view.findViewById(R.id.moreDetail);
        searchWord = view.findViewById(R.id.edit_query);
        searchWord.setSingleLine();
        wordItself = view.findViewById(R.id.word_itself);
        pronunciation = view.findViewById(R.id.pronunciation);
        meaning = view.findViewById(R.id.meaning);
        imageSearch = view.findViewById(R.id.search_icon);
    }

    public void isFirstOpenApp(){
        preferences = getContext().getSharedPreferences(StringConstant.SHAREDPREFERENCENAME, Context.MODE_PRIVATE);
        firstOpen = preferences.getBoolean(StringConstant.FIRSTOPENAPP,true);
        if (firstOpen){
            importDatabase();
            editor = preferences.edit();
            editor.putBoolean(StringConstant.FIRSTOPENAPP, false).commit();
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
        Cursor cursor = database.rawQuery("select * from words where english = ?",new String[]{searchWord.getText().toString().trim()});
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
            translation[0] = search_failed_in_database;
            return translation;
        }
        return translation;
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null){
            return false;
        }else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null){
                return info.isConnected();
            }else {
                return false;
            }
        }
    }
    public void searchFromInternet(){
        new Thread(new mRunnable()).start();
    }

    public void searchWordAndSetText(){
        if (searchWord.getText().toString().equals("")){
            Toast.makeText(getContext(),"Please input the word you want to search",Toast.LENGTH_LONG).show();
        } else if(searchWord.getText().toString().trim().contains(" ")){
            Toast.makeText(getContext(),"Please input a single word",Toast.LENGTH_LONG).show();
        } else {
            String [] result = searchFromDatabase();
            if (!result[0].equals(search_failed_in_database)){
                wordItself.setText(result[0]);
                pronunciation.setText(result[1]);
                meaning.setText(result[2]);
                more.setVisibility(View.VISIBLE);
            }else if (isNetworkAvailable()){
                searchFromInternet();
            }else {
                Toast.makeText(getContext(),"Please check if your Internet is available",Toast.LENGTH_LONG).show();
            }
        }
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            String [] result = (String[]) message.obj;
            switch (message.what){
                case UPDATE_CONTENT:
                    if (result[0].equals(search_failed_in_Internet)){
                        Toast.makeText(getContext(),"Search Failed, please check if your word is valid",Toast.LENGTH_SHORT).show();
                        wordItself.setText("");
                        pronunciation.setText("");
                        meaning.setText("");
                        more.setVisibility(View.GONE);
                    }
                    else {
                        wordItself.setText(result[0]);
                        pronunciation.setText(result[1]);
                        meaning.setText(result[2]);
                        more.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public  void hideInputKeyboard(final Context context) {
        final Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager mInputKeyBoard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (activity.getCurrentFocus() != null) {
                    mInputKeyBoard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        });
    }

    private class mRunnable implements Runnable{
        @Override
        public void run(){
            String string = "";
            String [] translation= {"","",""};
            translation[0] = search_failed_in_Internet;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) ((new URL(url + URLEncoder.encode(searchWord.getText().toString(), "utf-8")).openConnection()));
                connection.setRequestMethod("GET");
                connection.setReadTimeout(8000);
                connection.setConnectTimeout(8000);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                int response_code = connection.getResponseCode();
                if (response_code == connection.HTTP_OK) {
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    string = response.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            try {
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(new StringReader(string));
                int eventType = parser.getEventType();
                boolean explains = false;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if ("phonetic".equals(parser.getName())) {
                                try {
                                    translation[1] = "/" + parser.nextText() + "/";
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if ("explains".equals(parser.getName())) {
                                explains = true;
                            } else if ("ex".equals(parser.getName())) {
                                if (explains) {
                                    try {
                                        translation[2] += parser.nextText() + "\n";
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if ("explains".equals(parser.getName()))
                                explains = false;
                            break;
                        default:
                            break;
                    }
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            if (!translation[2].equals("")){
                translation[0] = searchWord.getText().toString();
            }
            Message message = new Message();
            message.what = UPDATE_CONTENT;
            message.obj = translation;
            handler.sendMessage(message);
        }
    }
}

