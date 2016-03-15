package barqsoft.footballscores.api;

import java.util.ArrayList;

import barqsoft.footballscores.model.Season;

/**
 * Created by xiaoma on 15/03/16.
 */
public interface OnDataLoad {
    /**
     * The data was successfully retrieved.
     * @param mSeasons
     */
    void onDataLoadedSuccess(ArrayList<Season> mSeasons);

    /**
     * The data could not be retrieved.
     */
    void onDataLoadedFail();

    /**
     * Indicates the state of the data process search.
     * @param load
     */
    void onDataLoading(boolean load);
}
