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
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class FinishExerciseActivity extends Activity {
    private TextView result_score;
    private int score;
    private boolean wordFinish;
    private String stringResult, username;
    private String [] results;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button nextLesson, exit;
    private int tempScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_finishexercise);
        findView();
        updateScore("dayScore");
        updateScore("weekScore");
        updateTotalScore(score);
        stringResult = stringResult + preferences.getString(StringConstant.WORDSTOSENDTOCLOUD,"");

        if (!stringResult.equals("")){
            results = stringResult.split(",");
            updateWordsStatusToDatabase();
            updateToCloud();
        }

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

    /**
     * find view and getIntent.getExtra;
     */
    private void findView(){
        username = AVUser.getCurrentUser().getUsername();
        result_score = findViewById(R.id.result_score);
        nextLesson = findViewById(R.id.nextLesson);
        exit = findViewById(R.id.exitButton);
        score = getIntent().getIntExtra("score",10);
        stringResult = getIntent().getStringExtra("words");
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,MODE_PRIVATE);
        wordFinish = getIntent().getBooleanExtra("wordFinish", false);
        result_score.setText(score+"");
        if (wordFinish){
            editor = preferences.edit();
            editor.putBoolean(StringConstant.ALLWORDSHAVEMASTERED,true);
            editor.commit();
        }
    }

    /**
     * @param none;
     * @function update the status of words that have been mastered to 1;
     */
    private void updateWordsStatusToDatabase(){
        File file = new File(getFilesDir()+"/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        ContentValues contentValues = new ContentValues();
        contentValues.put("status",1);
        for (String result : results){
            database.update("words",contentValues,"english = ?",new String[]{result});
        }
        database.close();
    }

    /**
     * @function store words that have been mastered to cloud
     */
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

    /**
     * @function create a record while no record found in condition that day equals today and user is current user
     * @param className
     */
    private void sendScores(final String className){
        final AVObject dayScore = new AVObject(className);
        dayScore.put("user", username);
        if (className.equals("dayScore"))
            tempScore = preferences.getInt(StringConstant.DAYSCORE,0);
        else
            tempScore = preferences.getInt(StringConstant.WEEKSCORE,0);
        dayScore.put("score",tempScore + score);
        dayScore.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                editor = preferences.edit();
                if (e == null){
                    if (className.equals("dayScore")) {
                        editor.putInt(StringConstant.DAYSCORE,0);
                        editor.putInt(StringConstant.DAYOFYEAR,StaticMethod.getDayOfYear());
                    }else {
                        editor.putInt(StringConstant.WEEKSCORE,0);
                        editor.putInt(StringConstant.WEEKOFYEAR,StaticMethod.getWeekOfYear());
                    }
                }else {
                    editor.putInt(StringConstant.DAYSCORE,tempScore + score);
                    if (className.equals("dayScore"))
                        editor.putInt(StringConstant.DAYOFYEAR,StaticMethod.getDayOfYear());
                    else
                    editor.putInt(StringConstant.WEEKOFYEAR,StaticMethod.getWeekOfYear());
                }
                editor.commit();
            }
        });
    }

    private void updateScoresDetail(final String className, String objectId, final int innerScore){
        final AVObject dayOrWeekScore = AVObject.createWithoutData(className,objectId);
        dayOrWeekScore.put("score",innerScore);
        dayOrWeekScore.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                editor = preferences.edit();
                if (e == null){
                    if (className.equals("dayScore")){
                        editor.putInt(StringConstant.DAYSCORE,0);
                        editor.putInt(StringConstant.DAYOFYEAR,StaticMethod.getDayOfYear());
                    }else {
                        editor.putInt(StringConstant.WEEKSCORE,0);
                        editor.putInt(StringConstant.WEEKOFYEAR,StaticMethod.getWeekOfYear());
                    }
                }else {
                    if (className.equals("dayScore")) {
                        editor.putInt(StringConstant.DAYSCORE, innerScore);
                        editor.putInt(StringConstant.DAYOFYEAR,StaticMethod.getDayOfYear());
                    }
                    else {
                        editor.putInt(StringConstant.WEEKSCORE, innerScore);
                        editor.putInt(StringConstant.WEEKOFYEAR,StaticMethod.getWeekOfYear());
                    }
                }
                editor.commit();
            }
        });
    }

    private void updateScore(final String className){
        final Calendar calendar = Calendar.getInstance();
        Date startDate;
        if (className.equals("dayScore")){
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            startDate = calendar.getTime();
        }else{
            calendar.set(Calendar.DAY_OF_WEEK, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND,0);
            startDate = calendar.getTime();
        }
        AVQuery<AVObject> queryByUser = new AVQuery<>(className);
        queryByUser.whereEqualTo("user",username);
        AVQuery<AVObject> queryByTime = new AVQuery<>(className);
        queryByTime.whereGreaterThanOrEqualTo("createdAt",startDate);
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(queryByUser,queryByTime));
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null){
                    if (avObject == null){
                        sendScores(className);
                    }else {
                        if (className.equals("dayScore")){
                            updateScoresDetail(className,avObject.getObjectId(),score + preferences.getInt(StringConstant.DAYSCORE,0) + avObject.getInt("score") );
                        }else {
                            updateScoresDetail(className,avObject.getObjectId(),score + preferences.getInt(StringConstant.WEEKSCORE, 0)+ avObject.getInt("score") );
                        }
                    }
                }else{
                    if (className.equals("dayScore"))
                        tempScore = preferences.getInt(StringConstant.DAYSCORE,0);
                    else
                        tempScore = preferences.getInt(StringConstant.WEEKSCORE,0);
                    editor = preferences.edit();
                    if (className.equals("dayScore")) {
                        editor.putInt(StringConstant.DAYSCORE,score + tempScore);
                        editor.putInt(StringConstant.DAYOFYEAR,StaticMethod.getDayOfYear());
                    }else {
                        editor.putInt(StringConstant.WEEKSCORE,score + tempScore);
                        editor.putInt(StringConstant.WEEKOFYEAR,StaticMethod.getWeekOfYear());
                    }
                    editor.commit();
                }
            }
        });
    }

    private void updateTotalScore(int score1){
        int tempTotalScore = preferences.getInt(StringConstant.TOTALSCORE,0) + score1;
        editor = preferences.edit();
        editor.putInt(StringConstant.TOTALSCORE,tempTotalScore);
        editor.commit();
        if (!preferences.getString(StringConstant.TOTALSCOREID,"").equals("")){
            AVObject object = AVObject.createWithoutData("Records",preferences.getString(StringConstant.TOTALSCOREID,""));
            object.put("totalScore",tempTotalScore);
            object.saveInBackground();
        }
    }
}
