package barqsoft.footballscores.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiaoma on 14/03/16.
 */
public class Match implements Parcelable{
    private String matchId;
    private String seasonId;
    private String matchDay;
    private String date;
    private String time;
    private String homeGoals;
    private String awayGoals;
    private String status;

    private String homeTeamId;
    private String awayTeamId;
    private Team homeTeam;
    private Team awayTeam;

    public Match() {
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(String homeGoals) {
        this.homeGoals = homeGoals;
    }

    public String getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(String awayGoals) {
        this.awayGoals = awayGoals;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(String homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public String getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(String awaysTeamId) {
        this.awayTeamId = awaysTeamId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(matchId);
        dest.writeString(seasonId);
        dest.writeString(matchDay);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(homeGoals);
        dest.writeString(awayGoals);
        dest.writeString(status);
        dest.writeString(homeTeamId);
        dest.writeString(awayTeamId);
        dest.writeParcelable(homeTeam, 0);
        dest.writeParcelable(awayTeam, 0);
    }

    public static final Parcelable.Creator<Match> CREATOR = new Parcelable.Creator<Match>() {
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    private Match(Parcel in) {
        matchId = in.readString();
        seasonId = in.readString();
        matchDay = in.readString();
        date = in.readString();
        time = in.readString();
        homeGoals = in.readString();
        awayGoals = in.readString();
        status = in.readString();
        homeTeamId = in.readString();
        awayTeamId = in.readString();
        homeTeam = in.readParcelable(Team.class.getClassLoader());
        awayTeam = in.readParcelable(Team.class.getClassLoader());
    }
}
