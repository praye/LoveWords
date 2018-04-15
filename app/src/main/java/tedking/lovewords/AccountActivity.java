package tedking.lovewords;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountActivity extends Activity {
    private ImageView back;
    private Button modifyPassword,modifyEmail;
    private TextView email, totalScore, learningDay;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,MODE_PRIVATE);
        findView();
        setText();
        modifyPassword.setOnClickListener(listener);
        modifyEmail.setOnClickListener(listener);
        back.setOnClickListener(listener);
    }
    private void findView(){
        back = findViewById(R.id.backToMain);
        modifyEmail = findViewById(R.id.modifyEmail_Item);
        modifyPassword = findViewById(R.id.modifyPassword_item);
        email = findViewById(R.id.e_mail_item);
        totalScore = findViewById(R.id.totalScore);
        learningDay = findViewById(R.id.learning_days);
    }
    private void setText(){
        email.setText(preferences.getString(StringConstant.EMAIL,"none"));
        totalScore.setText(preferences.getInt(StringConstant.TOTALSCORE,0)+"");
        learningDay.setText(preferences.getInt(StringConstant.TOTALLOGIN,1)+"");
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.backToMain:
                    startActivity(new Intent(AccountActivity.this,MainActivity.class));
                    AccountActivity.this.finish();
                    break;
                case R.id.modifyEmail_Item:
                    break;
                case R.id.modifyPassword_item:
                    startActivity(new Intent(AccountActivity.this,ModifyPasswordActivity.class));
                    AccountActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };
}
