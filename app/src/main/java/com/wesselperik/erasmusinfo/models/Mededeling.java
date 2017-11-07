package com.wesselperik.erasmusinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Wessel on 25-1-2017.
 */

public class Mededeling implements Parcelable {

    private int id;
    private String title;
    private String text;

    public Mededeling(int id, String title, String text){
        this.id = id;
        this.title = title;
        this.text = text;
    }

    private Mededeling(Parcel in) {
        id = in.readInt();
        title = in.readString();
        text = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(text);
    }

    public static final Creator<Mededeling> CREATOR = new Creator<Mededeling>() {
        @Override
        public Mededeling createFromParcel(Parcel in) {
            return new Mededeling(in);
        }

        @Override
        public Mededeling[] newArray(int size) {
            return new Mededeling[size];
        }
    };
}
