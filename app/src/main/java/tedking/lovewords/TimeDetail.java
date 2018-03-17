package tedking.lovewords;

/**
 * Created by Administrator on 2018/3/17.
 */

public class TimeDetail {
    private String time;
    private String repeatDate;
    private boolean takeEffect;
    public TimeDetail(String time, String repeatDate, boolean take_effect){
        this.time = time;
        this.repeatDate = repeatDate;
        this.takeEffect = take_effect;
    }
    public String getTime(){
        return time;
    }
    public String getRepeatDate(){
        return repeatDate;
    }
    public boolean getTakeEffect(){
        return takeEffect;
    }
    public void setTakeEffect(boolean takeEffect){
        this.takeEffect = takeEffect;
    }
}
