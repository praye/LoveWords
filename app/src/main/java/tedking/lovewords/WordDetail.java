package tedking.lovewords;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.IllegalFormatCodePointException;

import javax.net.ssl.HttpsURLConnection;

public class WordDetail extends AppCompatActivity {
    private String word,pronunciation;
    private Dialog dialog;
    private LinearLayout layout;
    private TextView wordItself, pronunciation_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        word = getIntent().getStringExtra("word");
        pronunciation = getIntent().getStringExtra("pronunciation");
        findView();
        showDialog();
        new CallbackTask().execute(dictionaryEntries(word));
    }

    private void findView(){
        layout = findViewById(R.id.word_detail_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        wordItself = findViewById(R.id.word_detail_itself);
        pronunciation_tv = findViewById(R.id.word_detail_pronunciation);
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

    private String dictionaryEntries(String word) {
        final String language = "en";
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }


    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "7ffba32e";
            final String app_key = "1eabf07b0b57c367b8ebe823dbe83dac";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            wordItself.setText(word);
            pronunciation_tv.setText(pronunciation);
            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray array = resultObject.getJSONArray("results");
                JSONObject object = array.getJSONObject(0);
                JSONArray lexicalEntries = object.getJSONArray("lexicalEntries");
                for (int i = 0; i < lexicalEntries.length(); i ++){
                    JSONObject lexicalEntry = lexicalEntries.getJSONObject(i);
                    JSONArray entries = lexicalEntry.getJSONArray("entries");
                    for(int j = 0; j < entries.length(); j ++){
                        JSONObject entry = entries.getJSONObject(j);
                        JSONArray senses = entry.getJSONArray("senses");
                        for (int k = 0; k < senses.length(); k ++){
                            JSONObject sense = senses.getJSONObject(k);
                            JSONArray definitions = sense.getJSONArray("definitions");
                            for (int l = 0; l < definitions.length(); l ++){
                                System.out.println(definitions.optString(l));
                                createTextView(definitions.optString(l),true);
                            }
                            try {
                                JSONArray examples = sense.getJSONArray("examples");
                                for (int l = 0; l < examples.length(); l++) {
                                    JSONObject example = examples.getJSONObject(l);
                                    System.out.println(example.optString("text"));
                                    createTextView(example.optString("text"), false);
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
                dialog.dismiss();
            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(WordDetail.this,"Network or server error! Please try it later", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        }
    }



    private void createTextView(String str,boolean isMeaning) {
        TextView textView = new TextView(this);
        textView.setPadding(16, 16, 16, 16);
        textView.setText(str);
        if (isMeaning) {
            textView.setTextSize(21);
            textView.setTextColor(Color.rgb(21,101, 192));
        }else {
            textView.setTextSize(15);
        }
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 10, 30, 0);
        layout.addView(textView,params);
    }

    private void showDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.recovery_loading,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView notice = view.findViewById(R.id.noticeText);
        notice.setText("Loading ...");
        builder.setView(view);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

}
