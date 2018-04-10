package tedking.lovewords;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.feedback.FeedbackAgent;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ActionBar ab;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private int currentItem;
    private AlarmFragment alarmFragment;
    private WordSearchFragment wordSearchFragment;
    private FeedbackAgent agent;
    private SharedPreferences preferences;
    private int startFragmentId;
    private SharedPreferences.Editor editor;
    private TextView username_nav;
    private View nav_header;
    static Activity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        findView();
        getTotalScoreId();
        setTotalLogin();
        agent = new FeedbackAgent(MainActivity.this);
        agent.sync();
    }

    //inflate sample options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actions,menu);
        return true;
    }

    //set
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (preferences.getInt(StringConstant.STARTFRAGMENTID,0)){
            case 0:
                menu.findItem(R.id.menu_search).setChecked(true);
                break;
            case 1:
                menu.findItem(R.id.menu_alarm).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.menu_game).setChecked(true);
                break;
        }
        switch (preferences.getInt(StringConstant.SONGID,0)){
            case 0:
                menu.findItem(R.id.menu_song0).setChecked(true);
                break;
            case 1:
                menu.findItem(R.id.menu_song1).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.menu_song2).setChecked(true);
                break;
        }
        switch (preferences.getInt(StringConstant.QUESTIONNUMBER,10)){
            case 5:
                menu.findItem(R.id.menu_number_5).setChecked(true);
                break;
            case 10:
                menu.findItem(R.id.menu_number_10).setChecked(true);
                break;
            case 15:
                menu.findItem(R.id.menu_number_15).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                hideInputKeyboard(MainActivity.this);
                return true;
            case R.id.menu_search:
                preferenceEdit(StringConstant.STARTFRAGMENTID,0);
                break;
            case R.id.menu_alarm:
                preferenceEdit(StringConstant.STARTFRAGMENTID,1);
                break;
            case R.id.menu_game:
                preferenceEdit(StringConstant.STARTFRAGMENTID,2);
                break;
            case R.id.menu_song0:
                preferenceEdit(StringConstant.SONGID,0);
                break;
            case R.id.menu_song1:
                preferenceEdit(StringConstant.SONGID,1);
                break;
            case R.id.menu_song2:
                preferenceEdit(StringConstant.SONGID,2);
                break;
            case R.id.menu_number_5:
                preferenceEdit(StringConstant.QUESTIONNUMBER,5);
                break;
            case R.id.menu_number_10:
                preferenceEdit(StringConstant.QUESTIONNUMBER,10);
                break;
            case R.id.menu_number_15:
                preferenceEdit(StringConstant.QUESTIONNUMBER,15);
                break;
            case R.id.menu_recovery:
                startActivity(new Intent(this,RecoveryActivity.class));
                break;
            case R.id.menu_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        nav_header = navigationView.getHeaderView(0);
        username_nav = nav_header.findViewById(R.id.username_nav);
        username_nav.setText(AVUser.getCurrentUser().getUsername());

        viewPager = findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        currentItem = viewPager.getCurrentItem();

        fab = findViewById(R.id.fab);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                if (position == 0){
                    fab.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_search);
                }else if (position == 1){
                    fab.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_add);
                    hideInputKeyboard(MainActivity.this);
                }else {
                    fab.setVisibility(View.GONE);
                    hideInputKeyboard(MainActivity.this);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentItem == 0){
                    wordSearchFragment.search.performClick();
                }else if(currentItem == 1){
                    alarmFragment.updateData.performClick();
                }
            }
        });

        // tab on the top, through viewpager to switch  "search", "alarm", "game" pages
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (currentItem == 0){
            fab.setImageResource(R.drawable.ic_search);
        }else if (currentItem == 1){
            fab.setImageResource(R.drawable.ic_add);
        }else {
            fab.setVisibility(View.GONE);
        }
        preferences = getSharedPreferences(StringConstant.SHAREDPREFERENCENAME, Context.MODE_PRIVATE);
        startFragmentId = preferences.getInt(StringConstant.STARTFRAGMENTID,0);
        viewPager.setCurrentItem(startFragmentId);
        editor = preferences.edit();
        if (StaticMethod.getDayOfYear() != preferences.getInt(StringConstant.DAYOFYEAR,-1)){
            editor.putInt(StringConstant.DAYSCORE,0);
            editor.putInt(StringConstant.DAYOFYEAR,StaticMethod.getDayOfYear());
        }
        if (StaticMethod.getWeekOfYear() != preferences.getInt(StringConstant.WEEKOFYEAR,-1)){
            editor.putInt(StringConstant.WEEKOFYEAR,StaticMethod.getWeekOfYear());
            editor.putInt(StringConstant.WEEKSCORE,0);
        }
    }

    //called by findView();
    private void setupViewPager(ViewPager viewPager) {
        wordSearchFragment = new WordSearchFragment();
        alarmFragment = new AlarmFragment();
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(wordSearchFragment, "Search");
        adapter.addFragment(alarmFragment, "Alarm");
        adapter.addFragment(new GameFragment(), "Game");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        //Toast.makeText(MainActivity.this, menuItem.getTitle(),Toast.LENGTH_LONG).show();
                        switch (menuItem.getTitle().toString()){
                            case "Words I'm Learning":
                                toWordsActivity("0");
                                break;
                            case "Words I've mastered":
                                toWordsActivity("1");
                                break;
                            case "My Achievements":
                                startActivity(new Intent(MainActivity.this,AchievementActivity.class));
                                break;
                            case "Log Out":
                                logOut();
                                break;
                            case "Rate the App" :
                                rateApp();
                                break;
                            case "Submit Feedback":
                                agent.startDefaultThreadActivity();
                                break;
                            case "Today":
                                toChampionActivity("Today");
                                break;
                            case "This Week":
                                toChampionActivity("This Week");
                                break;
                        }
                        return true;
                    }
                });
    }

    //Adapter class help to set the fragment
    static class Adapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment  fragment,String title){
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }


        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onBackPressed(){
        if (mDrawerLayout.isDrawerOpen(navigationView)){
            mDrawerLayout.closeDrawers();
        }else {
            super.onBackPressed();
        }
    }

    private void rateApp(){
        try{
            Uri uri = Uri.parse("market://details?id="+getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch(Exception e){
            Toast.makeText(MainActivity.this, "No App Market is installed in your device", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void preferenceEdit(String sting, int number){
        editor = preferences.edit();
        editor.putInt(sting,number);
        editor.commit();
    }

    private void toChampionActivity(String stage){
        Intent intent = new Intent(MainActivity.this,ChampionActivity.class);
        intent.putExtra("stage",stage);
        startActivity(intent);
    }

    private void toWordsActivity(String status){
        Intent intent = new Intent(MainActivity.this,WordsActivity.class);
        intent.putExtra("status",status);
        startActivity(intent);
    }

    private void logOut(){
        if (AVUser.getCurrentUser()!= null){
            new AlertDialog.Builder(MainActivity.this).setMessage("Sure to login out? The learning history will be cleared.").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AVUser.getCurrentUser().logOut();
                    File file = new File(getFilesDir()+"/databases/data.db");
                    SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("status","0");
                    database.update("words",contentValues,null,null);
                    database.close();
                    editor = preferences.edit();
                    editor.clear();
                    editor.putBoolean(StringConstant.FIRSTOPENAPP,false);
                    editor.commit();
                    Toast.makeText(MainActivity.this,"You have logged out your account!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).show();
        }
        else{
            Toast.makeText(MainActivity.this,"You have not logged in yet",Toast.LENGTH_LONG).show();
        }
    }

    private void getTotalScoreId(){
        if (preferences.getString(StringConstant.TOTALSCOREID,"").equals("")){
            if (isNetworkAvailable()){
                AVQuery<AVObject> query = new AVQuery<>("Records");
                query.whereEqualTo("user",AVUser.getCurrentUser().getUsername());
                query.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        if (e == null){
                            if (avObject == null){
                                System.out.println("getTotalScoreId, but is null");
                                final AVObject object = new AVObject("Records");
                                object.put("totalScore",preferences.getInt(StringConstant.TOTALSCORE,0));
                                object.put("user",AVUser.getCurrentUser().getUsername());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null){
                                            editor = preferences.edit();
                                            editor.putString(StringConstant.TOTALSCOREID,object.getObjectId());
                                            editor.commit();
                                            System.out.println("getTotalScoreId, null, but is set");
                                        }
                                    }
                                });
                            }else {
                                System.out.println("getTotalScoreId, not null");
                                editor = preferences.edit();
                                editor.putInt(StringConstant.TOTALSCORE,avObject.getInt("totalScore"));
                                editor.putInt(StringConstant.TOTALLOGIN,avObject.getInt("continueDays") + preferences.getInt(StringConstant.TOTALLOGIN,0));
                                editor.putString(StringConstant.TOTALSCOREID,avObject.getObjectId());
                                editor.commit();
                            }
                        }
                    }
                });
            }
        }
    }

    private void setTotalLogin(){
        if (StaticMethod.getDayOfYear() != preferences.getInt(StringConstant.LASTLOGINTIME,-1)){
            System.out.println("in setTotalLogin");
            int total = preferences.getInt(StringConstant.TOTALLOGIN,0) + 1;
            System.out.println(total);
            editor = preferences.edit();
            editor.putInt(StringConstant.TOTALLOGIN, total);
            editor.putInt(StringConstant.LASTLOGINTIME,StaticMethod.getDayOfYear());
            editor.commit();
            if (!preferences.getString(StringConstant.TOTALSCOREID,"").equals("")){
                AVObject object = AVObject.createWithoutData("Records",preferences.getString(StringConstant.TOTALSCOREID,""));
                object.put("continueDays",total);
                object.saveInBackground();
                System.out.println("saveTotalLogin");
            }
        }
    }


    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null){
            return false;
        }else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null){
                return info.isConnected();
            }else {
                return false;
            }
        }
    }

    public  void hideInputKeyboard(final Context context) {
        final Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager mInputKeyBoard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (activity.getCurrentFocus() != null) {
                    mInputKeyBoard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        });
    }
}
