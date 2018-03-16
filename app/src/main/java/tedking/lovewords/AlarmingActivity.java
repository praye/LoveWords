package tedking.lovewords;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmingActivity extends Activity {
    private MediaPlayer mediaPlayer;
    private static final long EXECUTE_TIME = 180000;
    private Vibrator vibrator;
    private Handler handler = new Handler();
    private Button exit;
    long [] pattern = {1000,1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarming);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        Intent intent = new Intent();
        intent.setClass(AlarmingActivity.this,AlarmService.class);
        startService(intent);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        exit = findViewById(R.id.exit);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },EXECUTE_TIME);
        mediaPlayer = MediaPlayer.create(this,R.raw.song);
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
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
        vibrator.cancel();
    }
}
