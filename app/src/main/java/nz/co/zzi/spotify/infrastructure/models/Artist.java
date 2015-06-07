package nz.co.zzi.spotify.infrastructure.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joao.gavazzi on 6/06/15.
 */
public class Artist implements Parcelable {

    public static final Parcelable.Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    private String mId;
    private String mImageUrl;
    private String mName;

    public Artist(final Parcel parcel) {
        mId = parcel.readString();
        mImageUrl = parcel.readString();
        mName = parcel.readString();
    }

    public Artist(String id, String imageUrl, String name) {
        mId = id;
        mImageUrl = imageUrl;
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mImageUrl);
        dest.writeString(mName);
    }

    public String getId() {
        return mId;
    }
}
