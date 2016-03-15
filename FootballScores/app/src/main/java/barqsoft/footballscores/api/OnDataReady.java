package barqsoft.footballscores.api;

import barqsoft.footballscores.model.FootballSeason;

/**
 * Created by xiaoma on 15/03/16.
 */
public interface OnDataReady {

    /**
     * When data is ready, the listener will implement this interface to process the data.
     * @param footballSeason
     */
    void onDataReady(FootballSeason footballSeason);
}
