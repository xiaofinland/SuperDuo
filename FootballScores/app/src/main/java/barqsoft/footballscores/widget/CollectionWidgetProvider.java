package barqsoft.footballscores.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by xiaoma on 11/03/16.
 */
public class CollectionWidgetProvider extends AppWidgetProvider {
    private static final int PERIOD = 60000;
    public static String MATCHES_WIDGET_UPDATE = "com.barqsoft.footballscores.widget.MATCHES_WIDGET_UPDATE";

    @Override
    public void onUpdate (Context context, AppWidgetManager appWidgetManager, int appWidgetIds[]) {
        for (int appWidgetId  :  appWidgetIds) {

            //Get the layout for the App Widget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);

            // Set up the intent that starts WidgetService, which will provide
            //the views for this collection
            final Intent widgetServiceIntent = new Intent(context, WidgetService.class);
            //Add the app widget Id to the intent extras
            widgetServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            widgetServiceIntent.setData(Uri.parse(widgetServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            //Set up the RemoteViews object to use a RemoteViews adapter,
            //This adapter connects to a RemoteViewsService through the specific intent
            //This is how to populate data.
            views.setRemoteAdapter(R.id.widget_list, widgetServiceIntent);

            //The empty view is displayed when the collection doesn't have any item
            //views.setEmptyView(R.id.widget_list, R.id.widget_no_match);

            // go back to MainActivity
            Intent templateIntent = new Intent(context,MainActivity.class);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            templateIntent.setData(Uri.parse(widgetServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent templatePendingIntent = PendingIntent.getActivity(context,0,templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list, templatePendingIntent);

            views.setOnClickPendingIntent(R.id.widget_header, getLaunchIntent(context));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    private PendingIntent getLaunchIntent(Context context){
        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, 0, launchIntent, 0);
    }

    @Override
    public void onEnabled(Context context){
        super.onEnabled(context);
        Log.d("onEnabled", context.getString(R.string.on_enabled));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), PERIOD, createClockTickIntent(context));
    }

    private PendingIntent createClockTickIntent (Context context){
        Intent intent = new Intent(MATCHES_WIDGET_UPDATE);
        PendingIntent pendingIntentTick = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntentTick;

        }

    @Override
    public void onDisabled (Context context){
        super.onDisabled(context);
        Log.d("onDisabled", context.getString(R.string.on_disabled));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }

    @Override
    public void onReceive (Context context, Intent intent){
        super.onReceive(context,intent);
        if (MATCHES_WIDGET_UPDATE.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int [] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            for (int appWidgetId : appWidgetIds){
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);

                Intent serviceIntent = new Intent(context,WidgetService.class);
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                views.setRemoteAdapter(R.id.widget_list, serviceIntent);
                views.setEmptyView(R.id.widget_list, R.id.widget_no_match);

                appWidgetManager.updateAppWidget(appWidgetId,views);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.widget_list);
            }
        }
    }

}
