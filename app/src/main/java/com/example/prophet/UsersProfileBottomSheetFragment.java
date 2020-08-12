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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prophet.Entities.User;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersProfileBottomSheetFragment extends BottomSheetDialogFragment {
    @BindView(R.id.user_image_sheet)
    CircleImageView myImage;
    @BindView(R.id.user_name_sheet)
    TextView userName;
    @BindView(R.id.user_about_me_sheet)
    TextView userAbout;
    @BindView(R.id.user_interests_l_layout_sheet)
    LinearLayout interestsLayout;

    @OnClick(R.id.add_friend_btn_sheet)
    public void onAddFriendClicked(View view) {
        onBtnClickerListener.onAddFriendClickListener(user.getmUid(), user.getmImageUri(),user.getmName());
    }

    private OnButtonClicker onBtnClickerListener;

    public interface OnButtonClicker {
        void onAddFriendClickListener(String uId, String imageUri,String name);
    }

    public static final String TAG = UsersProfileBottomSheetFragment.class.getSimpleName();
    private User user;

    public UsersProfileBottomSheetFragment(User user, Context context) {
        this.user = user;
        onBtnClickerListener = (OnButtonClicker) context;
    }

    public static UsersProfileBottomSheetFragment getInstance(User user, Context context) {
        return new UsersProfileBottomSheetFragment(user, context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile, container, false);
        ButterKnife.bind(this, view);
        Picasso.get().load(user.getmImageUri()).into(myImage);
        userName.setText(user.getmName());
        userAbout.setText("".matches(user.getmAboutMe()) ?
                getContext()
                        .getResources()
                        .getString(R.string.no_details) : user.getmAboutMe());
        interestsLayout.removeAllViews();
        if (user.getmInterests() != null)
            for (String inter : user.getmInterests()) {
                TextView textView = new TextView(getContext());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5, 5, 5, 5);
                textView.setLayoutParams(layoutParams);
                textView.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                textView.setTextColor(getContext().getResources().getColor(android.R.color.white));
                textView.setText(inter);
                interestsLayout.addView(textView);
            }
        return view;
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

    public void setUser(User user) {
        this.user = user;
    }
}
