package com.example.prophet.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String mName;
    private String mEmail;
    private String mImageUri;
    private String mUid;
    private String mAboutMe;
    private ArrayList<String> mInterests;


    public User() {
    }

    public User(String mName, String mEmail, String mImage, String uid) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mImageUri = mImage;
        this.mUid = uid;
        mAboutMe = "";
        mInterests = new ArrayList<>();
        mInterests.add("football");
        mInterests.add("basket ball");
        mInterests.add("swimming");

    }

    public void setmAboutMe(String mAboutMe) {
        this.mAboutMe = mAboutMe;
    }

    public void addInterests(String inter) {
        if (mInterests == null) {
            mInterests = new ArrayList<String>();
        }
        mInterests.add(inter);
    }

    public void setmInterests(ArrayList<String> mInterests) {
        this.mInterests = mInterests;
    }

    public ArrayList<String> getmInterests() {
        return mInterests;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }


    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String getmName() {
        return mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public String getmAboutMe() {
        return mAboutMe;
    }
}
