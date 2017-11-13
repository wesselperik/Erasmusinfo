package com.wesselperik.erasmusinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Wessel on 25-1-2017.
 */

public class Nieuws implements Parcelable {

    private int id;
    private String title;
    private String date;
    private String category;
    private String header;
    private String text;
    private String image;

    public Nieuws(int id, String title, String date, String category, String header, String text, String image){
        this.id = id;
        this.title = title;
        this.date = date;
        this.category = category;
        this.header = header;
        this.text = text;
        this.image = image;
    }

    private Nieuws(Parcel in) {
        id = in.readInt();
        title = in.readString();
        date = in.readString();
        category = in.readString();
        header = in.readString();
        text = in.readString();
        image = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(category);
        parcel.writeString(header);
        parcel.writeString(text);
        parcel.writeString(image);
    }

    public static final Creator<Nieuws> CREATOR = new Creator<Nieuws>() {
        @Override
        public Nieuws createFromParcel(Parcel in) {
            return new Nieuws(in);
        }

        @Override
        public Nieuws[] newArray(int size) {
            return new Nieuws[size];
        }
    };
}
