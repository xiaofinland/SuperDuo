package barqsoft.footballscores.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiaoma on 14/03/16.
 */
public class Team implements Parcelable{
    private Integer id;
    private String name;
    private String thumbnail;
    private String shortName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Team() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return " Id: " + getId() + " Name: " + getName() + " Shortname: " + getShortName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(thumbnail);
        dest.writeString(shortName);
    }

    public static final Parcelable.Creator<Team> CREATOR = new Parcelable.Creator<Team>() {
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    private Team(Parcel in) {
        id = in.readInt();
        name = in.readString();
        thumbnail = in.readString();
        shortName = in.readString();
    }
}
