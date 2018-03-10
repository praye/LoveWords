package tedking.lovewords;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private Dialog dialog;
    private Button [] days = new Button[7];
    private boolean []repeat = new boolean[]{false,false,false,false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
    }

    //inflate sample options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions,menu);
        return true;
    }

    //set
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                menu.findItem(R.id.menu_night_mode_system).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
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
            case R.id.menu_night_mode_system:
                setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case R.id.menu_night_mode_day:
                setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.menu_night_mode_night:
                setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.menu_night_mode_auto:
                setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
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

        buildDialog();

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

        //to do
        fab.setOnClickListener(onClickListener);

        // tab on the top, through viewpager to switch  "search", "alarm", "game" pages
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (currentItem == 0){
            fab.setImageResource(R.drawable.ic_search);
        }else {
            fab.setImageResource(R.drawable.ic_add);
        }
    }

    //Helper function to set Mode
    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    //called by findView();
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new WordSearchFragment(), "Search");
        adapter.addFragment(new AlaramFragment(), "Alarm");
        adapter.addFragment(new WordListFragment(), "Game");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
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

    private void buildDialog(){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.alarm_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        dialog = builder.create();
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        days[0] = view.findViewById(R.id.Sunday);
        days[1] = view.findViewById(R.id.Monday);
        days[2] = view.findViewById(R.id.Tuesday);
        days[3] = view.findViewById(R.id.Wednesday);
        days[4] = view.findViewById(R.id.Thursday);
        days[5] = view.findViewById(R.id.Friday);
        days[6] = view.findViewById(R.id.Saturday);
        cancel.setOnClickListener(onClickListener);
        confirm.setOnClickListener(onClickListener);
        for (int i = 0; i < 7; i ++){
            days[i].setOnClickListener(onClickListener);
        }
    }
    private void resetRepeate(){
        for (int i = 0; i < 7; i ++){
            repeat[i] = false;
            days[i].setTextColor(0xFF000000);
        }
    }
    private void setRepeat(int i){
        if (repeat[i]){
            days[i].setTextColor(0xFF000000);
        }else {
            days[i].setTextColor(0xFFFFFFFF);
        }
        repeat[i] = !repeat[i];
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.fab:
                    if (currentItem == 0){
                        Snackbar.make(view,"here's search fragment",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();
                    }else if (currentItem == 1){
                        dialog.show();
                    }
                    break;
                case R.id.cancel:
                    resetRepeate();
                    dialog.dismiss();
                    break;
                case R.id.confirm:
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this,"Confirm",Toast.LENGTH_LONG).show();
                    resetRepeate();
                    break;
                case R.id.Sunday:
                    setRepeat(0);
                    break;
                case R.id.Monday:
                    setRepeat(1);
                    break;
                case R.id.Tuesday:
                    setRepeat(2);
                    break;
                case R.id.Wednesday:
                    setRepeat(3);
                    break;
                case R.id.Thursday:
                    setRepeat(4);
                    break;
                case R.id.Friday:
                    setRepeat(5);
                    break;
                case R.id.Saturday:
                    setRepeat(6);
                    break;
            }
        }
    };

}
