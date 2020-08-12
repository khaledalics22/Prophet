package com.example.prophet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Entities.Chat;
import com.example.prophet.Entities.SignedUser;
import com.example.prophet.MessagesActivity;
import com.example.prophet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {
    private Context mContext;
    private Cursor mCursor;
    private ArrayList<Chat> mChats;

    public ChatAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    public ChatAdapter(Context mContext, ArrayList<Chat> mFriends) {
        this.mContext = mContext;
        this.mChats = mFriends;
    }

    public void swapCursor(Cursor mCursor) {
        this.mCursor = mCursor;
        notifyDataSetChanged();
    }

    public void addChat(Chat chat) {
        if (mChats == null) mChats = new ArrayList<>();
        mChats.add(chat);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) return mChats.size();
        if (mCursor == null) return 0;
        else return mCursor.getCount();
    }

    public void clear() {
        mChats = null;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.sender_image)
        CircleImageView chatImage;
        @BindView(R.id.sender_msg)
        TextView chatLastMsg;
        @BindView(R.id.sender_msg_date)
        TextView chatLastMsgDate;
        @BindView(R.id.sender_name)
        TextView chatName;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessagesActivity.class);
                    intent.putExtra("uid", mChats.get(getAdapterPosition()).getmChatId());
                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(int position) {
            if (mChats != null) {
                Chat chat = mChats.get(position);
                for (String member : chat.getmMembers()) {
                    if (!member.matches(SignedUser.user.getmUid())) {
                        FirebaseDatabase.getInstance()
                                .getReference().child("users").child(member).child("mImageUri")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String imgUri = snapshot.getValue(String.class);
                                        Picasso.get().load(Uri.parse(imgUri)).into(chatImage);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        FirebaseDatabase.getInstance()
                                .getReference().child("users").child(member).child("mName")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String name = snapshot.getValue(String.class);
                                        chatName.setText(name);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        break;
                    }
                }

                chatLastMsg.setText(chat.getmLastMessage().getmBody());
                chatName.setText(chat.getmChatName());
                //TODO handle showing date in more better way
                long date = Long.parseLong(chat.getmLastMessage().getmDate())/1000;
                long diff = System.currentTimeMillis()/1000 - date;
                if (diff < 60) {
                    chatLastMsgDate.setText(R.string.now);
                } else if (diff / 60.0 < 60) {
                    chatLastMsgDate.setText(mContext.getResources().getString(R.string.min_time, diff / 60));
                } else if (diff / 60.0 / 60.0 < 24) {
                    chatLastMsgDate.setText(mContext.getResources().getString(R.string.hour_time, diff / 60 / 60));
                } else {
                    chatLastMsgDate.setText(mContext.getResources().getString(R.string.days_time, diff / 60 / 60 / 24));
                }
            } else if (mCursor != null) {
                mCursor.moveToPosition(position);
            }
        }
    }
}
