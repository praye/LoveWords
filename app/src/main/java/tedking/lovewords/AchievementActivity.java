package tedking.lovewords;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.widget.ImageView;

public class AchievementActivity extends Activity {
    private ImageView [] days = new ImageView[6];
    private ImageView [] level = new ImageView[9];
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_achievement);
        findView();
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,MODE_PRIVATE);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        setAchievementColor("days",preferences.getInt(StringConstant.TOTALLOGIN,0),filter);
        setAchievementColor("score",preferences.getInt(StringConstant.TOTALSCORE,0),filter);
    }
    private void findView(){
        days[0] = findViewById(R.id.continuous01);
        days[1] = findViewById(R.id.continuous02);
        days[2] = findViewById(R.id.continuous03);
        days[3] = findViewById(R.id.continuous04);
        days[4] = findViewById(R.id.continuous05);
        days[5] = findViewById(R.id.continuous06);
        level[0] = findViewById(R.id.level01);
        level[1] = findViewById(R.id.level02);
        level[2] = findViewById(R.id.level03);
        level[3] = findViewById(R.id.level04);
        level[4] = findViewById(R.id.level05);
        level[5] = findViewById(R.id.level06);
        level[6] = findViewById(R.id.level07);
        level[7] = findViewById(R.id.level08);
        level[8] = findViewById(R.id.level09);
    }

    private void setAchievementColor(String achievement, int num, ColorMatrixColorFilter filter){
        if (achievement.equals("days")){
            if (num < 365)
                days[5].setColorFilter(filter);
            if (num < 100)
                days[4].setColorFilter(filter);
            if (num < 60)
                days[3].setColorFilter(filter);
            if (num < 30)
                days[2].setColorFilter(filter);
            if (num < 14)
                days[1].setColorFilter(filter);
            if (num < 7)
                days[0].setColorFilter(filter);
        }else {
            if (num < 70000)
                level[8].setColorFilter(filter);
            if (num < 50000)
                level[7].setColorFilter(filter);
            if (num < 30000)
                level[6].setColorFilter(filter);
            if (num < 10000)
                level[5].setColorFilter(filter);
            if (num < 5000)
                level[4].setColorFilter(filter);
            if (num < 2000)
                level[3].setColorFilter(filter);
            if (num < 1000)
                level[2].setColorFilter(filter);
            if (num < 500)
                level[1].setColorFilter(filter);
            if (num < 200)
                level[0].setColorFilter(filter);
        }
    }
}
