package tedking.lovewords;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;


import java.util.Calendar;
import java.util.Date;

public class ChampionActivity extends Activity {
    private TextView champion_username, champion_score;
    private String championOf;

    //just for test, it will delete anytime;
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_champion);
        championOf = getIntent().getStringExtra("stage");
        champion_username = findViewById(R.id.champion_username);
        champion_score = findViewById(R.id.champion_score);
        testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(listener);
        getChampionData(championOf);
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getChampionData(championOf);
        }
    };
    private void getChampionData(String string){
        Calendar calendar = Calendar.getInstance();
        Date startDate = new Date(), endDate = new Date();
        if (string.equals("Today")){
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            startDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH,1);
            endDate = calendar.getTime();
        }
        AVQuery<AVObject> query = new AVQuery<>("dayScore");
        query.whereGreaterThanOrEqualTo("createdAt",startDate);
        query.whereLessThan("createdAt",endDate);
        query.orderByDescending("score");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是符合条件的第一个 AVObject
                if (e == null){
                    champion_username.setText(avObject.getString("user"));
                    champion_score.setText(avObject.getInt("score") + "");
                }else {
                    e.printStackTrace();
                }
            }
        });
    }
}
