package tedking.lovewords;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import java.io.File;
import java.util.ArrayList;

public class FinishExerciseActivity extends Activity {
    private TextView result_score;
    private int score;
    private boolean wordFinish;
    private String objectId, stringResult, username;
    private String [] results;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button nextLesson, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_finishexercise);
        username = AVUser.getCurrentUser().getUsername();
        result_score = findViewById(R.id.result_score);
        nextLesson = findViewById(R.id.nextLesson);
        exit = findViewById(R.id.exitButton);
        score = getIntent().getIntExtra("score",10);
        stringResult = getIntent().getStringExtra("words");
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,MODE_PRIVATE);
        stringResult = stringResult + preferences.getString(StringConstant.WORDSTOSENDTOCLOUD,"");
        wordFinish = getIntent().getBooleanExtra("wordFinish", false);
        if (wordFinish){
            editor = preferences.edit();
            editor.putBoolean(StringConstant.ALLWORDSHAVEMASTERED,true);
            editor.commit();
        }
        if (!stringResult.equals("")){
            results = stringResult.split(",");
            updateWordsStatusToDatabase();
            updateToCloud();
        }
        result_score.setText(score+"");
        nextLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wordFinish){
                    Toast.makeText(FinishExerciseActivity.this,"词库已经做完",Toast.LENGTH_LONG).show();
                }else {
                    startActivity(new Intent(FinishExerciseActivity.this,Exercise.class));
                    finish();
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //update status of words that users have mastered to 1
    private void updateWordsStatusToDatabase(){
        for(String result : results)
            System.out.println(result);
        File file = new File(getFilesDir()+"/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        ContentValues contentValues = new ContentValues();
        contentValues.put("status",1);
        for (String result : results){
            database.update("words",contentValues,"english = ?",new String[]{result});
        }
        database.close();
    }
    private void updateToCloud(){
        ArrayList<AVObject> userWords = new ArrayList<>();
        for (String result : results){
            AVObject userWord = new AVObject("userWord");
            userWord.put("user",username);
            userWord.put("word",result);
            userWords.add(userWord);
        }
        AVObject.saveAllInBackground(userWords, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    //System.out.println("数据上传成功");
                }else {
                    //e.printStackTrace();
                    editor = preferences.edit();
                    editor.putString(StringConstant.WORDSTOSENDTOCLOUD,stringResult);
                    editor.commit();
                    //System.out.println("网络出错");
                    //Toast.makeText(FinishExerciseActivity.this,"网络出错",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //
    private void sendScores(){
        final AVObject dayScore = new AVObject("dayScore");
        dayScore.put("user", username);
        dayScore.put("score",100);
        dayScore.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    objectId = dayScore.getObjectId();
                    Toast.makeText(FinishExerciseActivity.this,"数据上传成功",Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(FinishExerciseActivity.this,"网络错误，请稍后再试",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateScores(){
        AVObject dayScore = AVObject.createWithoutData("dayScore",objectId);
        dayScore.put("score",200);
        dayScore.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    Toast.makeText(FinishExerciseActivity.this,"数据修改成功",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(FinishExerciseActivity.this,"数据修改错误",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //TODO
}
