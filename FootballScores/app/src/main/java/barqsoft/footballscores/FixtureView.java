package barqsoft.footballscores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.adapters.MatchAdapter;
import barqsoft.footballscores.model.Match;
import barqsoft.footballscores.model.Season;
import barqsoft.footballscores.utils.IconsMap;
import barqsoft.footballscores.utils.Utilities;

/**
 * Created by xiaoma on 14/03/16.
 */
public class FixtureView extends LinearLayout {
    private Season mSeason;
    private ImageView mLogo;
    private TextView mTitle;
    private ListView mMatches;
    private MatchAdapter mAdapter;
    private Button mShareButton;
    private Activity mActivity;
    private Context mContext;

    private IconsMap iconsMap;

    public FixtureView(Context context) {
        super(context);
        mContext = context;
    }

    public FixtureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixtureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        inflate(getContext(), R.layout.card_view, this);
        mTitle = (TextView) findViewById(R.id.league);
        mLogo = (ImageView) findViewById(R.id.country_flag);
        mMatches = (ListView) findViewById(R.id.match_list);

        if (Utilities.getSmallWithDisplay(mActivity) > 550) {
            mTitle.setTextSize(22);
        }
        mTitle.setText(mSeason.getCaption());

        iconsMap = new IconsMap(mContext);
        iconsMap.initIconsMap();

        mLogo.setImageDrawable(iconsMap.getIcon(mSeason.getId()));

        //setup list view
        mAdapter = new MatchAdapter(mActivity, mContext, mSeason.getMatches());
        mMatches.setAdapter(mAdapter);

        mShareButton = (Button) findViewById(R.id.button_share_league);
        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAnswer(createAnswer());
            }
        });
    }

    public void setSeason(Activity activity, Context context, Season season) {
        this.mSeason = season;
        mActivity = activity;
        mContext = context;
        init();
    }

    public int getTitleHeight() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_title);
        return layout.getHeight();
    }

    /**
     * Share a list of matches through an intent.
     *
     * @param answer Data to be send.
     */
    private void shareAnswer(String answer) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        Resources res = getResources();
        String subject = String.format(res.getString(R.string.subject_mail), mSeason.getCaption(),
                mSeason.getMatches().get(0).getDate());

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, answer);
        mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.share_league)));
    }

    /**
     * Creates a list of matches which will be shared by e-mail or other media.
     *
     * @return String - Body of E-mail.
     */
    private String createAnswer() {

        StringBuilder builder = new StringBuilder();

        for (Match match : mSeason.getMatches()) {

            builder.append(match.getHomeTeam().getName());

            builder.append("    ");

            if (!match.getStatus().equals(mContext.getString(R.string.timed))) {
                builder.append(match.getHomeGoals());
                builder.append("  -  ");
                builder.append(match.getAwayGoals());
            } else {
                builder.append(match.getTime());
            }

            builder.append("     ");
            builder.append(match.getAwayTeam().getName());

            builder.append("\n");
        }

        return builder.toString();
    }
}
