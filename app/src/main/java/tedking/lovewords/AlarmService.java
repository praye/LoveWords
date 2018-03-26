package tedking.lovewords;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AlarmService extends Service {
    private AlarmManager manager;
    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long alarmTime = System.currentTimeMillis() + 8 * 24 * 60 * 60 * 1000;
        boolean[] repeatDay = {false, false, false, false, false, false, false};
        String[] stringDay = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        boolean hasAlarm = false;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //过期闹钟列表outDate
        List<String> outDate = new LinkedList<>();
        File file = new File(getFilesDir() + "/databases/data.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        //以下为删除过期闹钟，只有单次闹钟并且take_effect为1的闹钟才会过期
        //闹钟过期有两种情况，第一是闹钟刚响，第二个是关机后闹钟过期
        Cursor cursor = database.rawQuery("select * from alarm where repeat = ? and take_effect = ?", new String[]{"单次闹钟", "1"});
        while (cursor.moveToNext()) {
            if (Long.parseLong(cursor.getString(2)) < System.currentTimeMillis()) {
                outDate.add(cursor.getString(0));
            }
        }
        cursor.close();
        Iterator<String> iterator = outDate.iterator();
        while (iterator.hasNext()) {
            database.delete("alarm", "time = ?", new String[]{iterator.next()});
        }


        //以下为设置闹钟时间
        cursor = database.rawQuery("select * from alarm where take_effect = ?", new String[]{"1"});
        while (cursor.moveToNext()) {
            //闹钟为单次闹钟
            if (!cursor.getString(2).equals("")) {
                long tempTime = Long.parseLong(cursor.getString(2));
                if (alarmTime > tempTime) {
                    alarmTime = tempTime;
                }
            }
            //闹钟为重复闹钟
            else {
                for (int i = 0; i < 7; i++) {
                    repeatDay[i] = false;
                }
                String timeDetail = cursor.getString(0), tempRepeatDay = cursor.getString(1), tempHour, tempMinute;
                String[] timeDetailArray = timeDetail.split(":");
                tempHour = timeDetailArray[0];
                tempMinute = timeDetailArray[1];
                for (int i = 0; i < 7; i++) {
                    if (tempRepeatDay.contains(stringDay[i])) {
                        repeatDay[i] = true;
                    }
                }
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tempHour));
                calendar.set(Calendar.MINUTE, Integer.parseInt(tempMinute));
                calendar.set(Calendar.SECOND, 0);
                //重复闹钟在当天有闹钟
                if (repeatDay[day - 1]) {
                    if (calendar.getTimeInMillis() > System.currentTimeMillis()) { //闹钟时间比系统时间要晚
                        //比alarmTime要早
                        if (calendar.getTimeInMillis() < alarmTime){
                            alarmTime = calendar.getTimeInMillis();
                        }else {
                            //do nothing
                        }
                    } else { //闹钟时间比系统时间要早
                        for (int i = 1; i <= 7; i++) {
                            if (repeatDay[(day - 1 + i) % 7] && (calendar.getTimeInMillis() + 24 * 60 * 60 * 1000 * i < alarmTime)) {
                                alarmTime = calendar.getTimeInMillis() + 24 * 60 * 60 * 1000 * i;
                                break;
                            }
                        }
                    }
                } else { //重复闹钟在当天没有闹钟
                    for (int i = 1; i < 7; i++) {
                        if (repeatDay[(day - 1 + i) % 7] && (calendar.getTimeInMillis() + 24*60*60*1000*i < alarmTime)) {
                            alarmTime = calendar.getTimeInMillis() + 24 * 60 * 60 * 1000 * i;
                            break;
                        }
                    }
                }
            }
            hasAlarm = true;
        }
        if (hasAlarm) {
            Intent intent1 = new Intent(this, AlarmingActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
            manager.setWindow(AlarmManager.RTC_WAKEUP, alarmTime, 1000, pendingIntent);
        }
        database.close();
        return super.onStartCommand(intent, flags, START_STICKY);
    }
    @Override
    public void onDestroy(){
        Intent localIntent = new Intent();
        localIntent.setClass(AlarmService.this,AlarmService.class);
        startService(localIntent);
        super.onDestroy();
    }
}
