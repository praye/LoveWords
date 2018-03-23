package tedking.lovewords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018/3/23.
 */

public class WordAdapter extends BaseAdapter {
    private List<WordMeaning> list;
    private Context context;

    public WordAdapter(List<WordMeaning> list, Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }
    @Override
    public Object getItem(int position){
        return list == null ? null : list.get(position);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_loadmorelistview_item,null);
            viewHolder.wordItself = convertView.findViewById(R.id.word_item);
            viewHolder.meaning = convertView.findViewById(R.id.meaning_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.wordItself.setText(list.get(i).getWorditself());
        viewHolder.meaning.setText(list.get(i).getMeaning());
        return convertView;
    }

    private class ViewHolder{
        private TextView wordItself;
        private TextView meaning;
    }
}
