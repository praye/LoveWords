package tedking.lovewords;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

public class MissingLetterActivity extends AppCompatActivity {
    private LinearLayout linearLayout, englishLayout;
    private SQLiteDatabase database;
    private Button answer, next;
    private String englishWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_letter);
        findView();
        operation();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operation();
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (englishWord == null){
                    Toast.makeText(MissingLetterActivity.this,"App Error!", Toast.LENGTH_SHORT).show();
                }else {
                    englishLayout.removeAllViews();
                    createTextView(englishWord,false);
                }
            }
        });
    }

    private void findView(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        linearLayout = findViewById(R.id.linearLayout);
        englishLayout = findViewById(R.id.layout1);
        answer = findViewById(R.id.answer);
        next = findViewById(R.id.nextOne);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy(){
        if (database != null){
            database.close();
        }
        super.onDestroy();
    }

    private String [] getData(){
        String [] result = new String[2];
        if (database == null){
            File file = new File(getFilesDir()+"/databases/data.db");
            database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        }
        Cursor cursor = database.rawQuery("select english, chinese from words where status = 0 order by Random() limit 1",null);
        if (cursor != null){
            while (cursor.moveToNext()){
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
            }
        }else {
            result[1] = "No data";
        }
        return result;
    }
    private void createTextView(String str, boolean isMeaning) {
        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setTextSize(20);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (isMeaning) {
            params.gravity = Gravity.CENTER_HORIZONTAL;
            linearLayout.addView(textView, params);
        }else {
            englishLayout.addView(textView,params);
        }
    }
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < count; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    private class MyTextWatcher implements TextWatcher{
        private EditText[] editTexts;
        private String word;
        private int [] random;
        public MyTextWatcher(EditText[] editTexts, String word, int[] random){
            this.editTexts = editTexts;
            this.word = word;
            this.random = random;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() == 1){
                boolean inputFinished = true;
                for (int t = 0; t < editTexts.length; t ++){
                    if (editTexts[t].getText().toString().length() == 0){
                        editTexts[t].requestFocus();
                        inputFinished = false;
                        break;
                    }
                }
                if (inputFinished){
                    boolean right = true;
                    for (int t = 0; t < editTexts.length; t ++){
                        if (!editTexts[t].getText().toString().equals(word.charAt(random[t]) + "")){
                            right = false;
                            break;
                        }
                    }
                    if (right){
                        operation();
                        //Toast.makeText(MissingLetterActivity.this,"Correct",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MissingLetterActivity.this,"Wrong Answer",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void operation(){
        if (linearLayout.getChildCount() > 1) {
            linearLayout.removeViewAt(1);
        }
        englishLayout.removeAllViews();
        final String[] result = getData();
        englishWord = result[0];
        if (!result[0].equals("No data")) {
            int length = result[0].length();
            int temp =(int) (Math.random() * length / 3 + length / 3.0);
            final int number = temp == 0 ? 1 : temp;
            final EditText [] editText = new EditText[number];
            final int[] random = randomCommon(0, length-1, number);
            Arrays.sort(random);
            for (int i = 0; i < number; i ++){
                editText[i] = new EditText(MissingLetterActivity.this);
                editText[i].setSingleLine();
                editText[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
                editText[i].addTextChangedListener(new MyTextWatcher(editText, result[0], random));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }

            boolean flag;
            int count = 0;
            for (int i = 0; i < length; i++) {
                flag = false;
                for (int j = 0; j < number; j ++){
                    if (i == random[j]){
                        englishLayout.addView(editText[count]);
                        count ++;
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    createTextView(result[0].charAt(i)+"",false);
                }
            }
            createTextView(result[1], true);
        }else {
            Toast.makeText(MissingLetterActivity.this,result[0],Toast.LENGTH_SHORT).show();
        }
    }
}
