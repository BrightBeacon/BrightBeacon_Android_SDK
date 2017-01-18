package com.brtbeacon.sdk.demo;

import android.os.Parcel;
import android.os.Parcelable;

public class ParameterInfo implements Parcelable {
    public String name;
    public String text;
    public int number;
    
    public ParameterInfo(String name, String text, int number) {
        this.name = name;
        this.text = text;
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.text);
        dest.writeInt(this.number);
    }

    public ParameterInfo() {
    }

    protected ParameterInfo(Parcel in) {
        this.name = in.readString();
        this.text = in.readString();
        this.number = in.readInt();
    }
    
    @Override
    public String toString() {
    	return this.name;
    }

    public static final Creator<ParameterInfo> CREATOR = new Creator<ParameterInfo>() {
        @Override
        public ParameterInfo createFromParcel(Parcel source) {
            return new ParameterInfo(source);
        }

        @Override
        public ParameterInfo[] newArray(int size) {
            return new ParameterInfo[size];
        }
    };
    
}
