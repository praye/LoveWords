package tedking.lovewords;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChampionActivity extends Activity {
    private ListView listView;
    private String championOf, className;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_champion);
        championOf = getIntent().getStringExtra("stage");
        listView = findViewById(R.id.champion_list);
        getChampionData(championOf);
    }
    private void getChampionData(String string){
        Calendar calendar = Calendar.getInstance();
        Date startDate;
        if (string.equals("Today")){
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            startDate = calendar.getTime();
            className = "dayScore";
        }else{
            calendar.set(Calendar.DAY_OF_WEEK, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND,0);
            startDate = calendar.getTime();
            className = "weekScore";
        }
        AVQuery<AVObject> query = new AVQuery<>(className);
        query.whereGreaterThanOrEqualTo("createdAt",startDate);
        query.orderByDescending("score");
        query.limit(5);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    if (list == null){
                        Toast.makeText(ChampionActivity.this,"目前未有排行信息",Toast.LENGTH_LONG).show();
                    }else {
                        for (AVObject item : list){
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("user",item.get("user"));
                            map.put("score",item.get("score"));
                            data.add(map);
                        }
                        adapter = new SimpleAdapter(getApplicationContext(),data,R.layout.layout_champion_item,new String[]{"user","score"},new int[]{R.id.champion_username,R.id.champion_score});
                        listView.setAdapter(adapter);
                    }

                }else {
                    Toast.makeText(ChampionActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
