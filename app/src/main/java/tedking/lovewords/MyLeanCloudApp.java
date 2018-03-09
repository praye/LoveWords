package tedking.lovewords;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;

/**
 * Created by bing on 02/03/2018.
 */

public class MyLeanCloudApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"ul4nRqOpTgSPD62PWmkASufM-gzGzoHsz","VAFz9Gf28WwH83kzJMVB0ueV");
        //开启调试日志
        AVOSCloud.setDebugLogEnabled(true);
        AVAnalytics.enableCrashReport(this, true);
    }
}

