package com.example.csontosmnika.popularmovies.models;


import android.os.Parcel;
import android.os.Parcelable;

public class TrailerModel implements Parcelable {

    private String name;
    private String key;

    public TrailerModel() {
    }

    public TrailerModel(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.key);
    }

    protected TrailerModel(Parcel in) {
        this.name = in.readString();
        this.key = in.readString();
    }

    public static final Creator<TrailerModel> CREATOR = new Creator<TrailerModel>() {
        @Override
        public TrailerModel createFromParcel(Parcel source) {
            return new TrailerModel(source);
        }

        @Override
        public TrailerModel[] newArray(int size) {
            return new TrailerModel[size];
        }
    };
}
