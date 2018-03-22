package tedking.lovewords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class AlarmingActivity extends Activity {
    private MediaPlayer mediaPlayer;
    private static final long EXECUTE_TIME = 180000;
    private Vibrator vibrator;
    private Handler handler = new Handler();
    private SharedPreferences preferences;
    private Button exit, todo;
    long [] pattern = {1000,1000};
    private static final String SONGID = "songId", TESTFINISH = "testFinish";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarming);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        final Intent intent = new Intent();
        intent.setClass(AlarmingActivity.this,AlarmService.class);
        startService(intent);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        exit = findViewById(R.id.exit);
        todo = findViewById(R.id.todo);
        preferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },EXECUTE_TIME);
        if (preferences.getInt(SONGID, 0) == 0){
            mediaPlayer = MediaPlayer.create(this,R.raw.song0);
        }else if (preferences.getInt(SONGID, 0) == 1){
            mediaPlayer = MediaPlayer.create(this,R.raw.song1);
        }else {
            mediaPlayer = MediaPlayer.create(this,R.raw.song2);
        }
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        vibrator.vibrate(pattern, 0);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                vibrator.cancel();
                finish();
            }
        });
        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                vibrator.cancel();
                if (preferences.getBoolean(TESTFINISH, false)){
                    Toast.makeText(AlarmingActivity.this,"词库已经做完",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent1 = new Intent();
                    intent1.setClass(AlarmingActivity.this,Exercise.class);
                    startActivity(intent1);
                }
                finish();
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
        vibrator.cancel();
    }
}
