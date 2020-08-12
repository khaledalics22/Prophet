package com.example.prophet.Entities;

public class Request {
    private String mSenderId;
    private String mReceiverId;
    private String mFlag = "false";
    private String mReceiverImageUri;
    private String mSenderImageUri;
    private String mReceiverName;
    private String mSenderName;
    private String mReqId;

    public Request() {
    }

    public Request(String mReceiverImageUri
            , String mSenderId
            , String mReceiverId
            , String mReceiverName
            , String mReqId
            , String mSenderName
            , String mSenderImageUri) {
        this.mSenderId = mSenderId;
        this.mReceiverId = mReceiverId;
        this.mReceiverImageUri = mReceiverImageUri;
        this.mReceiverName = mReceiverName;
        this.mReqId = mReqId;
        this.mSenderImageUri = mSenderImageUri;
        this.mSenderName = mSenderName;
    }

    public void setmSenderImageUri(String mSenderImageUri) {
        this.mSenderImageUri = mSenderImageUri;
    }

    public void setmSenderName(String mSenderName) {
        this.mSenderName = mSenderName;
    }

    public String getmSenderImageUri() {
        return mSenderImageUri;
    }

    public String getmSenderName() {
        return mSenderName;
    }

    public void setmReqId(String mReqId) {
        this.mReqId = mReqId;
    }

    public void setmReceiverName(String mReceiverName) {
        this.mReceiverName = mReceiverName;
    }

    public String getmReceiverName() {
        return mReceiverName;
    }

    public void setmSenderId(String mSenderId) {
        this.mSenderId = mSenderId;
    }

    public void setmReceiverId(String mReceiverId) {
        this.mReceiverId = mReceiverId;
    }

    public void setmFlag(String mFlag) {
        this.mFlag = mFlag;
    }

    public void setmReceiverImageUri(String mReceiverImageUri) {
        this.mReceiverImageUri = mReceiverImageUri;
    }

    public String getmSenderId() {
        return mSenderId;
    }

    public String getmReceiverId() {
        return mReceiverId;
    }

    public String getmFlag() {
        return mFlag;
    }

    public String getmReceiverImageUri() {
        return mReceiverImageUri;
    }

    public String getmReqId() {
        return mReqId;
    }

}
