package com.example.prophet;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Adapters.ChatAdapter;
import com.example.prophet.Adapters.RequestsAdapter;
import com.example.prophet.Database.ProphetContract;
import com.example.prophet.Entities.Chat;
import com.example.prophet.Entities.Message;
import com.example.prophet.Entities.Request;
import com.example.prophet.Entities.SignedUser;
import com.example.prophet.Entities.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ProfileBottomSheetFragment.OnButtonClicker
        , RequestsAdapter.OnClickedListener {

    private static final int RC_SIGN_IN = 1;
    private static final String ANONYMOUS = "Anonymous";
    private Menu homeMenu;
    private DatabaseReference mChatRef;
    private DatabaseReference UsersRef;
    private DatabaseReference mUserChatsRef;
    private ChildEventListener mUserChatsChildEventListener;
    private ChildEventListener mChatChildEventListener;
    private ChildEventListener mFriendsChildEventListener;
    private DatabaseReference mFriendsRef;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private ChatAdapter chatAdapter;
    private int mReqNum = 0;

    @BindView(R.id.sender_rv)
    RecyclerView senderRv;

    @OnClick(R.id.fab_add_friends)
    public void onFabClicked(View view) {
        startActivity(new Intent(this, AddFriendActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.chats);
        }
        chatAdapter = new ChatAdapter(this, new ArrayList<>());
        senderRv.setLayoutManager(new LinearLayoutManager(this));
        senderRv.setAdapter(chatAdapter);
        senderRv.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void attachReferences() {
        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        mChatRef = FirebaseDatabase.getInstance().getReference().child("chats");
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friend_requests");
    }

    @Override
    protected void onResume() {
        super.onResume();
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // show ui
                onSignedInListener(user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), user.getUid());
            } else {
                onSignedOutListener();
                SignedUser.user = null;
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                        , new AuthUI.IdpConfig.GoogleBuilder().build())).build(),
                        RC_SIGN_IN);
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);


    }

    @Override
    protected void onPause() {
        super.onPause();
        detachFirebaseListeners();
        onSignedOutListener();
    }


    @Override
    public void onAcceptRequestClicked(Request request) {

        mFriendsRef.child(request.getmReqId()).child("mFlag").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                createChatRoom(request);
            }
        });
    }

    private void createChatRoom(Request request) {
        ArrayList<String> members = new ArrayList<>();
        members.add(request.getmSenderId());
        members.add(SignedUser.user.getmUid());
        DatabaseReference pushedRef = mChatRef.push();
        String chatID = pushedRef.getKey();
        Chat chat = new Chat(chatID, null, members
                , new Message("say hi to your new friend"
                , String.valueOf(System.currentTimeMillis()), null, null)
                , null);
        mChatRef.child(chatID).setValue(chat);
        mUserChatsRef.setValue(chatID);
    }

    private void detachFirebaseListeners() {
        if (authStateListener != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
        if (mChatChildEventListener != null)
            mChatRef.removeEventListener(mChatChildEventListener);
        if (mUserChatsChildEventListener != null)
            mUserChatsRef.removeEventListener(mUserChatsChildEventListener);
        mUserChatsChildEventListener = null;

        authStateListener = null;
        mChatChildEventListener = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ChatActivity.this, "signed in", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChatActivity.this, "failed to sign in", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    public void getFriendsRequests() {
        if (mFriendsChildEventListener == null) {
            mFriendsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Request request = snapshot.getValue(Request.class);
                    if (request != null
                            && request.getmReceiverId().matches(
                            SignedUser.user.getmUid())
                            && request.getmFlag().matches("false")) {
                        mReqNum++;
                    }
                    updateReqNumbers();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Request request = snapshot.getValue(Request.class);
                    if (request.getmFlag().matches("true")) {
                        mReqNum--;
                        RequestsBottomSheetFragment.getInstanceRef().adapter.removeItem(request.getmReqId());
                        updateReqNumbers();
                    }
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
        mFriendsRef.addChildEventListener(mFriendsChildEventListener);
    }

    private void updateReqNumbers() {
        TextView reqNum = homeMenu.findItem(R.id.friends_requests_menu).getActionView().findViewById(R.id.requests_num);
        if (mReqNum > 0) {
            reqNum.setVisibility(View.VISIBLE);
            reqNum.setText(String.valueOf(mReqNum));
        }
        else{
            reqNum.setVisibility(View.GONE);
        }
    }

    public void onSignedInListener(String displayName, String email, Uri photoUrl, String uid) {
        attachReferences();
        if (mChatChildEventListener == null)
            mChatChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Chat chat = snapshot.getValue(Chat.class);
                    chatAdapter.addChat(chat);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    finish();
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

        mChatRef.addChildEventListener(mChatChildEventListener);
        User user = new User(displayName, email, photoUrl.toString(), uid);
        UsersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    UsersRef.child(uid).setValue(user);
                    SignedUser.user = user;
                } else {
                    SignedUser.user = snapshot.getValue(User.class);
                }
                getFriendsRequests();
                userChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userChats() {
        mUserChatsRef = FirebaseDatabase.getInstance().getReference().child("user_chats").child(SignedUser.user.getmUid());
        if (mUserChatsChildEventListener == null) {
            mUserChatsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
        mUserChatsRef.addChildEventListener(mUserChatsChildEventListener);

    }

    private void onSignedOutListener() {
        chatAdapter.clear();
        mReqNum = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SignedUser.user = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = new String[]{
                ProphetContract.Friends.COLUMN_NAME,
                ProphetContract.Friends.COLUMN_IMAGE
        };
        return new CursorLoader(this, ProphetContract.Friends.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        chatAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        chatAdapter.swapCursor(null);

    }

    @Override
    public void onAddInterestBtnClicked(String inter) {
        ArrayList<String> newList = SignedUser.user.getmInterests();
        newList.add(inter);
        UsersRef.child(SignedUser.user.getmUid()).child("mInterests").setValue(newList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChatActivity.this, R.string.added, Toast.LENGTH_SHORT).show();
                SignedUser.user.getmInterests().add(inter);
            }
        });
    }

    @Override
    public void onEditAboutMeBtnClicked(String aboutMe) {
        UsersRef.child(SignedUser.user.getmUid()).child("mAboutMe").setValue(aboutMe).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChatActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                SignedUser.user.setmAboutMe(aboutMe);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        homeMenu = menu;
        menu.findItem(R.id.friends_requests_menu).getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestsBottomSheetFragment.getInstance(ChatActivity.this).show(
                        getSupportFragmentManager(), RequestsBottomSheetFragment.TAG
                );
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_menu:
                ProfileBottomSheetFragment profile = ProfileBottomSheetFragment.getInstance(SignedUser.user, this);
                profile.show(getSupportFragmentManager(), ProfileBottomSheetFragment.TAG);
                return true;
            case R.id.settings_menu:
                // handle logging out
                // AuthUI.getInstance().signOut(this);
                return true;
            case R.id.log_out_menu:
                // handle logging out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}