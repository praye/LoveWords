package tedking.lovewords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

public class Exercise extends Activity {
    private Button [] choices = new Button[4];
    private TextView word, timer, score;
    private String [] words;
    private String [] explains;
    private String result = "";
    private boolean finish = false;
    private SharedPreferences preferences;
    private Drawable drawable;
    private Handler handler = new Handler();
    private int questionNumber, actualQuestionNumber, questionNow = 0, position, scoreNumber;
    private long firstPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_exercise);
        findView();
        getData();
        position = setText();
        for (int i = 0; i < 4; i ++)
            choices[i].setOnClickListener(onClickListener);
        countDownTimer.start();
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.choice0:
                    operation(0);
                    break;
                case R.id.choice1:
                    operation(1);
                    break;
                case R.id.choice2:
                    operation(2);
                    break;
                case R.id.choice3:
                    operation(3);
                    break;
            }
        }
    };
    private void findView(){
        word = findViewById(R.id.testWord);
        score = findViewById(R.id.score);
        timer = findViewById(R.id.timer);
        choices[0] = findViewById(R.id.choice0);
        choices[1] = findViewById(R.id.choice1);
        choices[2] = findViewById(R.id.choice2);
        choices[3] = findViewById(R.id.choice3);
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME, Context.MODE_PRIVATE);
        questionNumber = preferences.getInt(StringConstant.QUESTIONNUMBER, 10);
        words = new String[questionNumber];
        explains = new String[questionNumber * 4];
    }

    private void getData(){
        int dataNumber, expected_number = questionNumber * 4;
        File file = new File(getFilesDir() + "/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = database.rawQuery("select * from words where status = ? order by RANDOM() limit " + expected_number, new String[]{"0"});
        dataNumber = cursor.getCount();

        //when number of result equals to expected number
        if (dataNumber == expected_number){
            actualQuestionNumber = questionNumber;
            for (int i = 0; i < expected_number; i ++){
                cursor.moveToNext();
                if (i < questionNumber){
                    words[i] = cursor.getString(0);
                }
                explains[i] = cursor.getString(2);
            }
        }else if (dataNumber > questionNumber){  //when number of result less than expected number but larger than question number
            actualQuestionNumber = questionNumber;
            for (int i = 0; i < questionNumber; i ++){
                cursor.moveToNext();
                words[i] = cursor.getString(0);
                explains[i] = cursor.getString(2);
            }
            int restNumber = questionNumber * 3;
            cursor = database.rawQuery("select * from words where status = ? order by RANDOM() limit " + restNumber, new String[]{"1"});
            for (int i = questionNumber; i < expected_number; i ++){
                cursor.moveToNext();
                explains[i] = cursor.getString(2);
            }
        }else { //when number of result less than or equals to question number but larger than zero
            actualQuestionNumber = cursor.getCount();
            finish = true;
            for (int i = 0; i < dataNumber; i++){
                cursor.moveToNext();
                words[i] = cursor.getString(0);
                explains[i] = cursor.getString(2);
            }
            int restNumber = expected_number - cursor.getCount();
            cursor = database.rawQuery("select * from words where status = ? order by RANDOM() limit ?", new String[]{"1",String.valueOf(restNumber)});
            for (int i = dataNumber; i < expected_number; i ++){
                cursor.moveToNext();
                explains[i] = cursor.getString(2);
            }
        }
        database.close();
    }
    private int setText(){
        score.setText(scoreNumber + "");
        word.setText(words[questionNow]);
        int position =(int) (3 * Math.random());
        switch (position){
            case 0:
                choices[0].setText(explains[questionNow]);
                choices[1].setText(explains[questionNow * 4 + 1]);
                choices[2].setText(explains[questionNow * 4 + 2]);
                choices[3].setText(explains[questionNow * 4 + 3]);
                break;
            case 1:
                choices[1].setText(explains[questionNow]);
                choices[0].setText(explains[questionNow * 4 + 1]);
                choices[2].setText(explains[questionNow * 4 + 2]);
                choices[3].setText(explains[questionNow * 4 + 3]);
                break;
            case 2:
                choices[2].setText(explains[questionNow]);
                choices[0].setText(explains[questionNow * 4 + 1]);
                choices[1].setText(explains[questionNow * 4 + 2]);
                choices[3].setText(explains[questionNow * 4 + 3]);
                break;
            case 3:
                choices[3].setText(explains[questionNow]);
                choices[0].setText(explains[questionNow * 4 + 1]);
                choices[1].setText(explains[questionNow * 4 + 2]);
                choices[2].setText(explains[questionNow * 4 + 3]);
                break;
        }
        return position;
    }
    private void operation(int i){
        questionNow ++;
        if (questionNow == actualQuestionNumber){
            Intent intent = new Intent(Exercise.this,FinishExerciseActivity.class);
            if (i == position){
                scoreNumber += 10;
                result = result + word.getText() + ",";
            }else {
                countDownTimer.cancel();
                choices[position].setBackgroundColor(Color.argb(255,137,207,240));
                for (int j = 0; j < 4; j ++){
                    choices[j].setClickable(false);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < 4; j ++){
                            choices[j].setClickable(true);
                        }
                    }
                },2000);
            }
            intent.putExtra("wordFinish", finish && scoreNumber / 10 == actualQuestionNumber);
            intent.putExtra("score",scoreNumber);
            intent.putExtra("words",result);
            startActivity(intent);
            finish();
        }else {
            if (i == position){
                scoreNumber += 10;
                result = result + word.getText() + ",";
                position = setText();
                countDownTimer.start();
            }else {
                countDownTimer.cancel();
                drawable = choices[position].getBackground();
                choices[position].setBackgroundColor(Color.argb(255,137,207,240));
                for (int j = 0; j < 4; j ++){
                    choices[j].setClickable(false);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        choices[position].setBackground(drawable);
                        for (int j = 0; j < 4; j ++){
                            choices[j].setClickable(true);
                        }
                        position = setText();
                        countDownTimer.start();
                    }
                },2000);
            }
        }
    }

    private CountDownTimer countDownTimer = new CountDownTimer(11000,1000) {
        @Override
        public void onTick(long l) {
            timer.setText((l / 1000) + "s");
        }

        @Override
        public void onFinish() {
            operation(5);
        }
    };

    @Override
    public void onDestroy(){
        countDownTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstPressTime > 2000) {
            Toast.makeText(Exercise.this, "再按一次退出程序，退出后本次测试无效", Toast.LENGTH_SHORT).show();
            firstPressTime = secondTime;
        } else {
            super.onBackPressed();
        }
    }

}
