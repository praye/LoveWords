package tedking.lovewords;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;


/**
 * Created by Administrator on 2018/3/23.
 */

public class LoadmoreListView extends ListView implements AbsListView.OnScrollListener{
    private View footer;
    private boolean isLoading;
    private int lastVisibleItem, totalItemCount;
    IloadListener iLoadListener;

    public LoadmoreListView(Context context) {
        super(context);
        initView(context);
    }
    public LoadmoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public LoadmoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    public void initView(Context context){
        footer = LayoutInflater.from(context).inflate(R.layout.layout_footer,null);
        footer.findViewById(R.id.loading_layout).setVisibility(GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (totalItemCount == lastVisibleItem && i == SCROLL_STATE_IDLE){
            if (!isLoading){
                isLoading = true;
                footer.findViewById(R.id.loading_layout).setVisibility(VISIBLE);
                iLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        lastVisibleItem = i + i1;
        totalItemCount = i2;
    }

    public void setInterface(IloadListener iLoadListener){
        this.iLoadListener = iLoadListener;
    }

    public interface IloadListener{
        void onLoad();
    }
    public void onLoadComplete(){
        isLoading = false;
        footer.findViewById(R.id.loading_layout).setVisibility(GONE);
    }
}
