package barqsoft.footballscores.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by xiaoma on 14/03/16.
 */
public class Season implements Parcelable{
    private String id;
    private String caption;
    private String league;
    private String year;
    private ArrayList<Match> matches;

    public Season() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(caption);
        dest.writeString(league);
        dest.writeString(year);
        dest.writeList(matches);
    }

    public static final Parcelable.Creator<Season> CREATOR = new Parcelable.Creator<Season>() {
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        public Season[] newArray(int size) {
            return new Season[size];
        }
    };

    private Season(Parcel in) {
        id = in.readString();
        caption = in.readString();
        league = in.readString();
        year = in.readString();
        in.readList(matches, this.getClass().getClassLoader());
    }
}
