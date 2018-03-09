package tedking.lovewords;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/5.
 */

public class AlaramFragment extends Fragment {
    private ListView alarmList;
    private SimpleAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.alarm_fragment_layout, container,false);
        alarmList = view.findViewById(R.id.alarm_list);
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String,Object> temp = new LinkedHashMap<>();
        temp.put("time","8:30");
        temp.put("repeate_day","工作日");
        data.add(temp);
        Map<String,Object> temp1 = new LinkedHashMap<>();
        temp1.put("time","8:30");
        temp1.put("repeate_day","工作日");
        data.add(temp1);
        adapter = new SimpleAdapter(getContext(),data,R.layout.alarm_item_layout, new String[]{"time","repeate_day"}, new int[]{R.id.time, R.id.repeat_day});
        alarmList.setAdapter(adapter);
        return view;
    }
}
