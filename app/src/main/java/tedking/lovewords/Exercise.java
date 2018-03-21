package tedking.lovewords;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

public class Exercise extends Activity {
    private Button [] choices = new Button[4];
    private TextView word;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_exercise);
        findView();
        choices[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendScores();
            }
        });
        choices[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateScores();
            }
        });
    }
    private void findView(){
        word = findViewById(R.id.testWord);
        choices[0] = findViewById(R.id.choice0);
        choices[1] = findViewById(R.id.choice1);
        choices[2] = findViewById(R.id.choice2);
        choices[3] = findViewById(R.id.choice3);
    }
    private void sendScores(){
        final AVObject dayScore = new AVObject("dayScore");
        dayScore.put("user", AVUser.getCurrentUser().getUsername());
        dayScore.put("score",100);
        dayScore.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    objectId = dayScore.getObjectId();
                    Toast.makeText(Exercise.this,"数据上传成功",Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(Exercise.this,"网络错误，请稍后再试",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Exercise.this,"数据修改成功",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Exercise.this,"数据修改错误",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
