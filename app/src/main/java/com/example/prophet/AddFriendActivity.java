package com.example.prophet;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Adapters.UserAdapter;
import com.example.prophet.Entities.Request;
import com.example.prophet.Entities.SignedUser;
import com.example.prophet.Entities.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendActivity extends AppCompatActivity implements UserAdapter.OnClickedListener, UsersProfileBottomSheetFragment.OnButtonClicker {
    @BindView(R.id.user_rv)
    RecyclerView usersRecView;
    @BindView(R.id.user_sv)
    SearchView searchView;
    @BindView(R.id.friend_requests_sheet)
    TextView sheetTitle;
    RecyclerView.LayoutManager mUsersLayout;
    UserAdapter mUsersAdapter;

    private DatabaseReference mUsersRef;
    private ChildEventListener mOnNewUsersListener;
    private DatabaseReference mFriendsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_acitivity);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_friends);
        }
        searchView.setVisibility(View.VISIBLE);
        sheetTitle.setVisibility(View.GONE);
        mUsersAdapter = new UserAdapter(this, new ArrayList<>());
        mUsersLayout = new LinearLayoutManager(this);
        usersRecView.setHasFixedSize(true);
        usersRecView.setLayoutManager(mUsersLayout);
        usersRecView.setAdapter(mUsersAdapter);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friend_requests");
    }

    @Override
    public void onAddFriendClickListener(String uId, String imageUri, String name) {
        // add friend
        addFriendRequest(uId, imageUri, name);
    }

    private void addFriendRequest(String uId, String imageUri, String name) {
        DatabaseReference pushedReq = mFriendsRef.push();
        String key = pushedReq.getKey();
        Request request = new Request(imageUri, SignedUser.user.getmUid()
                , uId, name, key, SignedUser.user.getmName(), SignedUser.user.getmImageUri());
        mFriendsRef.child(key).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddFriendActivity.this, getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(AddFriendActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onAddFriendClicked(String uid, String imageUri, String name) {
        // add friend
        addFriendRequest(uid, imageUri, name);
    }

    @Override
    public void onUserClicked(User user) {
        UsersProfileBottomSheetFragment.getInstance(user, AddFriendActivity.this).show(
                getSupportFragmentManager(), UsersProfileBottomSheetFragment.TAG);
    }

    private void attachFirebaseListeners() {
        mOnNewUsersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (!SignedUser.user.getmUid().matches(user.getmUid()))
                    mUsersAdapter.addUser(user);
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
        mUsersRef.addChildEventListener(mOnNewUsersListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUsersRef.removeEventListener(mOnNewUsersListener);
        mUsersAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachFirebaseListeners();

    }
}