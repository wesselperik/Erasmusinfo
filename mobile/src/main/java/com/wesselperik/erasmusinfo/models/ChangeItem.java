package com.wesselperik.erasmusinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wesselperik on 22/11/2017.
 */

public class ChangeItem implements Parcelable {

    public String itemClass;
    public String itemHour;
    public String itemTeacher;
    public String itemComment;

    private ChangeItem(Parcel in) {
        itemClass = in.readString();
        itemHour = in.readString();
        itemTeacher = in.readString();
        itemComment = in.readString();
    }

    public String getItemClass() {
        return itemClass;
    }

    public String getItemHour() {
        return itemHour;
    }

    public String getItemTeacher() {
        return itemTeacher;
    }

    public String getItemComment() {
        return itemComment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemClass);
        parcel.writeString(itemHour);
        parcel.writeString(itemTeacher);
        parcel.writeString(itemComment);
    }

    public static final Creator<ChangeItem> CREATOR = new Creator<ChangeItem>() {
        @Override
        public ChangeItem createFromParcel(Parcel in) {
            return new ChangeItem(in);
        }

        @Override
        public ChangeItem[] newArray(int size) {
            return new ChangeItem[size];
        }
    };
}