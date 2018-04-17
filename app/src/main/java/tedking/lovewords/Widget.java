package tedking.lovewords;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import java.io.File;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {
    private static String UPDATE_CONDUCTION = "REFRESH WORD IN WIDGET";
    private SharedPreferences preferences;

    /*static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        System.out.println("updateAppWidget");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        String [] result = setTextOperation(context);
        views.setTextViewText(R.id.appwidget_text, result[0]);
        views.setTextViewText(R.id.meaning_widget,result[1]);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        System.out.println("onUpdate");
        Intent intent = new Intent();
        intent.setAction(UPDATE_CONDUCTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
        remoteViews.setOnClickPendingIntent(R.id.newone,pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds,remoteViews);
        context.sendBroadcast(intent);
    }

    @Override
    public void onEnabled(Context context) {
        System.out.println("onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        System.out.println("onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context,Intent intent){
        System.out.println("onReceive " + intent.getAction());
        super.onReceive(context,intent);
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
        String action = intent.getAction();
        if (action.equals(UPDATE_CONDUCTION)){
            String[] strings = setTextOperation(context);
            views.setTextViewText(R.id.appwidget_text,strings[0]);
            views.setTextViewText(R.id.meaning_widget,strings[1]);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context,Widget.class);
            appWidgetManager.updateAppWidget(componentName,views);
        }
    }

    public String [] setTextOperation(Context context){
        preferences = context.getSharedPreferences(StringConstant.SHAREDPREFERENCENAME,Context.MODE_PRIVATE);
        String tableName = "words", emptyCode = "You have mastered all words in database";
        String [] result = new String[2];
        if (preferences.getBoolean(StringConstant.FIRSTOPENAPP,true)){
            result[0] = "You need to login.";
            result[1] = "Click refresh button after login";
            return result;
        } else {
            File file = new File(context.getFilesDir() + "/databases/data.db");
            SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = database.rawQuery("select * from " + tableName + " where status = 0", null);
            if (cursor.getCount() == 0) {
                result[0] = emptyCode;
                database.close();
                return result;
            } else {
                cursor = database.rawQuery("select * from " + tableName + " where status = 0 order by RANDOM() limit 1", null);
                while (cursor.moveToNext()) {
                    result[0] = cursor.getString(0);
                    result[1] = cursor.getString(2);
                }
                database.close();
                return result;
            }
        }
    }
}

