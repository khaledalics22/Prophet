package com.example.prophet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Adapters.RequestsAdapter;
import com.example.prophet.Entities.Request;
import com.example.prophet.Entities.SignedUser;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestsBottomSheetFragment extends BottomSheetDialogFragment {

    private static boolean isEditingAboutMe = false;

    public static final String TAG = "REQUESTS_TAG";
    private DatabaseReference friendsRef;

    public RequestsBottomSheetFragment(Context context) {
    }
    private static RequestsBottomSheetFragment mInstance;
    public static RequestsBottomSheetFragment getInstance(Context context) {
        mInstance  = new RequestsBottomSheetFragment(context);
        return mInstance;

    }

    @BindView(R.id.user_rv)
    RecyclerView recyclerView;
    @BindView(R.id.user_sv)
    SearchView searchView;
    @BindView(R.id.friend_requests_sheet)
    TextView sheetTitle;
    RecyclerView.LayoutManager layoutManager;
    RequestsAdapter adapter;
    private ChildEventListener onRequestsEventListener;

    public static RequestsBottomSheetFragment getInstanceRef() {
        return mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_friend_acitivity, container, false);
        ButterKnife.bind(this, view);
        searchView.setVisibility(View.GONE);
        sheetTitle.setVisibility(View.VISIBLE);
        adapter = new RequestsAdapter(getContext(), new ArrayList<>());
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (onRequestsEventListener == null) {
            onRequestsEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Request request = snapshot.getValue(Request.class);
                    if (request != null
                            && request.getmReceiverId().matches(SignedUser.user.getmUid())
                            && request.getmFlag().matches("false"))
                        adapter.addRequest(request);
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
        friendsRef = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        friendsRef.addChildEventListener(onRequestsEventListener);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (onRequestsEventListener != null)
            friendsRef.removeEventListener(onRequestsEventListener);
        onRequestsEventListener = null;
    }

    // full screen
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }


    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
