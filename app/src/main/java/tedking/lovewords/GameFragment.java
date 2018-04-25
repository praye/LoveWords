package tedking.lovewords;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Administrator on 2018/3/28.
 */

public class GameFragment extends android.support.v4.app.Fragment {
    private Button enter, exit, missingLetter;
    private CardView cardView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_alarming,container,false);
        findView(view);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Exercise.class));
                getActivity().finish();
            }
        });
        missingLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),MissingLetterActivity.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    public void findView(View view){
        enter = view.findViewById(R.id.todo);
        exit = view.findViewById(R.id.exit);
        cardView = view.findViewById(R.id.cardView);
        missingLetter = view.findViewById(R.id.missingLetter);
        cardView.setVisibility(View.GONE);
        exit.setVisibility(View.GONE);
    }
}
