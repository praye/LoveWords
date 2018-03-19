package tedking.lovewords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean toGuide;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome);
        preferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        toGuide = preferences.getBoolean("guide",true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toGuide){
                    Intent intent = new Intent(WelcomeActivity.this,GuideActivity.class);
                    editor = preferences.edit();
                    editor.putBoolean("guide",false).commit();
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
