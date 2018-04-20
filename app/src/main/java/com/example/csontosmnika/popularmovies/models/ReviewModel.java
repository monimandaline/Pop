package com.example.csontosmnika.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewModel implements Parcelable {

    private String author;
    private String content;

    public ReviewModel() {
    }

    public ReviewModel(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.content);
    }

    protected ReviewModel(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override
        public ReviewModel createFromParcel(Parcel source) {
            return new ReviewModel(source);
        }

        @Override
        public ReviewModel[] newArray(int size) {
            return new ReviewModel[size];
        }
    };
}
