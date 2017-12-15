package com.wesselperik.erasmusinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wessel on 20-11-2017.
 */

public class News implements Parcelable {

    public String title;
    public String shortText;
    public String text;
    public String category;
    public String date;
    public String url;
    public String image;

    public News(String title, String shortText, String category, String date, String url) {
        this.title = title;
        this.shortText = shortText;
        this.text = "";
        this.category = category;
        this.date = date;
        this.url = url;
        this.image = "";
    }

    public News(String title, String shortText, String text, String category, String date, String url, String image) {
        this.title = title;
        this.shortText = shortText;
        this.text = text;
        this.category = category;
        this.date = date;
        this.url = url;
        this.image = image;
    }

    private News(Parcel in) {
        title = in.readString();
        shortText = in.readString();
        text = in.readString();
        category = in.readString();
        date = in.readString();
        url = in.readString();
        image = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getShortText() {
        return shortText;
    }

    public String getText() {
        return text;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return getTitle() + " - " + getDate() + " - " + getCategory() + " - " + getShortText();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(shortText);
        parcel.writeString(text);
        parcel.writeString(category);
        parcel.writeString(date);
        parcel.writeString(url);
        parcel.writeString(image);
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
