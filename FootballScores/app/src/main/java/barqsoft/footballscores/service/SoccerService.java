package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import barqsoft.footballscores.model.FootballSeason;
import barqsoft.footballscores.model.Match;
import barqsoft.footballscores.model.Season;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.utils.Parser;
import barqsoft.footballscores.utils.RestClient;
import database.DaoHelper;
import database.DatabaseContract;
import database.ScoresDBHelper;

/**
 * Created by xiaoma on 14/03/16.
 */
public class SoccerService extends IntentService {

    private final String LOG_TAG = SoccerService.class.getSimpleName();

    //PARAM_SOCCER_SEASON
    public static final String PARAM_FOOTBALL_SEASON = "PARAM_FOOTBALL_SEASON";
    public static final String PARAM_CLEAN_DATABASE = "PARAM_CLEAN_DATABASE";

    //Actions
    public static final String ACTION_GET_FIXTURES = "ACTION_GET_FIXTURES";
    public static final String ACTION_CLEAN_DATABASE = "ACTION_CLEAN_DATABASE";

    //Notifications
    public static final String NOTIFICATION_GET_FIXTURES = "NOTIFICATION_GET_FIXTURES";
    public static final String NOTIFICATION_CLEAN_DATABASE = "NOTIFICATION_CLEAN_DATABASE";

    private DaoHelper daoHelper;

    public SoccerService() {
        super("SoccerService");
        daoHelper = new DaoHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_FIXTURES.equals(action)) {
                publishResults(getSoccerSeason(""));
            }
            if (ACTION_CLEAN_DATABASE.equals(action)) {
                cleanDataBase();
            }
        }
    }

    private void publishResults(FootballSeason footballSeason) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_FOOTBALL_SEASON, footballSeason);
        intent.setAction(NOTIFICATION_GET_FIXTURES);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void cleanDataBase() {
        ScoresDBHelper dbHelper = new ScoresDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, ScoresDBHelper.DATABASE_VERSION, ScoresDBHelper.DATABASE_VERSION + 1);

        Intent intent = new Intent();
        intent.putExtra(PARAM_CLEAN_DATABASE, getString(R.string.clean_database));
        intent.setAction(NOTIFICATION_CLEAN_DATABASE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Obtains one FootballSeason for a time frame.
     *
     * @param timeFrame The period of time.
     * @return soccerSeason.
     */
    private FootballSeason getSoccerSeason(String timeFrame) {

        FootballSeason footballSeason = new FootballSeason();

        try {
            URL seasonsUrl = createSeasonsUrl();

            Log.d("URL ", seasonsUrl.toString());

            String result = RestClient.getData(seasonsUrl.toString(), getString(R.string.api_key));

            ArrayList<Season> seasons = Parser.parseSeasonsJSON(result);

            for (Season season : seasons) {

                //if season is not still in database, let's persist it.
                if (!daoHelper.checkIfSeasonExist(String.valueOf(season.getId()))) {
                    ContentValues seasonValues = new ContentValues();
                    seasonValues.put(DatabaseContract.SeasonEntry._ID, season.getId());
                    seasonValues.put(DatabaseContract.SeasonEntry.LEAGUE, season.getLeague());
                    seasonValues.put(DatabaseContract.SeasonEntry.CAPTION, season.getCaption());
                    seasonValues.put(DatabaseContract.SeasonEntry.YEAR, season.getYear());

                    Uri url = DatabaseContract.SeasonEntry.buildSeasonUri(Long.parseLong(season.getId()));
                    getContentResolver().insert(url, seasonValues);
                }

                if (timeFrame == null || timeFrame.equals("")) {
                    addMatches(season, "p10");
                    addMatches(season, "n4");
                } else {
                    addMatches(season, timeFrame);
                }
            }

            footballSeason.setSeasons(seasons);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        return footballSeason;
    }

    /**
     * Adds matches for one season.
     *
     * @param season    Season Object.
     * @param timeFrame the period of time.
     */
    private void addMatches(Season season, String timeFrame) {

        try {
            URL fixturesUrl = createFixtureUrl(String.valueOf(season.getId()), timeFrame);
            String result = RestClient.getData(fixturesUrl.toString(), getString(R.string.api_key));

            ArrayList<Match> matches = Parser.parseMatchesJSON(result);

            if (season.getMatches() != null && !season.getMatches().isEmpty()) {
                season.getMatches().addAll(matches);
            } else {
                season.setMatches(matches);
            }

            //Add Teams' objects to the matches
            addTeamsToMatches(matches);

        } catch (MalformedURLException ex) {
            Log.e(LOG_TAG, "Error ", ex);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    /**
     * Adds the teams in each of the Matches.
     *
     * @param matches List of Match Objects.
     */
    private void addTeamsToMatches(ArrayList<Match> matches) {

        if (matches != null && !matches.isEmpty()) {

            for (int i = 0; i < matches.size(); i++) {
                Match match = matches.get(i);

                Team homeTeam = daoHelper.getTeam(match.getHomeTeamId());
                Team awayTeam = daoHelper.getTeam(match.getAwayTeamId());

                if (homeTeam == null || awayTeam == null) {
                    Log.d("data", "home team: " + match.getHomeTeamId() + " " + " away team: " + match.getAwayTeamId());
                }
                match.setHomeTeam(homeTeam);
                match.setAwayTeam(awayTeam);
            }
        }

        //persists matches into database
        if (matches != null && !matches.isEmpty()) {
            for (Match match : matches) {
                if (!daoHelper.checkIfMatchExist(match.getMatchId())) {
                    ContentValues matchValues = new ContentValues();
                    matchValues.put(DatabaseContract.MatchEntry._ID, match.getMatchId());
                    matchValues.put(DatabaseContract.MatchEntry.SEASON_ID, match.getSeasonId());
                    matchValues.put(DatabaseContract.MatchEntry.MATCH_DAY, match.getMatchDay());
                    matchValues.put(DatabaseContract.MatchEntry.STATUS, match.getStatus());
                    matchValues.put(DatabaseContract.MatchEntry.DATE, match.getDate());
                    matchValues.put(DatabaseContract.MatchEntry.TIME, match.getTime());
                    matchValues.put(DatabaseContract.MatchEntry.HOME_GOALS, match.getHomeGoals());
                    matchValues.put(DatabaseContract.MatchEntry.AWAY_GOALS, match.getAwayGoals());
                    matchValues.put(DatabaseContract.MatchEntry.HOME_TEAM_ID, match.getHomeTeam().getId());
                    matchValues.put(DatabaseContract.MatchEntry.AWAY_TEAM_ID, match.getAwayTeam().getId());

                    Uri url = DatabaseContract.MatchEntry.buildMatchUri(Long.parseLong(match.getMatchId()));
                    getContentResolver().insert(url, matchValues);
                }
            }
        }
    }
    @NonNull
    private URL createSeasonsUrl() throws MalformedURLException {
        final String BASE_URL = "http://api.football-data.org/v1/soccerseasons/";
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .build();

        return new URL(builtUri.toString());
    }
    @NonNull
    private URL createFixtureUrl(String soccerSeason, String timeFrame) throws MalformedURLException {
        final String BASE_URL = "http://api.football-data.org/v1/soccerseasons";

        final String FIXTURES = "fixtures";
        final String QUERY_TIME_FRAME = "timeFrame";
        final String QUERY_TIME_VALUE = "n7"; // one week next

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(soccerSeason)
                .appendPath(FIXTURES)
                .appendQueryParameter(QUERY_TIME_FRAME, timeFrame)
                .build();

        return new URL(builtUri.toString());
    }

}
