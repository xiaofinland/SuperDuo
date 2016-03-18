package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.utils.Parser;
import barqsoft.footballscores.utils.RestClient;
import database.DaoHelper;
import database.DatabaseContract;
import database.DatabaseContract.TeamEntry;


/**
 * Created by xiaoma on 14/03/16.
 */
public class TeamService extends IntentService {

    private final String LOG_TAG = TeamService.class.getSimpleName();

    //PARAM
    public static final String MESSAGE = "MESSAGE";
    public static final String SPINNER_ACTIVE = "SPINNER_ACTIVE";
    public static final String SPINNER_NOT_ACTIVE = "SPINNER_NOT_ACTIVE";
    public static final String SPINNER_STATUS = "SPINNER_STATUS";
    private static final String RECEIVER = "RECEIVER";
    public static final int NEW_PROGRESS = 1;
    public static final String PROGRESS = "PROGRESS";

    //Actions
    public static final String ACTION_GET_TEAMS = "ACTION_GET_TEAMS";

    //Notifications
    public static final String NOTIFICATION_GET_TEAMS = "NOTIFICATION_GET_TEAMS";
    public static final String NOTIFICATION_SPINNER_STATUS = "NOTIFICATION_SPINNER_STATUS";

    public TeamService() {
        super("TeamService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG,"reach onHandleIntent");
        if (null != intent) {
            final String action = intent.getAction();
            if (ACTION_GET_TEAMS.equals(action)) {

                // obtaining ResultReceiver from Intent that started this IntentService
                ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
                getAllTeamsAndPersistThem(receiver);
            }
        }
    }

    /**
     * Gets the teams provided by the service and persist them.
     *
     * @param receiver Receiver object which is used to send
     *                 data about download progress of teams.
     */
    private void getAllTeamsAndPersistThem(ResultReceiver receiver) {
        Log.d(LOG_TAG, "Reached getAllTeamsAndPersistThem");
        DaoHelper daoHelper = new DaoHelper(this);

        if (daoHelper.getTeamCount() == 226) {
            activeSpinner(SPINNER_NOT_ACTIVE);
            return;
        }

        final ArrayList<String> leagues = new ArrayList<>();
        leagues.add("394");
        leagues.add("395");
        leagues.add("396");
        leagues.add("397");
        leagues.add("398");
        leagues.add("399");
        leagues.add("400");
        leagues.add("401");
        leagues.add("402");
        leagues.add("403");
        leagues.add("404");
        leagues.add("405");

        ArrayList<Team> teams = new ArrayList<>();

        for (String league : leagues) {
            try {
                URL url = createSeasonTeamUrl(league);
                String result = RestClient.getData(url.toString(), getString(R.string.api_key));

                ArrayList<Team> teamsTemp = Parser.parseTeams(result);
                teams.addAll(teamsTemp);

            } catch (MalformedURLException urlEx) {
                Log.e(LOG_TAG, "Error ", urlEx);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            }
        }

        //workaround - change http header per https
        changeHeaderForRequestHttps(teams);

        Vector<ContentValues> values = new Vector<>(leagues.size());
        int load = 0;
        for (Team team : teams) {

            if (null != team) {
                ContentValues teamValues = new ContentValues();
                teamValues.put(DatabaseContract.TeamEntry._ID, team.getId());
                teamValues.put(DatabaseContract.TeamEntry.NAME, team.getName());
                teamValues.put(DatabaseContract.TeamEntry.SHORT_NAME, team.getShortName());
                teamValues.put(DatabaseContract.TeamEntry.THUMBNAIL, team.getThumbnail());
                values.add(teamValues);
                Log.d("item downloaded", String.valueOf(team.getId()));
            }

            // data that will be send into ResultReceiver
            Bundle data = new Bundle();
            data.putInt(PROGRESS, ++load);

            // sends progress into ResultReceiver located in your Activity
            receiver.send(NEW_PROGRESS, data);
        }

        // activeSpinner(SPINNER_ACTIVE);
        insertData(values);
        activeSpinner(SPINNER_NOT_ACTIVE);
    }
    private void insertData(Vector<ContentValues> values) {

        Log.d("Insert", "number of items + " + values.size());

        ContentValues[] insertData = new ContentValues[values.size()];
        values.toArray(insertData);
        int inserted_data = getContentResolver().bulkInsert(DatabaseContract.TeamEntry.CONTENT_URI, insertData);

        String msg = "Successfully Inserted : " + String.valueOf(inserted_data);
        Log.v(LOG_TAG, msg);

        publishResults(msg);
    }

    private void publishResults(String message) {
        Intent intent = new Intent();
        intent.putExtra(MESSAGE, message);
        intent.setAction(NOTIFICATION_GET_TEAMS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void activeSpinner(String message) {
        Intent intent = new Intent();
        intent.putExtra(SPINNER_STATUS, message);
        intent.setAction(NOTIFICATION_SPINNER_STATUS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @NonNull
    private URL createSeasonTeamUrl(@NonNull String seasonId) throws MalformedURLException {
        final String BASE_URL = "http://api.football-data.org/v1/soccerseasons/";

        final String TEAMS = "teams";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(seasonId)
                .appendPath(TEAMS)
                .build();

        return new URL(builtUri.toString());
    }

    private void changeHeaderForRequestHttps(ArrayList<Team> teams) {
        for (Team team : teams) {
            String thumbnail = team.getThumbnail();
            if (!thumbnail.contains("https")) {
                team.setThumbnail(thumbnail.replace("http", "https"));
            }
            if (thumbnail.startsWith("upload")) {
                team.setThumbnail("https://" + thumbnail);
            }
        }
    }


}
