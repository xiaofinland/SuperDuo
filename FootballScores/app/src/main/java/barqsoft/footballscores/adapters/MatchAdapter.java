package barqsoft.footballscores.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.Match;

/**
 * Created by xiaoma on 15/03/16.
 */
public class MatchAdapter extends BaseAdapter {

    private static final String TAG = MatchAdapter.class.getSimpleName();

    ArrayList<Match> mMatches = new ArrayList<>();

    private Activity mActivity;
    private Context mContext;

    // inner class to hold a reference to each match_view of ListView
    public class ViewHolder {

        public ImageView homeTeamFlag;
        public TextView homeTeamName;
        public TextView matchResult;
        public TextView matchTime;
        public ImageView awayTeamFlag;
        public TextView awayTeamName;

        public ViewHolder(View v) {
            homeTeamName = (TextView) v.findViewById(R.id.home_team_name);
            homeTeamFlag = (ImageView) v.findViewById(R.id.home_team_flag);
            matchResult = (TextView) v.findViewById(R.id.match_result);
            matchTime = (TextView) v.findViewById(R.id.match_time);
            awayTeamFlag = (ImageView) v.findViewById(R.id.away_team_flag);
            awayTeamName = (TextView) v.findViewById(R.id.away_team_name);
        }
    }

    public MatchAdapter(Activity activity, Context context, ArrayList<Match> matches) {
        mActivity = activity;
        mContext = context;
        mMatches = matches;
    }

    @Override
    public int getCount() {
        return mMatches == null ? 0 : mMatches.size();
    }

    @Override
    public Object getItem(int position) {
        return mMatches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.match_view, null);

            holder = new ViewHolder(rowView);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        Match match = mMatches.get(position);

        //programming defensive because of the bad api's information
        if (match.getHomeTeam() != null && match.getAwayTeam() != null) {
            // fill data
            String shortName = match.getHomeTeam().getShortName();
            String longName = match.getHomeTeam().getName();

            if (shortName != null && !shortName.equals("")) {
                holder.homeTeamName.setText(shortName);
            } else {
                holder.homeTeamName.setText(longName);
            }

            String thumbnailHomeTeam = match.getHomeTeam().getThumbnail();

            if (thumbnailHomeTeam != null && !thumbnailHomeTeam.isEmpty()) {

                Uri uri = Uri.parse(thumbnailHomeTeam);

                Glide.with(mActivity)
                        .load(uri)
                        .error(R.drawable.ic_warning)
                        .placeholder(R.drawable.ic_no_flag)
                        .into(holder.homeTeamFlag);
            }

            if (!match.getStatus().equals(mContext.getString(R.string.timed))) {
                String result = match.getHomeGoals() + " - "
                        + match.getAwayGoals();

                holder.matchResult.setText(result);
                holder.matchTime.setText(match.getTime());
            } else {
                holder.matchTime.setText(match.getTime());
                holder.matchTime.setVisibility(View.VISIBLE);
                holder.matchResult.setVisibility(View.GONE);
            }

            shortName = match.getAwayTeam().getShortName();
            longName = match.getAwayTeam().getName();

            if (shortName != null && !shortName.isEmpty()) {
                holder.awayTeamName.setText(shortName);
            } else {
                holder.awayTeamName.setText(longName);
            }

            String thumbnailAwayTeam = match.getAwayTeam().getThumbnail();

            if (thumbnailAwayTeam != null && !thumbnailAwayTeam.isEmpty()) {

                Uri uri = Uri.parse(thumbnailAwayTeam);

                Glide.with(mActivity)
                        .load(uri)
                        .error(R.drawable.ic_warning)
                        .placeholder(R.drawable.ic_no_flag)
                        .into(holder.awayTeamFlag);
            }
        } else {
            Log.d(TAG, "Match status:" + match.getStatus());
        }

        return rowView;
    }

}
