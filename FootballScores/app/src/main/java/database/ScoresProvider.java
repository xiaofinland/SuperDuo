package database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {

    private static ScoresDBHelper dbHelper;

    private static final int SEASON_ID = 100;
    private static final int SEASON = 101;
    private static final int SEASON_FULL = 102;

    private static final int TEAM_ID = 200;
    private static final int TEAM = 201;

    private static final int MATCH_ID = 300;
    private static final int MATCH = 301;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder bookFull;
    static {
        bookFull = new SQLiteQueryBuilder();
        bookFull.setTables(
                DatabaseContract.SeasonEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        DatabaseContract.MatchEntry.TABLE_NAME + " USING (" + DatabaseContract.MatchEntry._ID + ")");
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_SEASONS + "/#", SEASON_ID);
        matcher.addURI(authority, DatabaseContract.PATH_TEAMS + "/#", TEAM_ID);
        matcher.addURI(authority, DatabaseContract.PATH_MATCHES + "/#", MATCH_ID);

        matcher.addURI(authority, DatabaseContract.PATH_SEASONS, SEASON);
        matcher.addURI(authority, DatabaseContract.PATH_TEAMS, TEAM);
        matcher.addURI(authority, DatabaseContract.PATH_MATCHES, MATCH);

        matcher.addURI(authority, DatabaseContract.PATH_SEASON_WITH_MATCHES + "/#", SEASON_FULL);

        return matcher;
    }


    @Override
    public boolean onCreate()
    {
        dbHelper = new ScoresDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case SEASON_ID:
                return DatabaseContract.SeasonEntry.CONTENT_ITEM_TYPE;
            case TEAM_ID:
                return DatabaseContract.TeamEntry.CONTENT_ITEM_TYPE;
            case MATCH_ID:
                return DatabaseContract.MatchEntry.CONTENT_ITEM_TYPE;
            case SEASON:
                return DatabaseContract.SeasonEntry.CONTENT_TYPE;
            case TEAM:
                return DatabaseContract.TeamEntry.CONTENT_TYPE;
            case MATCH:
                return DatabaseContract.MatchEntry.CONTENT_TYPE;
            case SEASON_FULL:
                return DatabaseContract.SeasonEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case SEASON:
                rowsUpdated = db.update(DatabaseContract.SeasonEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MATCH:
                rowsUpdated = db.update(DatabaseContract.MatchEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        //Log.v(FetchScoreTask.LOG_TAG,uri.getPathSegments().toString());
        final int match = uriMatcher.match(uri);
        //Log.v(FetchScoreTask.LOG_TAG,SCORES_BY_LEAGUE);
        //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[0]);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(match));
        switch (match){
        case SEASON:
        retCursor = dbHelper.getReadableDatabase().query(
                DatabaseContract.SeasonEntry.TABLE_NAME,
                projection,
                selection,
                selection == null ? null : selectionArgs,
                null,
                null,
                sortOrder
        );
        break;
        case TEAM:
        retCursor = dbHelper.getReadableDatabase().query(
                DatabaseContract.TeamEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        break;
        case MATCH:
        retCursor = dbHelper.getReadableDatabase().query(
                DatabaseContract.MatchEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        break;
        case SEASON_ID:
        retCursor = dbHelper.getReadableDatabase().query(
                DatabaseContract.SeasonEntry.TABLE_NAME,
                projection,
                DatabaseContract.SeasonEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                selectionArgs,
                null,
                null,
                sortOrder
        );
        break;
        case TEAM_ID:
        retCursor = dbHelper.getReadableDatabase().query(
                DatabaseContract.TeamEntry.TABLE_NAME,
                projection,
                DatabaseContract.TeamEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                selectionArgs,
                null,
                null,
                sortOrder
        );
        break;
        case MATCH_ID:
        retCursor = dbHelper.getReadableDatabase().query(
                DatabaseContract.MatchEntry.TABLE_NAME,
                projection,
                DatabaseContract.MatchEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                selectionArgs,
                null,
                null,
                sortOrder
        );
        break;
        case SEASON_FULL:
        retCursor = bookFull.query(dbHelper.getReadableDatabase(),
                null,
                null,
                selectionArgs,
                DatabaseContract.SeasonEntry.TABLE_NAME + "." + DatabaseContract.SeasonEntry._ID,
                null,
                sortOrder);
        break;
        default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SEASON_ID: {
                long _id = db.insert(DatabaseContract.SeasonEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.SeasonEntry.buildSeasonUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(DatabaseContract.SeasonEntry.buildSeasonUri(_id), null);
                break;
            }
            case MATCH_ID: {
                long _id = db.insert(DatabaseContract.MatchEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DatabaseContract.MatchEntry.buildMatchUri(values.getAsLong("_id"));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //db.delete(DatabaseContract.SCORES_TABLE,null,null);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(muriMatcher.match(uri)));
        final int match = uriMatcher.match(uri);
        int count = 0;

        switch (match)
        {
            case MATCH:
                db.beginTransaction();

                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(DatabaseContract.MatchEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return count;


            case TEAM:
                db.beginTransaction();

                try
                {
                    for(ContentValues value : values)
                    {
                        long _id = db.insertWithOnConflict(DatabaseContract.TeamEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return count;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case SEASON:
                rowsDeleted = db.delete(
                        DatabaseContract.SeasonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MATCH:
                rowsDeleted = db.delete(
                        DatabaseContract.MatchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEAM:
                rowsDeleted = db.delete(
                        DatabaseContract.TeamEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SEASON_ID:
                rowsDeleted = db.delete(
                        DatabaseContract.SeasonEntry.TABLE_NAME,
                        DatabaseContract.SeasonEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
}
