package tedking.lovewords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

public class AccountActivity extends Activity {
    private ImageView back;
    private Button modifyPassword,modifyEmail;
    private TextView email, email_dialog, totalScore, learningDay;
    private SharedPreferences preferences;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,MODE_PRIVATE);
        findView();
        setText();
        buildDialog();
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
        email.setText(AVUser.getCurrentUser().getEmail());
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
                    dialog.show();
                    break;
                case R.id.modifyPassword_item:
                    startActivity(new Intent(AccountActivity.this,ModifyPasswordActivity.class));
                    AccountActivity.this.finish();
                    break;
                case R.id.cancel_dialog:
                    dialog.dismiss();
                    break;
                case R.id.confirm_dialog:
                    String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
                    if (email_dialog.getText().toString().matches(strPattern)){
                        setEmail();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(AccountActivity.this, "Please input correct email address",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private void buildDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_email_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        Button cancel = view.findViewById(R.id.cancel_dialog);
        Button confirm = view.findViewById(R.id.confirm_dialog);
        email_dialog = view.findViewById(R.id.emailAddress_dialog);
        confirm.setOnClickListener(listener);
        cancel.setOnClickListener(listener);
    }
    private void setEmail(){
        final AVUser user = AVUser.getCurrentUser();
        if (user.getEmail().equals(email_dialog.getText().toString())){
            Toast.makeText(AccountActivity.this,"You have set this email already.",Toast.LENGTH_SHORT).show();
        }else {
            user.setEmail(email_dialog.getText().toString());
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        Toast.makeText(AccountActivity.this,"We have sent an email to your email box.Please verify your email address!",Toast.LENGTH_SHORT).show();
                        email.setText(user.getEmail());
                    }else {
                        Toast.makeText(AccountActivity.this,"Something Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
