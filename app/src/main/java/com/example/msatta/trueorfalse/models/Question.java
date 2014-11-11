package com.example.msatta.trueorfalse.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by michele on 09/11/14.
 */
public class Question implements Parcelable {

    private String text;
    private boolean answer;

    public Question(){

    }

    public Question(Parcel parcel){
        this.text = parcel.readString();
        this.answer = parcel.readByte() != 0;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeByte((byte) (this.answer ? 1 : 0));

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
