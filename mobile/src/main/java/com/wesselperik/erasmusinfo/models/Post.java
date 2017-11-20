package com.wesselperik.erasmusinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wessel on 20-11-2017.
 */

public class Post implements Parcelable {

    @SerializedName("id")
    public long ID;
    public String title;
    public String content;

    private Post(Parcel in) {
        ID = in.readLong();
        title = in.readString();
        content = in.readString();
    }

    public long getId() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(ID);
        parcel.writeString(title);
        parcel.writeString(content);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
