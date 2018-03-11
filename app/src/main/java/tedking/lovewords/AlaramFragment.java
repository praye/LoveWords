package tedking.lovewords;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    public Button updateData;
    private Dialog dialog;
    private Button [] days = new Button[7];
    private boolean []repeat = new boolean[]{false,false,false,false,false,false,false};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.alarm_fragment_layout, container,false);
        alarmList = view.findViewById(R.id.alarm_list);
        updateData = view.findViewById(R.id.updateData);
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
                    resetRepeate();
                    dialog.dismiss();
                    break;
                case R.id.confirm:
                    dialog.dismiss();
                    Toast.makeText(getContext(),"Confirm",Toast.LENGTH_LONG).show();
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

    private void buildDialog(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.alarm_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
    private void resetRepeate(){
        for (int i = 0; i < 7; i ++){
            repeat[i] = false;
            days[i].setTextColor(0xFF000000);
        }
    }
}
