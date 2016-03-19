package barqsoft.footballscores;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import barqsoft.footballscores.adapters.FixtureAdapter;
import barqsoft.footballscores.api.FixtureManager;
import barqsoft.footballscores.api.OnDataLoad;
import barqsoft.footballscores.model.Season;

/**
 * Created by xiaoma on 14/03/16.
 */
public class FixtureFragment extends Fragment implements OnDataLoad {
    private final String SEASONS = "SEASONS";
    private static final String LOG_TAG = FixtureFragment.class.getSimpleName();

    private ArrayList<Season> mSeasons = new ArrayList<>();
    private FixtureAdapter mFixtureAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerViewFixtures;
    private LinearLayoutManager mLayoutManager;
    private FixtureManager mFixtureManager;
    private String date;
    private ImageView mNoMatchImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View containerView = inflater.inflate(R.layout.fragment_fixtures, container, false);

        mNoMatchImage = (ImageView) containerView.findViewById(R.id.no_match_for_today);

        mProgressBar = (ProgressBar) containerView.findViewById(R.id.progress_bar_fixtures);

        if (savedInstanceState != null) {
            mSeasons = savedInstanceState.getParcelableArrayList(SEASONS);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        mRecyclerViewFixtures = (RecyclerView) containerView.findViewById(R.id.recycler_view_fixtures);
        mRecyclerViewFixtures.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewFixtures.setLayoutManager(mLayoutManager);

        mFixtureAdapter = new FixtureAdapter(getActivity(), getContext(), mSeasons);
        mRecyclerViewFixtures.setAdapter(mFixtureAdapter);

        return containerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SEASONS, mSeasons);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFixtureManager != null) {
            mFixtureManager.registerListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFixtureManager != null) {
            mFixtureManager.unregisterListener(this);
        }
    }

    @Override
    public void onDataLoadedSuccess(ArrayList<Season> seasons) {
        this.mSeasons = seasons;
        filterSeasons(mSeasons);
        Log.i(LOG_TAG, "reached onDataLoadedSuccess");
        Log.i(LOG_TAG, "season page empty? " + mSeasons.isEmpty());
        Log.i(LOG_TAG, "no match textView visibility before  is: " + mNoMatchImage.isShown());

        if(mSeasons.isEmpty()) {
            mNoMatchImage.setVisibility(View.VISIBLE);

            Log.i(LOG_TAG, "no match textView visibility after is: " + mNoMatchImage.isShown());
        }
        mFixtureAdapter.setSeasons(mSeasons);
    }

    /**
     * Filters the seasons which do not have any match.
     *
     * @param mSeasons
     */
    private void filterSeasons(ArrayList<Season> mSeasons) {
        Iterator<Season> it = mSeasons.iterator();
        while (it.hasNext()) {
            if (it.next().getMatches().size() == 0) {
                it.remove();
            }
        }
    }

    @Override
    public void onDataLoadedFail() {

    }

    @Override
    public void onDataLoading(boolean load) {
        if (mProgressBar != null) {
            if (load) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setFixtureFragment(FixtureManager mFixtureFragment) {
        this.mFixtureManager = mFixtureFragment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
