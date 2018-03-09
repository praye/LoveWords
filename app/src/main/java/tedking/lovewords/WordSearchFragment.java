package tedking.lovewords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/3/4.
 */

public class WordSearchFragment extends android.support.v4.app.Fragment {
    private Button search;
    private EditText editText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.word_search_layout, container, false);
        search = view.findViewById(R.id.btn_search);
        editText = view.findViewById(R.id.edit_query);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //method to be implemented
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),editText.getText(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
