package nz.co.zzi.spotify.infrastructure.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joao.gavazzi on 7/06/15.
 */
public class Track implements Parcelable {

    public static final Parcelable.Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    private String mName;
    private String mAlbum;
    private String mSmallImageUrl;
    private String mLargeImageUrl;
    private String mPreviewUrl;

    public Track(Parcel parcel) {
        mName = parcel.readString();
        mAlbum = parcel.readString();
        mSmallImageUrl = parcel.readString();
        mLargeImageUrl = parcel.readString();
        mPreviewUrl = parcel.readString();
    }

    public Track(String name, String album, String smallImageUrl, String largeImageUrl, String previewUrl) {
        mName = name;
        mAlbum = album;
        mSmallImageUrl = smallImageUrl;
        mLargeImageUrl = largeImageUrl;
        mPreviewUrl = previewUrl;
    }

    public String getName() {
        return mName;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getSmallImageUrl() {
        return mSmallImageUrl;
    }

    public String getLargeImageUrl() {
        return mLargeImageUrl;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mAlbum);
        dest.writeString(mSmallImageUrl);
        dest.writeString(mLargeImageUrl);
        dest.writeString(mPreviewUrl);
    }
}
