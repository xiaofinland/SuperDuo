package barqsoft.footballscores.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by xiaoma on 14/03/16.
 */
public class FootballSeason implements Parcelable {
    ArrayList<Season> seasons;

    public FootballSeason() {
        seasons = new ArrayList<>();
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(seasons);
    }

    public static final Parcelable.Creator<FootballSeason> CREATOR = new Parcelable.Creator<FootballSeason>() {
        public FootballSeason createFromParcel(Parcel in) {
            return new FootballSeason(in);
        }

        public FootballSeason[] newArray(int size) {
            return new FootballSeason[size];
        }
    };

    private FootballSeason(Parcel in) {
        in.readList(seasons, this.getClass().getClassLoader());
    }
}
