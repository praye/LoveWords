package tedking.lovewords;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/5.
 */

public class AlaramFragment extends Fragment {
    private ListView alarmList;
    private SimpleAdapter adapter;
    public Button updateData;
    private Dialog dialog;
    private TextView noAlarmNotice;
    private TimePicker timePicker;
    private int hour, minute;
    private Button [] days = new Button[7];
    List<Map<String,Object>> data = new ArrayList<>();
    private boolean []repeat = new boolean[]{false,false,false,false,false,false,false};
    private String [] repeatDay = {"日 ","一 ","二 ","三 ","四 ","五 ","六 "};
    private static String alarmExist = "该时间的闹钟已存在，请重新设置", alarmTimeTooNear = "您设置的闹钟太相近，请重新设置", alarmSet = "闹钟已成功设置";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.alarm_fragment_layout, container,false);
        alarmList = view.findViewById(R.id.alarm_list);
        updateData = view.findViewById(R.id.updateData);
        noAlarmNotice = view.findViewById(R.id.no_alarm_notice);
        initData();
        alarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final Map<String, String> map = (Map< String, String>)adapter.getItem(position);
                new AlertDialog.Builder(getContext()).setMessage("是否删除").setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.remove(position);
                        adapter.notifyDataSetChanged();
                        deleteAlarmFromDatabase(map.get("time"));
                        if (data.size() == 0){
                            noAlarmNotice.setVisibility(View.VISIBLE);
                        }
                    }
                }).show();
                return true;
            }
        });
        buildDialog();
        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.updateData:
                    dialog.show();
                    break;
                case R.id.cancel:
                    resetRepeat();
                    dialog.dismiss();
                    break;
                case R.id.confirm:
                    dialog.dismiss();
                    saveAlarmTime();
                    resetRepeat();
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

    private void buildDialog(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.alarm_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        days[0] = view.findViewById(R.id.Sunday);
        days[1] = view.findViewById(R.id.Monday);
        days[2] = view.findViewById(R.id.Tuesday);
        days[3] = view.findViewById(R.id.Wednesday);
        days[4] = view.findViewById(R.id.Thursday);
        days[5] = view.findViewById(R.id.Friday);
        days[6] = view.findViewById(R.id.Saturday);
        timePicker = view.findViewById(R.id.timePicker);
        updateData.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        confirm.setOnClickListener(onClickListener);
        for (int i = 0; i < 7; i ++){
            days[i].setOnClickListener(onClickListener);
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
    private void resetRepeat(){
        for (int i = 0; i < 7; i ++){
            repeat[i] = false;
            days[i].setTextColor(0xFF000000);
        }
    }

    private void getTimeFromTimePicker(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        }
        else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
    }

    //call when user confirm to add a alarm, it will save data to database and notify adapter to refresh list view
    private void saveAlarmTime(){
        File file = new File(getContext().getFilesDir()+"/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        getTimeFromTimePicker();
        Cursor cursor = database.rawQuery("select * from alarm where time = ?", new String[]{hour + ":" + minute});
        ContentValues cv = new ContentValues();

        //闹钟已存在，如21:21这种
        if (cursor.moveToNext()){
            database.close();
            Toast.makeText(getContext(),alarmExist,Toast.LENGTH_SHORT).show();
        }
        else {
            boolean timeTooNearFlag = false, timeRepeatFlag = false;
            int tempHour, tempMinute;
            String records;
            String[] strings;
            cursor = database.rawQuery("select * from alarm",null);
            while (cursor.moveToNext()){
                records = cursor.getString(0);
                strings = records.split(":");
                tempHour = Integer.parseInt(strings[0]);
                tempMinute = Integer.parseInt(strings[1]);
                if (hour == tempHour){
                    //闹钟已存在，如07:07,21:07这种
                    if (tempMinute == minute){
                        timeRepeatFlag = true;
                        Toast.makeText(getContext(),alarmExist,Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //闹钟时间太近，如07:07和07:10
                    if (Math.abs(tempMinute - minute) < 5){
                        timeTooNearFlag = true;
                        Toast.makeText(getContext(),alarmTimeTooNear,Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            if (timeRepeatFlag || timeTooNearFlag ){
                database.close();
            }
            else{
                String time, storeData = "";
                boolean hasRepeat = false;
                if (hour < 10) {
                    time = "0" + hour + ":";
                }
                else{
                    time = hour + ":";
                }
                if (minute < 10){
                    time = time + "0" + minute;
                }
                else {
                    time = time + minute;
                }
                cv.put("time",time);
                for (int i = 0; i < 7; i ++){
                    if (repeat[i]){
                        storeData += repeatDay[i];
                        hasRepeat = true;
                    }
                }
                if (hasRepeat){
                    cv.put("repeat",storeData);
                    cv.put("specify","");
                }
                else {
                    cv.put("repeat","单次闹钟");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.MINUTE,minute);
                    calendar.set(Calendar.SECOND,0);
                    if (calendar.getTimeInMillis() <= System.currentTimeMillis()){
                        cv.put("specify",(calendar.getTimeInMillis() + 24*60*60*1000));
                    }
                    else {
                        cv.put("specify",calendar.getTimeInMillis());
                    }
                }
                cv.put("take_effect","1");
                database.insert("alarm",null,cv);
                database.close();
                Map<String,Object> temp = new LinkedHashMap<>();
                temp.put("time",time);
                temp.put("repeat",hasRepeat ? storeData : "单次闹钟");
                temp.put("take_effect", true);
                data.add(temp);
                adapter.notifyDataSetChanged();
                noAlarmNotice.setVisibility(View.GONE);
                Toast.makeText(getContext(),alarmSet,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //called when fragment is created, it initial alarm data and set adapter
    private void initData(){
        String time,repeat;
        boolean take_effect;
        boolean notNull = false;
        File file = new File(getContext().getFilesDir()+"/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = database.rawQuery("select * from alarm", null);

        while (cursor.moveToNext()){
            notNull = true;
            time = cursor.getString(0);
            repeat = cursor.getString(1);
            take_effect = cursor.getInt(3) == 1 ? true : false;
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("time",time);
            temp.put("repeat",repeat);
            temp.put("take_effect", take_effect);
            data.add(temp);
        }
        cursor.close();
        database.close();
        if (notNull) {
            adapter = new SimpleAdapter(getContext(), data, R.layout.alarm_item_layout, new String[]{"time",  "repeat", "take_effect"}, new int[]{R.id.time, R.id.repeat_day, R.id.alarm_control});
            alarmList.setAdapter(adapter);
            noAlarmNotice.setVisibility(View.GONE);
        }
        else {
            adapter = new SimpleAdapter(getContext(), data, R.layout.alarm_item_layout, new String[]{"time",  "repeat", "take_effect"}, new int[]{R.id.time, R.id.repeat_day, R.id.alarm_control});
            alarmList.setAdapter(adapter);
        }
    }

    //called when item of list view and confirm to delete;
    private void deleteAlarmFromDatabase(String time){
        File file = new File(getContext().getFilesDir()+"/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        database.delete("alarm","time=?",new String[]{time});
        database.close();
    }

    //TODO: logic function of switch in item is not implemented yet
}
