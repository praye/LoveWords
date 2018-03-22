package tedking.lovewords;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.feedback.FeedbackAgent;

import java.util.ArrayList;
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
    public static final String STARTFRAGMENTID = "startFragmentId", SONGID = "songId", QUESTIONNUMBER = "question_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
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
        switch (preferences.getInt(STARTFRAGMENTID,0)){
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
        switch (preferences.getInt(SONGID,0)){
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
        switch (preferences.getInt(QUESTIONNUMBER,10)){
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
                return true;
            case R.id.menu_search:
                preferenceEdit(STARTFRAGMENTID,0);
                break;
            case R.id.menu_alarm:
                preferenceEdit(STARTFRAGMENTID,1);
                break;
            case R.id.menu_game:
                preferenceEdit(STARTFRAGMENTID,2);
                break;
            case R.id.menu_song0:
                preferenceEdit(SONGID,0);
                break;
            case R.id.menu_song1:
                preferenceEdit(SONGID,1);
                break;
            case R.id.menu_song2:
                preferenceEdit(SONGID,2);
                break;
            case R.id.menu_number_5:
                preferenceEdit(QUESTIONNUMBER,5);
                break;
            case R.id.menu_number_10:
                preferenceEdit(QUESTIONNUMBER,10);
                break;
            case R.id.menu_number_15:
                preferenceEdit(QUESTIONNUMBER,15);
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
                    fab.setImageResource(R.drawable.ic_search);
                }else {
                    fab.setImageResource(R.drawable.ic_add);
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
        }else {
            fab.setImageResource(R.drawable.ic_add);
        }
        preferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        startFragmentId = preferences.getInt(STARTFRAGMENTID,0);
        viewPager.setCurrentItem(startFragmentId);

    }

    //called by findView();
    private void setupViewPager(ViewPager viewPager) {
        wordSearchFragment = new WordSearchFragment();
        alarmFragment = new AlarmFragment();
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(wordSearchFragment, "Search");
        adapter.addFragment(alarmFragment, "Alarm");
        adapter.addFragment(new WordListFragment(), "Game");
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
                                break;
                            case "Words I've mastered":
                                break;
                            case "My Achievements":
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

    private void logOut(){
        if (AVUser.getCurrentUser()!= null){
            new AlertDialog.Builder(MainActivity.this).setMessage("We do not advise you to log out your account, it may cause your data lost").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AVUser.getCurrentUser().logOut();
                    Toast.makeText(MainActivity.this,"You have logged out your account!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).show();
        }
        else{
            Toast.makeText(MainActivity.this,"You have not logged in yet",Toast.LENGTH_LONG).show();
        }
    }
}
