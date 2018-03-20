package tedking.lovewords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.avos.avoscloud.AVUser;

public class WelcomeActivity extends Activity {
    private boolean toGuide;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome);
        toGuide = AVUser.getCurrentUser() == null;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toGuide){
                    Intent intent = new Intent(WelcomeActivity.this,GuideActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
                else{
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            }
        },1000);
    }
}
