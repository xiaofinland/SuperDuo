package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.R;

/**
 * Created by xiaoma on 10/03/16.
 */
public class WidgetDataProvider extends RemoteViewsService.RemoteViewsFactory{

    private Context mContext;
    private int mAppWidgetId;


    public WidgetDataProvider (Context context, Intent intent){
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate(){
        // get matches for today
    }

    @Override
    public RemoteViews getViewAt(int position){
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.appwidget_layout);
        return rv;
    }
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
