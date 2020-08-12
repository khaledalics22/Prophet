package com.example.prophet.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Entities.Message;
import com.example.prophet.Entities.SignedUser;
import com.example.prophet.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private Context mContext;
    private ArrayList<Message> mMessages;

    public MessageAdapter(Context mContext, ArrayList<Message> mMessages) {
        this.mContext = mContext;
        this.mMessages = mMessages;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_list_item, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mMessages == null) return 0;
        return mMessages.size();
    }

    public void addMessage(Message msg) {
        if (mMessages == null)
            mMessages = new ArrayList<>();
        mMessages.add(msg);
        notifyDataSetChanged();
    }

    public void clear() {
        mMessages = null;
        notifyDataSetChanged();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_tv)
        TextView msg;
        @BindView(R.id.message_container)
        View cardView;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            RelativeLayout.LayoutParams params;
            if (mMessages.get(position).getmAuthorId().matches(SignedUser.user.getmUid())) {
                params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_START);
                }
            } else {
                params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_END);

                }
            }
            cardView.setLayoutParams(params);
            msg.setText(mMessages.get(position).getmBody());
        }
    }
}
