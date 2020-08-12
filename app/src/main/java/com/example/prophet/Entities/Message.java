package com.example.prophet.Entities;

public class Message {
    private String mBody;
    private String mDate;
    private String mAuthorId;
    private String mReceiverId;
    private String mImageUri;

    public Message() {
    }

    public Message(String mBody, String mDate, String mAuthorId, String receiver) {
        this.mBody = mBody;
        this.mDate = mDate;
        this.mAuthorId = mAuthorId;
        this.mReceiverId = receiver;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmBody(String mBody) {
        this.mBody = mBody;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void setmAuthorId(String mAuthorId) {
        this.mAuthorId = mAuthorId;
    }

    public void setmReceiverId(String mReceiverId) {
        this.mReceiverId = mReceiverId;
    }

    public String getmBody() {
        return mBody;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmAuthorId() {
        return mAuthorId;
    }

    public String getmReceiverId() {
        return mReceiverId;
    }
}
