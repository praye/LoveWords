package tedking.lovewords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Dialog dialog;
    private int times = 0, sum = 0, expectedSum = 0;
    private static final  int updateSignal = 1;

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
                        showDialog();
                        setStatus();
                    }else {
                        Toast.makeText(RecoveryActivity.this,"Your data is newest",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(RecoveryActivity.this,"Network error!",Toast.LENGTH_SHORT).show();
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
                        times = i / 1000 + 1;
                        for (int a = 0; a < times; a ++){
                            expectedSum += a;
                        }
                        final ContentValues cv = new ContentValues();
                        cv.put("status","1");
                        query.limit(1000);
                        for (int t = 0; t < times; t ++){
                            query.skip(t*1000);
                            final int t1 = t;
                            query.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    if(e == null) {
                                        for (AVObject object : list) {
                                            database.update("words", cv, "english = ?", new String[]{object.getString("word")});
                                        }
                                        Message message = new Message();
                                        message.what = updateSignal;
                                        message.obj = t1;
                                        handler.sendMessage(message);
                                    }else {
                                        System.out.println("Network error");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
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

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            int result = (int) message.obj;
            switch (message.what){
                case updateSignal:
                    for (int i = 0; i < times; i ++){
                        if (result == i){
                            sum += result;
                            System.out.println(i);
                            if (sum == expectedSum){
                                dialog.dismiss();
                                Toast.makeText(RecoveryActivity.this,"Data recovered",Toast.LENGTH_LONG).show();
                                editor = preferences.edit();
                                editor.putBoolean(StringConstant.UPDATEDICTIONARY,true);
                                editor.commit();
                                System.out.println("finish");
                            }
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.recovery_loading,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
}
