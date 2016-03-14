package barqsoft.footballscores.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import barqsoft.footballscores.model.FootballSeason;
import barqsoft.footballscores.service.SoccerService;

/**
 * Created by xiaoma on 15/03/16.
 */
public class InternetProvider implements Provider{

    private Context mContext;
    private OnDataReady mOnDataReady;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SoccerService.NOTIFICATION_GET_FIXTURES)) {
                FootballSeason soccerSeason = intent.getParcelableExtra(SoccerService.PARAM_FOOTBALL_SEASON);
                mOnDataReady.onDataReady(soccerSeason);
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
            }
        }
    };

    public InternetProvider(Context context, OnDataReady onDataReady) {
        mContext = context;
        mOnDataReady = onDataReady;
    }

    @Override
    public void getFixtures() {

        registerBroadcastReceiver();

        Intent intent = new Intent(mContext, SoccerService.class);
        intent.setAction(SoccerService.ACTION_GET_FIXTURES);
        mContext.startService(intent);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SoccerService.NOTIFICATION_GET_FIXTURES);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, filter);
    }
}
