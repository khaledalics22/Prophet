package com.example.prophet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class ProfileBottomSheetFragment extends BottomSheetDialogFragment {
    @BindView(R.id.my_image)
    CircleImageView myImage;
    @BindView(R.id.my_name)
    TextView myName;
    @BindView(R.id.my_about_me)
    TextView myAboutMe;
    @BindView(R.id.my_interests_l_layout)
    LinearLayout interestsLayout;
    @BindView(R.id.my_et_interests)
    EditText myInterestsEdit;
    @BindView(R.id.Interests)
    TextView interView;
    private OnButtonClicker onBtnClickerListener;

    public interface OnButtonClicker {
        void onAddInterestBtnClicked(String inter);

        void onEditAboutMeBtnClicked(String aboutMe);
    }

    private static boolean isEditingAboutMe = false;

    @OnClick(R.id.my_about_me_btn)
    public void editAboutMeClicked(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.about_me);
        View v = new EditText(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        EditText editText = new EditText(getContext());
        editText.setPadding(10, 10, 10, 10);
        editText.setLayoutParams(params);
        dialog.setView(editText).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String update = editText.getText().toString();
                myAboutMe.setText(update);
                onBtnClickerListener.onEditAboutMeBtnClicked(update);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.add_interests_btn)
    public void addInterestClicked(View view) {
        String inter = myInterestsEdit.getText().toString().trim();
        onBtnClickerListener.onAddInterestBtnClicked(inter);// add to firebase
        TextView textView = new TextView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(getContext().getResources().getColor(android.R.color.white));
        textView.setText(inter);
        myInterestsEdit.setText("");
        interestsLayout.addView(textView);
    }


    public static final String TAG = "PROFILE_TAG";
    private User user;

    public ProfileBottomSheetFragment(User user, Context context) {
        this.user = user;
        onBtnClickerListener = (OnButtonClicker) context;
    }

    public static ProfileBottomSheetFragment getInstance(User user, Context context) {
        return new ProfileBottomSheetFragment(user, context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile, container, false);
        ButterKnife.bind(this, view);
        Picasso.get().load(user.getmImageUri()).into(myImage);
        myName.setText(user.getmName());
        myAboutMe.setText("".matches(user.getmAboutMe()) ?
                getContext()
                        .getResources()
                        .getString(R.string.tell_people_about_you) : user.getmAboutMe());
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
