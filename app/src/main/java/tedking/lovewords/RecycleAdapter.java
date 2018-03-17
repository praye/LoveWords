package tedking.lovewords;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018/3/17.
 */

public class RecycleAdapter extends BaseAdapter {
    private List<TimeDetail> timeDetails;
    private Context context;
    public RecycleAdapter(Context context, List<TimeDetail> timeDetails){
        this.context = context;
        this.timeDetails = timeDetails;
    }
    @Override
    public int getCount(){
        return timeDetails == null ? 0 : timeDetails.size();
    }

    @Override
    public Object getItem(int position){
        return timeDetails == null ? null : timeDetails.get(position);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup){
        final int position = i;
        TimeDetail temp = (TimeDetail) getItem(i);
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.alarm_item_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.time = convertView.findViewById(R.id.time);
            viewHolder.repeatDate = convertView.findViewById(R.id.repeat_day);
            viewHolder.take_effect = convertView.findViewById(R.id.alarm_control);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.time.setText(timeDetails.get(i).getTime());
        viewHolder.repeatDate.setText(timeDetails.get(i).getRepeatDate());
        viewHolder.take_effect.setOnCheckedChangeListener(null);
        viewHolder.take_effect.setChecked(temp.getTakeEffect());

        viewHolder.take_effect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                timeDetails.get(position).setTakeEffect(b);
                File file = new File(context.getFilesDir()+"/databases/data.db");
                SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
                ContentValues contentValues = new ContentValues();
                contentValues.put("take_effect",b ? "1" : "0");
                if(b && timeDetails.get(position).getRepeatDate().equals("单次闹钟")){
                    //TODO it need to update specify data
                    String [] times = timeDetails.get(position).getTime().split(":");
                    int hour = Integer.parseInt(times[0]);
                    int minute = Integer.parseInt(times[1]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    if (calendar.getTimeInMillis() > System.currentTimeMillis()){
                        contentValues.put("specify",calendar.getTimeInMillis());
                    }else {
                        contentValues.put("specify",(calendar.getTimeInMillis() + 24*60*60*1000));
                    }
                }
                database.update("alarm",contentValues,"time = ?", new String[]{timeDetails.get(position).getTime()});
                database.close();
                Intent intent = new Intent();
                intent.setClass(context,AlarmService.class);
                context.startService(intent);
            }
        });
        return convertView;
    }

    private class  ViewHolder{
        private TextView time;
        private TextView repeatDate;
        private Switch take_effect;
    }
}
