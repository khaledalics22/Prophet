package com.example.prophet.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Entities.User;
import com.example.prophet.Entities.Utils;
import com.example.prophet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {
    private Context mContext;
    private Cursor mCursor;
    private ArrayList<User> mUsers;
    private OnClickedListener mOnItemClickedListener;

    public interface OnClickedListener {
        void onUserClicked(User user);

        void onAddFriendClicked(String uid, String imageUri, String name);
    }

    public UserAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    public UserAdapter(Context mContext, ArrayList<User> mFriends) {
        this.mContext = mContext;
        this.mUsers = mFriends;
        mOnItemClickedListener = (OnClickedListener) mContext;
    }

    public void swapCursor(Cursor mCursor) {
        this.mCursor = mCursor;
        notifyDataSetChanged();
    }

    public void addUser(User friend) {
        if (mUsers == null) mUsers = new ArrayList<>();
        mUsers.add(friend);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mUsers != null) return mUsers.size();
        if (mCursor == null) return 0;
        else return mCursor.getCount();
    }

    public void clear() {
        mUsers = null;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_image)
        CircleImageView userImage;
        @BindView(R.id.user_about)
        TextView userAbout;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.add_friend_btn)
        ImageButton addFriendBtn;

        @OnClick(R.id.add_friend_btn)
        public void onAddFriendClicked(View view) {
            mOnItemClickedListener.onAddFriendClicked(mUsers.get(getAdapterPosition()).getmUid()
                    , mUsers.get(getAdapterPosition()).getmImageUri()
                    , mUsers.get(getAdapterPosition()).getmName());
            Utils.ImageViewAnimatedChange(mContext, addFriendBtn, R.drawable.ic_baseline_done_24);
        }

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickedListener.onUserClicked(mUsers.get(getAdapterPosition()));
                }
            });
        }

        public void bind(int position) {
            if (mUsers != null) {
                User user = mUsers.get(position);
                Picasso.get().load(Uri.parse(user.getmImageUri())).into(userImage);
                userAbout.setText(user.getmAboutMe());
                userName.setText(user.getmName());
            } else if (mCursor != null) {
                mCursor.moveToPosition(position);
            }
        }
    }
}
