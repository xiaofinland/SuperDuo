package barqsoft.footballscores.api;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import barqsoft.footballscores.model.FootballSeason;
import barqsoft.footballscores.model.Season;
import database.DaoHelper;

/**
 * Created by xiaoma on 15/03/16.
 */
public class DatabaseProvider implements Provider, Serializable{

    private Context mContext;
    private OnDataReady mOnDataReady;

    public DatabaseProvider(Context context, OnDataReady onDataReady) {
        mContext = context;
        mOnDataReady = onDataReady;
    }

    @Override
    public void getFixtures() {

        DaoHelper daoHelper = new DaoHelper(mContext);
        FootballSeason soccerSeason = new FootballSeason();
        ArrayList<Season> seasons = daoHelper.getAllSeasons();

        for (Season season : seasons) {
            season.setMatches(daoHelper.getAllMatchForOneSeason(season.getId()));
        }
        soccerSeason.setSeasons(seasons);

        mOnDataReady.onDataReady(soccerSeason);
    }

}
