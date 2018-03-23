package tedking.lovewords;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

public class FinishExerciseActivity extends Activity {
    private TextView result_score;
    private int score;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_finishexercise);
        result_score = findViewById(R.id.result_score);
        score = getIntent().getIntExtra("score",10);
        result_score.setText(score+"");
        Toast.makeText(FinishExerciseActivity.this,getIntent().getBooleanExtra("wordFinish",false) + "",Toast.LENGTH_LONG).show();
    }

    //
    private void sendScores(){
        final AVObject dayScore = new AVObject("dayScore");
        dayScore.put("user", AVUser.getCurrentUser().getUsername());
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
