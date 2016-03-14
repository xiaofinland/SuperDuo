package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import barqsoft.footballscores.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.model.Match;
import barqsoft.footballscores.model.Season;
import barqsoft.footballscores.model.Team;

/**
 * Created by xiaoma on 14/03/16.
 */
public class DaoHelper {
    private static final String TAG = DaoHelper.class.getSimpleName();

    private Context mContext;
    private ScoresDBHelper dbHelper;

    public DaoHelper(Context context) {
        mContext = context;
        dbHelper = new ScoresDBHelper(mContext);
    }

    /**
     * Gets the number of team from the database.
     *
     * @return count.
     */
    public int getTeamCount() {
        String countQuery = "SELECT  * FROM " + DatabaseContract.TeamEntry.TABLE_NAME;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Gets the last id from Team Table.
     *
     * @return lastId.
     */
    public long getLastItemId() {
        long lastId = 0;
        String query = "SELECT ROWID from " + DatabaseContract.TeamEntry.TABLE_NAME + " order by ROWID DESC limit 1";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
            c.close();
        }
        return lastId;
    }

    /**
     * Verifies if a match is already persisted in the database.
     *
     * @param id Match id.
     * @return boolean.
     */
    public boolean checkIfMatchExist(String id) {
        Cursor matchEntry = mContext.getContentResolver().query(
                DatabaseContract.MatchEntry.buildMatchUri(Long.parseLong(id)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (matchEntry.getCount() > 0) {
            return true;
        }

        matchEntry.close();
        return false;
    }

    /**
     * Verifies if a season is already persisted in the database.
     *
     * @param id Season id.
     * @return boolean.
     */
    public boolean checkIfSeasonExist(String id) {
        Cursor seasonEntry = mContext.getContentResolver().query(
                DatabaseContract.SeasonEntry.buildSeasonUri(Long.parseLong(id)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        if (seasonEntry.getCount() > 0) {
            return true;
        }

        seasonEntry.close();
        return false;
    }

    /**
     * Gets matches for today.
     *
     * @return ArrayList<Match>.
     */
    public ArrayList<Match> getMatchesForToday() {

        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.format_yyyy_MM_dd), Locale.ENGLISH);
        String query = "SELECT * from " + DatabaseContract.MatchEntry.TABLE_NAME + " where "
                + DatabaseContract.MatchEntry.DATE + " = '" + format.format(new Date()) + "'";

        return getMatches(query);
    }

    /**
     * Gets one Match.
     *
     * @param id Match id.
     * @return Match.
     */
    public Match getMatch(String id) {

        String query = "SELECT * from " + DatabaseContract.MatchEntry.TABLE_NAME + " where "
                + DatabaseContract.MatchEntry._ID + " = " + id;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToNext()) {
            Match matchFromCursor = getMatchFromCursor(c);
            c.close();
            return matchFromCursor;
        }
        return null;
    }

    /**
     * Gets all seasons.
     *
     * @return seasons.
     */
    public ArrayList<Season> getAllSeasons() {

        String query = "SELECT * from " + DatabaseContract.SeasonEntry.TABLE_NAME;

        ArrayList<Season> seasons = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "Seasons found: " + c.getCount());

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Season season = new Season();
                season.setId(c.getString(c.getColumnIndex(DatabaseContract.SeasonEntry._ID)));
                season.setLeague(c.getString(c.getColumnIndex(DatabaseContract.SeasonEntry.LEAGUE)));
                season.setCaption(c.getString(c.getColumnIndex(DatabaseContract.SeasonEntry.CAPTION)));
                season.setYear(c.getString(c.getColumnIndex(DatabaseContract.SeasonEntry.YEAR)));
                seasons.add(season);
                c.moveToNext();
            }
            c.close();
        }

        return seasons;
    }

    /**
     * Gets all matches for one season.
     *
     * @param seasonId Season id.
     * @return ArrayList<Match>.
     */
    public ArrayList<Match> getAllMatchForOneSeason(String seasonId) {

        String query = "SELECT * from " + DatabaseContract.MatchEntry.TABLE_NAME + " where "
                + DatabaseContract.MatchEntry.SEASON_ID + " = " + seasonId;
        return getMatches(query);
    }

    public ArrayList<Match> getAllMatch() {

        String query = "SELECT * from " + DatabaseContract.MatchEntry.TABLE_NAME;
        return getMatches(query);
    }

    /**
     * Execute a query which could retrieve more than one Match.
     *
     * @param query Query to be executed.
     * @return matches.
     */
    private ArrayList<Match> getMatches(String query) {

        ArrayList<Match> matches = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.getCount() > 0) {

            Log.d(TAG, "found: " + c.getCount());

            c.moveToFirst();
            while (!c.isAfterLast()) {
                matches.add(getMatchFromCursor(c));
                c.moveToNext();
            }
            c.close();
        }

        for (Match match : matches) {
            match.setHomeTeam(getTeam(match.getHomeTeamId()));
            match.setAwayTeam(getTeam(match.getAwayTeamId()));
        }

        return matches;
    }

    /**
     * Gets a match form Cursor.
     *
     * @param c Cursor.
     * @return match.
     */
    public Match getMatchFromCursor(Cursor c) {

        Match match = new Match();

        if (c != null) {
            match.setMatchId(String.valueOf(c.getInt(c.getColumnIndex(DatabaseContract.MatchEntry._ID))));
            match.setSeasonId(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.SEASON_ID)));
            match.setMatchDay(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.MATCH_DAY)));
            match.setStatus(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.STATUS)));
            match.setDate(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.DATE)));
            match.setTime(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.TIME)));
            match.setHomeGoals(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.HOME_GOALS)));
            match.setAwayGoals(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.AWAY_GOALS)));
            match.setHomeTeamId(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.HOME_TEAM_ID)));
            match.setAwayTeamId(c.getString(c.getColumnIndex(DatabaseContract.MatchEntry.AWAY_TEAM_ID)));
        }
        return match;
    }

    public Team getTeam(String id) {

        Team team = new Team();

        String query = "SELECT * from " + DatabaseContract.TeamEntry.TABLE_NAME + " where "
                + DatabaseContract.TeamEntry._ID + " = " + id;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            team.setId(c.getInt(c.getColumnIndex(DatabaseContract.TeamEntry._ID)));
            team.setName(c.getString(c.getColumnIndex(DatabaseContract.TeamEntry.NAME)));
            team.setShortName(c.getString(c.getColumnIndex(DatabaseContract.TeamEntry.SHORT_NAME)));
            team.setThumbnail(c.getString(c.getColumnIndex(DatabaseContract.TeamEntry.THUMBNAIL)));
            c.close();
        } else {
            Log.d(TAG, "Team with: " + id + " was not found.");
        }
        return team;
    }
}
