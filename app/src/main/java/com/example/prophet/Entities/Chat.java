package com.example.prophet.Entities;

import java.util.ArrayList;
import java.util.HashMap;

public class Chat {
    private ArrayList<String> mMembers;
    private Message mLastMessage;
    private String mImageUri;
    private String mChatName;
    private String mChatId;

    public Chat() {
        this.mMembers = new ArrayList<String>();
        this.mLastMessage = new Message(null, null, null, null);
    }

    public Chat(String mChatId, String mChatName, ArrayList<String> mMembers, Message mLastMessage, String mImageUri) {
        this.mMembers = mMembers;
        this.mLastMessage = mLastMessage;
        this.mImageUri = mImageUri;
        this.mChatName = mChatName;
        this.mChatId = mChatId;
    }

    public void setmChatId(String mChatId) {
        this.mChatId = mChatId;
    }

    public void setmChatName(String mChatName) {
        this.mChatName = mChatName;
    }

    public String getmChatName() {
        return mChatName;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public void setmMembers(ArrayList<String> mMembers) {
        this.mMembers = mMembers;
    }

    public void setmLastMessage(Message mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    public ArrayList<String> getmMembers() {
        return mMembers;
    }

    public Message getmLastMessage() {
        return mLastMessage;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public String getmChatId() {
        return mChatId;
    }
}
