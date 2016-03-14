package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    public static final int DATABASE_VERSION = 5;
    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_SEASON_TABLE = "CREATE TABLE " + DatabaseContract.SeasonEntry.TABLE_NAME + " (" +
                DatabaseContract.SeasonEntry._ID + " INTEGER PRIMARY KEY," +
                DatabaseContract.SeasonEntry.CAPTION + " TEXT NOT NULL," +
                DatabaseContract.SeasonEntry.LEAGUE + " TEXT ," +
                DatabaseContract.SeasonEntry.YEAR + " INTEGER ," +
                "UNIQUE (" + DatabaseContract.SeasonEntry._ID + ") ON CONFLICT IGNORE)";

        final String SQL_CREATE_TEAM_TABLE = "CREATE TABLE " + DatabaseContract.TeamEntry.TABLE_NAME + " (" +
                DatabaseContract.TeamEntry._ID + " INTEGER PRIMARY KEY," +
                DatabaseContract.TeamEntry.NAME + " TEXT NOT NULL," +
                DatabaseContract.TeamEntry.SHORT_NAME + " TEXT NOT NULL," +
                DatabaseContract.TeamEntry.THUMBNAIL + " INTEGER ," +
                "UNIQUE (" + DatabaseContract.TeamEntry._ID + ") ON CONFLICT IGNORE)";

        final String SQL_CREATE_MATCH_TABLE = "CREATE TABLE " + DatabaseContract.MatchEntry.TABLE_NAME + " (" +
                DatabaseContract.MatchEntry._ID + " INTEGER," +
                DatabaseContract.MatchEntry.SEASON_ID + " INTEGER," +
                DatabaseContract.MatchEntry.HOME_TEAM_ID + " INTEGER," +
                DatabaseContract.MatchEntry.AWAY_TEAM_ID + " INTEGER," +
                DatabaseContract.MatchEntry.DATE + " TEXT," +
                DatabaseContract.MatchEntry.TIME + " TEXT," +
                DatabaseContract.MatchEntry.MATCH_DAY + " TEXT," +
                DatabaseContract.MatchEntry.HOME_GOALS + " TEXT," +
                DatabaseContract.MatchEntry.AWAY_GOALS + " TEXT," +
                DatabaseContract.MatchEntry.STATUS + " TEXT," +
                " FOREIGN KEY (" + DatabaseContract.MatchEntry.SEASON_ID + ") REFERENCES " +
                DatabaseContract.SeasonEntry.TABLE_NAME + " (" + DatabaseContract.SeasonEntry._ID + ")," +
                " FOREIGN KEY (" + DatabaseContract.MatchEntry.HOME_TEAM_ID + ") REFERENCES " +
                DatabaseContract.TeamEntry.TABLE_NAME + " (" + DatabaseContract.TeamEntry._ID + ")," +
                " FOREIGN KEY (" + DatabaseContract.MatchEntry.AWAY_TEAM_ID + ") REFERENCES " +
                DatabaseContract.TeamEntry.TABLE_NAME + " (" + DatabaseContract.TeamEntry._ID + "))";

        Log.d("sql-statments", SQL_CREATE_SEASON_TABLE);
        Log.d("sql-statments", SQL_CREATE_TEAM_TABLE);
        Log.d("sql-statments", SQL_CREATE_MATCH_TABLE);

        db.execSQL(SQL_CREATE_SEASON_TABLE);
        db.execSQL(SQL_CREATE_TEAM_TABLE);
        db.execSQL(SQL_CREATE_MATCH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MatchEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SeasonEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TeamEntry.TABLE_NAME);
    }
}
