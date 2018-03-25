package tedking.lovewords;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/25.
 */

public class StaticMethod {
    public static int getDayOfYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_YEAR);
    }
    public static int getWeekOfYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
