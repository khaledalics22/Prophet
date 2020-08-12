package com.example.prophet;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Adapters.MessageAdapter;
import com.example.prophet.Entities.Message;
import com.example.prophet.Entities.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessagesActivity extends AppCompatActivity {

    private DatabaseReference mChatMessagesRef;
    private ChildEventListener mChatMsgsChildEventListener;
    private MessageAdapter messageAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String chatID;

    @BindView(R.id.messages_rv)
    RecyclerView recyclerView;

    @BindView(R.id.message_et)
    EditText messageEt;

    @OnClick(R.id.send_message_btn)
    public void onSendClicked() {
        if (!messageEt.getText().toString().matches("")) {
            Message message = new Message(
                    messageEt.getText().toString().trim()
                    , String.valueOf(System.currentTimeMillis())
                    , Utils.user.getmUid()
                    , "null");
            mChatMessagesRef.child(chatID).push().setValue(message);
            messageEt.setText("");
            FirebaseDatabase.getInstance().getReference().child("chats").child(chatID).child("mLastMessage").setValue(message);
        } else {
            Toast.makeText(this, R.string.empty_message, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        chatID = getIntent().getStringExtra("uid");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(getIntent().getStringExtra("chat_name"));
        }
        if (chatID == null) {
            Toast.makeText(this, R.string.Error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        messageAdapter = new MessageAdapter(this, null);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setHasFixedSize(false);
        mChatMessagesRef = FirebaseDatabase.getInstance().getReference().child("chat_messages");
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom<oldBottom){
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()>0?messageAdapter.getItemCount()-1:0);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatMsgsChildEventListener == null) {
            mChatMsgsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Message message = snapshot.getValue(Message.class);
                    messageAdapter.addMessage(message);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        }
        mChatMessagesRef.child(chatID).addChildEventListener(mChatMsgsChildEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mChatMsgsChildEventListener != null)
            mChatMessagesRef.removeEventListener(mChatMsgsChildEventListener);

        mChatMsgsChildEventListener = null;
        messageAdapter.clear();

    }

}