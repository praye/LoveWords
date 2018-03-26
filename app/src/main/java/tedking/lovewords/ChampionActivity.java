package tedking.lovewords;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChampionActivity extends Activity {
    private ListView listView;
    private String championOf, className;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter adapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_champion);
        championOf = getIntent().getStringExtra("stage");
        listView = findViewById(R.id.champion_list);
        textView = findViewById(R.id.empty_ranking);
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
                    System.out.println(list.size());
                    if (list.size() == 0){
                        textView.setVisibility(View.VISIBLE);
                    }else {
                        for (int i = 0; i < list.size(); i++){
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("user",list.get(i).get("user"));
                            map.put("score",list.get(i).get("score"));
                            setImageResource(map,i);
                            data.add(map);
                        }
                        adapter = new SimpleAdapter(getApplicationContext(),data,R.layout.layout_champion_item,new String[]{"image","user","score"},new int[]{R.id.ranking,R.id.champion_username,R.id.champion_score});
                        listView.setAdapter(adapter);
                        textView.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(ChampionActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void setImageResource(Map<String, Object> map, int i){
        switch (i){
            case 0:
                map.put("image",R.drawable.rank_gold);
                break;
            case 1:
                map.put("image",R.drawable.rank_silver);
                break;
            case 2:
                map.put("image",R.drawable.rank_bronze);
                break;
            case 3:
                map.put("image",R.drawable.rank_four);
                break;
            case 4:
                map.put("image",R.drawable.rank_five);
                break;
            case 5:
                map.put("image",R.drawable.rank_six);
                break;
            case 6:
                map.put("image",R.drawable.rank_seven);
                break;
            case 7:
                map.put("image",R.drawable.rank_eight);
                break;
            case 8:
                map.put("image",R.drawable.rank_nine);
                break;
            case 9:
                map.put("image",R.drawable.rank_ten);
                break;
        }
    }
}
