package tedking.lovewords;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RecoveryActivity extends Activity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase database;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recovery);
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,MODE_PRIVATE);
        File file = new File(getFilesDir()+"/databases/data.db");
        database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        button = findViewById(R.id.recovery);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    if (!preferences.getBoolean(StringConstant.UPDATEDICTIONARY, false)){
                        setStatus();
                    }else {
                        Toast.makeText(RecoveryActivity.this,"无需恢复",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(RecoveryActivity.this,"网络出错，请检查你的网络",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setStatus(){
        System.out.println("setStatus");
        final AVQuery<AVObject> query = new AVQuery<>("userWord");
        query.whereEqualTo("user", AVUser.getCurrentUser().getUsername());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null){
                    query.selectKeys(Arrays.asList("word"));
                    if (i != 0){
                        System.out.println("i =" + i);
                        int j = i / 1000 + 1;
                        final ContentValues cv = new ContentValues();
                        cv.put("status","1");
                        query.limit(1000);
                        for (int t = 0; t < j; t ++){
                            query.skip(t*1000);
                            query.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    for (AVObject object : list){
                                        database.update("words",cv,"english = ?", new String[]{object.getString("word")});
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        editor = preferences.edit();
        editor.putBoolean(StringConstant.UPDATEDICTIONARY,true);
        editor.commit();
    }
    @Override
    public void onDestroy(){
        if (database.isOpen())
            database.close();
        super.onDestroy();
    }
    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
