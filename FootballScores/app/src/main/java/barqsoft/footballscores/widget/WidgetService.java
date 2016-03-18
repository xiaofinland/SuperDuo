package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.Match;
import database.DaoHelper;

/**
 * Created by xiaoma on 10/03/16.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory (Intent intent) {

        return new WidgetViewsFactory(getApplicationContext(), intent);

    }
}

class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG= WidgetViewsFactory.class.getSimpleName();

    private final Context mContext;
    private final int mAppWidgetId;
    private ArrayList<Match> mMatches;

    public WidgetViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mMatches = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        //Get matches for today
        DaoHelper mDaoHelper = new DaoHelper(mContext);
        mMatches = mDaoHelper.getMatchesForToday();
    }

    @Override
    public void onDataSetChanged() {
        //Get matches for today
        DaoHelper mDaoHelper = new DaoHelper(mContext);
        mMatches = mDaoHelper.getMatchesForToday();
    }

    @Override
    public void onDestroy() {
        mMatches.clear();
    }

    @Override
    public int getCount() {
        return mMatches == null ? 0 : mMatches.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Match match = mMatches.get(position);

        RemoteViews mView;
        Log.i(LOG_TAG, "match status: " + match.getStatus());

        if (!match.getStatus().equals(mContext.getString(R.string.timed))) {
            mView = new RemoteViews(mContext.getPackageName(), R.layout.match_view_finished);
        } else {
            mView = new RemoteViews(mContext.getPackageName(), R.layout.match_view_timed);
        }

        //sets home team data
        String shortName = match.getHomeTeam().getShortName();
        String longName = match.getHomeTeam().getName();

        if (shortName != null && !shortName.equals("")) {
            mView.setTextViewText(R.id.home_team_name, shortName);
        } else {
            mView.setTextViewText(R.id.home_team_name, longName);
        }

        try {
            Bitmap b = Picasso.with(mContext)
                    .load(match.getHomeTeam().getThumbnail())
                    .error(R.drawable.ic_no_flag)
                    .placeholder(R.drawable.ic_no_flag)
                    .get();

            mView.setImageViewBitmap(R.id.home_team_flag, b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = match.getHomeGoals() + " - " + match.getAwayGoals();
        if (!result.equals("null - null")) {
            mView.setTextViewText(R.id.match_result, result);
        }

        mView.setTextViewText(R.id.match_time, match.getTime());

        //sets away team data
        shortName = match.getAwayTeam().getShortName();
        longName = match.getAwayTeam().getName();

        if (shortName != null && !shortName.equals("")) {
            mView.setTextViewText(R.id.away_team_name, shortName);
        } else {
            mView.setTextViewText(R.id.away_team_name, longName);
        }

        try {
            Bitmap b = Picasso.with(mContext)
                    .load(match.getAwayTeam().getThumbnail())
                    .error(R.drawable.ic_no_flag)
                    .placeholder(R.drawable.ic_no_flag)
                    .get();

            mView.setImageViewBitmap(R.id.away_team_flag, b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mView;
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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}