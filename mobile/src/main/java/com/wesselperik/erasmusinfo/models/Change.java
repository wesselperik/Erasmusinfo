package com.wesselperik.erasmusinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wesselperik on 21/11/2017.
 */

public class Change implements Parcelable {

    @SerializedName("id")
    public long ID;
    public String title;
    public List<ChangeItem> changes;

    private Change(Parcel in) {
        ID = in.readLong();
        title = in.readString();
        changes = new ArrayList<ChangeItem>();
        in.readList(changes, null);
    }

    public long getId() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public List<ChangeItem> getChanges() {
        return changes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(ID);
        parcel.writeString(title);
        parcel.writeList(changes);
    }

    public static final Creator<Change> CREATOR = new Creator<Change>() {
        @Override
        public Change createFromParcel(Parcel in) {
            return new Change(in);
        }

        @Override
        public Change[] newArray(int size) {
            return new Change[size];
        }
    };
}
