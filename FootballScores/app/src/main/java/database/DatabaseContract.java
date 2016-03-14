package database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class DatabaseContract{
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_MATCHES = "match";
    public static final String PATH_SEASONS = "season";
    public static final String PATH_TEAMS = "team";
    public static final String PATH_SEASON_WITH_MATCHES = "seasonsWithMatches";

    public static final class MatchEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MATCHES).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MATCHES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MATCHES;
        //Table data
        public static final String TABLE_NAME = "matches";
        public static final String SEASON_ID = "season_id";
        public static final String MATCH_DAY = "match_day";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String HOME_TEAM_ID = "home_team_id";
        public static final String AWAY_TEAM_ID = "away_tam_id";
        public static final String HOME_GOALS = "home_goals";
        public static final String AWAY_GOALS = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String STATUS = "status";

        public static Uri buildMatchUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SeasonEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEASONS).build();

        public static final Uri FULL_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEASON_WITH_MATCHES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SEASONS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SEASONS;

        public static final String TABLE_NAME = "seasons";

        public static final String CAPTION = "caption";

        public static final String LEAGUE = "league";

        public static final String YEAR = "year";

        public static Uri buildSeasonUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSeasonWithMatchesUri(long id) {
            return ContentUris.withAppendedId(FULL_CONTENT_URI, id);
        }
    }

    public static final class TeamEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEAMS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TEAMS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TEAMS;

        public static final String TABLE_NAME = "teams";

        public static final String NAME = "name";

        public static final String SHORT_NAME = "short_name";

        public static final String THUMBNAIL = "thumbnail";

        public static Uri buildEntryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
