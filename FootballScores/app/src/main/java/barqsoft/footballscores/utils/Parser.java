package barqsoft.footballscores.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import barqsoft.footballscores.model.Match;
import barqsoft.footballscores.model.Season;
import barqsoft.footballscores.model.Team;

/**
 * Created by xiaoma on 14/03/16.
 */
public class Parser {
    public static final String LOG_TAG = Parser.class.getSimpleName();

    private static final String SEASON_LINK = "http://api.football-data.org/v1/soccerseasons/";
    private static final String LINKS = "_links";
    private static final String SELF = "self";

    /**
    Parse JSON for list of season
    *
     * @param result The data retrieved by the service.
     * @return seasonsResult.
     */

    public static ArrayList<Season> parseSeasonsJSON(String result) {

        final ArrayList<String> leaguesOut = new ArrayList<>();
        leaguesOut.add("380");
        leaguesOut.add("396");
        leaguesOut.add("397");
        leaguesOut.add("400");
        leaguesOut.add("401");
        leaguesOut.add("402");
        leaguesOut.add("403");
        leaguesOut.add("404");

        //Season data
        String CAPTION = "caption";
        String LEAGUE = "league";
        String YEAR = "year";

        ArrayList<Season> seasonsResult = new ArrayList<>();

        try {

            JSONArray seasons = new JSONArray(result);

            for (int i = 0; i < seasons.length(); i++) {

                Season soccerSeason = new Season();

                JSONObject season = seasons.getJSONObject(i);

                String league = season.getJSONObject(LINKS).getJSONObject(SELF).getString("href");
                league = league.replace(SEASON_LINK, "");

                if (!leaguesOut.contains(league)) {
                    soccerSeason.setId(league);
                    soccerSeason.setCaption(season.getString(CAPTION));
                    soccerSeason.setLeague(season.getString(LEAGUE));
                    soccerSeason.setYear(season.getString(YEAR));

                    seasonsResult.add(soccerSeason);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return seasonsResult;
    }

    /**
     * Parses a JSON for list of match.
     *
     * @param result The data retrieved by the service.
     * @return matchesSoccer.
     */
    public static ArrayList<Match> parseMatchesJSON(String result) {

        final String SOCCER_SEASON = "soccerseason";

        final String MATCH_LINK = "http://api.football-data.org/v1/fixtures/";
        final String MATCH_DAY = "matchday";
        final String HOME_TEAM = "homeTeam";
        final String AWAY_TEAM = "awayTeam";

        final String FIXTURES = "fixtures";
        final String MATCH_DATE = "date";
        final String STATUS = "status";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";

        ArrayList<Match> matchesSoccer = new ArrayList<>();

        try {

            JSONArray matches = new JSONObject(result).getJSONArray(FIXTURES);


            for (int i = 0; i < matches.length(); i++) {

                Match match = new Match();
                JSONObject matchJSON = matches.getJSONObject(i);

                String matchId = matchJSON.getJSONObject(LINKS).getJSONObject(SELF).
                        getString("href");
                matchId = matchId.replace(MATCH_LINK, "");

                match.setMatchId(matchId);

                String league = matchJSON.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).getString("href");
                league = league.replace(SEASON_LINK, "");

                match.setSeasonId(league);

                String mDate = matchJSON.getString(MATCH_DATE);
                String mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                String[] partTime = mTime.split(":");
                mTime = partTime[0] + ":" + partTime[1];
                mDate = mDate.substring(0, mDate.indexOf("T"));

                match.setDate(mDate);
                match.setTime(mTime);
                match.setStatus(matchJSON.getString(STATUS));
                match.setMatchDay(matchJSON.getString(MATCH_DAY));

                String homeTeamId = matchJSON.getJSONObject(LINKS).getJSONObject(HOME_TEAM).
                        getString("href");

                String[] parts = homeTeamId.split("/");

                match.setHomeTeamId(parts[parts.length - 1]);

                String awayTeamId = matchJSON.getJSONObject(LINKS).getJSONObject(AWAY_TEAM).
                        getString("href");

                parts = awayTeamId.split("/");

                match.setAwayTeamId(parts[parts.length - 1]);

                JSONObject resultMatch = matchJSON.getJSONObject(RESULT);
                String homeGoals = resultMatch.getString(HOME_GOALS);

                if (homeGoals != null && !homeGoals.isEmpty()) {
                    match.setHomeGoals(homeGoals);
                }

                String awayGoals = resultMatch.getString(AWAY_GOALS);

                if (awayGoals != null && !awayGoals.isEmpty()) {
                    match.setAwayGoals(resultMatch.getString(AWAY_GOALS));
                }

                matchesSoccer.add(match);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return matchesSoccer;
    }

    /**
     * Parses a JOSN for list of teams.
     *
     * @param result The data retrieved by the service.
     * @return teams.
     */
    public static ArrayList<Team> parseTeams(String result) {

        ArrayList<Team> teams = new ArrayList<>();

        final String MATCH_LINK = "http://api.football-data.org/v1/teams/";

        //Season data
        String NAME = "name";
        String SHORT_NAME = "shortName";
        String THUMBNAIL = "crestUrl";
        String ERROR = "error";
        String TEAMS = "teams";

        try {

            JSONArray teamsJSON = new JSONObject(result).getJSONArray(TEAMS);

            for (int i = 0; i <= teamsJSON.length(); i++) {

                JSONObject teamJSON = teamsJSON.getJSONObject(i);

                if (!teamJSON.has(ERROR)) {
                    Team team = new Team();

                    String teamId = teamJSON.getJSONObject(LINKS).getJSONObject(SELF).
                            getString("href");
                    teamId = teamId.replace(MATCH_LINK, "");
                    team.setId(Integer.parseInt(teamId));
                    team.setName(teamJSON.getString(NAME));
                    team.setShortName(teamJSON.getString(SHORT_NAME));
                    team.setThumbnail(teamJSON.getString(THUMBNAIL));
                    changeThumbnailPath(team);

                    Log.d("Team", team.toString());

                    teams.add(team);
                }
            }

        } catch (JSONException je) {
            Log.e(LOG_TAG, je.getMessage());
        }

        return teams;
    }

    /**
     * Parses a JSON for a team.
     *
     * @param result The data retrieved by the service.
     * @return team.
     */
    public static Team parseTeam(String result) {

        //Season data
        String NAME = "name";
        String SHORT_NAME = "shortName";
        String THUMBNAIL = "crestUrl";
        String ERROR = "error";

        try {
            JSONObject teamJSON = new JSONObject(result);
            if (!teamJSON.has(ERROR)) {
                Team team = new Team();
                team.setName(teamJSON.getString(NAME));
                team.setShortName(teamJSON.getString(SHORT_NAME));
                team.setThumbnail(teamJSON.getString(THUMBNAIL));
                changeThumbnailPath(team);

                Log.d("Team", team.toString());

                return team;
            }
        } catch (JSONException je) {
            Log.e(LOG_TAG, je.getMessage());
        }

        return null;
    }


    /**
     * Implements an algorithm in order to sanity the thumbnail team's url.
     *
     * @param team Team Object.
     */
    private static void changeThumbnailPath(Team team) {

        String thumbnail = team.getThumbnail();

        if (thumbnail != null) {

            String[] partsExt = thumbnail.split("\\.");

            if (partsExt.length > 1) {

                String ext = partsExt[partsExt.length - 1];

                if (!thumbnail.isEmpty() && ext.equals("svg")) {
                    String[] parts = thumbnail.split("wikipedia/");
                    String partOne = parts[1];
                    //quit characters which represent the country
                    String[] noCountry = partOne.split("/", 2);
                    //get the team's name
                    String[] name = partOne.split("/");
                    String newPath = parts[0] + "wikipedia/" + name[0] + "/thumb/" + noCountry[1] + "/200px-" + name[name.length - 1] + ".png";
                    team.setThumbnail(newPath);
                }
            }
        }
    }
}
