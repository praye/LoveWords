package tedking.lovewords;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.UpdatePasswordCallback;

import java.io.File;

public class ModifyPasswordActivity extends AppCompatActivity {
    private Button modifyPassword;
    private EditText originalPassword, newPassword,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        findView();
        modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptModifyPassword();
            }
        });
    }
    private void findView(){
        modifyPassword = findViewById(R.id.modifyPassword);
        originalPassword = findViewById(R.id.originalPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
    }
    private void attemptModifyPassword(){
        if (inputValid()){
            final AVUser user = AVUser.getCurrentUser();
            user.updatePasswordInBackground(originalPassword.getText().toString(), newPassword.getText().toString(), new UpdatePasswordCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null){
                        String name = user.getUsername();
                        user.logOut();
                        Toast.makeText(ModifyPasswordActivity.this,"Password modified",Toast.LENGTH_SHORT).show();
                        AVUser.logInInBackground(name, newPassword.getText().toString(), new LogInCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                if (e == null){
                                    Intent intent = new Intent(ModifyPasswordActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    ModifyPasswordActivity.this.finish();
                                }else {
                                    File file = new File(getFilesDir()+"/databases/data.db");
                                    SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("status","0");
                                    database.update("words",contentValues,null,null);
                                    database.close();
                                    SharedPreferences preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.clear();
                                    editor.putBoolean(StringConstant.FIRSTOPENAPP,false);
                                    editor.commit();
                                    Toast.makeText(ModifyPasswordActivity.this,"Please exit the app and login later",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        if (e.getCode() == 210){
                            Toast.makeText(ModifyPasswordActivity.this,"Original password incorrect",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ModifyPasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private boolean inputValid(){
        if (originalPassword.getText().toString().length() == 0){
            Toast.makeText(this,"Please input your original password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (originalPassword.getText().toString().length() <= 4){
            Toast.makeText(this,"Original password is too short!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.getText().toString().length() == 0){
            Toast.makeText(this,"Please input new password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.getText().toString().length() <= 4){
            Toast.makeText(this,"New password is too short!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.getText().toString().length() == 0){
            Toast.makeText(this,"Please input confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
            Toast.makeText(this,"Two passwords are not matched!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
