package barqsoft.footballscores.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

import barqsoft.footballscores.FixtureView;
import barqsoft.footballscores.model.Season;

/**
 * Created by xiaoma on 15/03/16.
 */
public class FixtureAdapter extends RecyclerView.Adapter<FixtureAdapter.ItemViewHolder> {

    private ArrayList<Season> mSeasons;
    private Activity mActivity;
    private Context mContext;

    public FixtureAdapter(Activity activity, Context context, ArrayList<Season> seasons) {
        mActivity = activity;
        mSeasons = seasons;
        mContext = context;
    }
    // inner class to hold a reference to each card_view of RecyclerView
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public final CardView cardView;
        public FixtureView fixtureView;

        public ItemViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view_fixture);
            fixtureView = (FixtureView) cardView.findViewById(R.id.list_of_matches);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_fixture_view, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final int pos = position;
        final FixtureView fixtureView = holder.fixtureView;
        fixtureView.setSeason(mActivity, mContext, mSeasons.get(position));

        final CardView cardView = holder.cardView;

        //workaround in order to adjust the height of the card taking into account the number of matches
        cardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                cardView.getViewTreeObserver().removeOnPreDrawListener(this);

                // initially changing the height to min height
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();

                int itemHeight = 220;

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

                    itemHeight = 115;

                    if (mSeasons.get(position).getMatches().size() == 1) {
                        itemHeight += 10;
                    }
                }

                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                    itemHeight = 175;

                    if (mSeasons.get(position).getMatches().size() == 1) {
                        itemHeight += 10;
                    }
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
                        && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {

                    itemHeight = 165;
                }

                layoutParams.height = (mSeasons.get(pos).getMatches().size() * itemHeight) + fixtureView.getTitleHeight();
                cardView.setLayoutParams(layoutParams);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSeasons == null ? 0 : mSeasons.size();
    }

    /**
     * Sets seasons
     *
     * @param mSeasons Seasons
     */
    public void setSeasons(ArrayList<Season> mSeasons) {
        this.mSeasons = mSeasons;
        notifyDataSetChanged();
    }
}

