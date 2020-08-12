package com.example.prophet.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prophet.Entities.Request;
import com.example.prophet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.Holder> {
    private Context mContext;
    private Cursor mCursor;
    private ArrayList<Request> mRequests;
    private OnClickedListener mOnItemClickedListener;

    public void removeItem(String reqId) {
        for(Request req : mRequests){
            if(req.getmReqId().matches(reqId))
            {
                mRequests.remove(req);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public interface OnClickedListener {
        void onAcceptRequestClicked(Request request);
    }

    public RequestsAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    public RequestsAdapter(Context mContext, ArrayList<Request> mRequests) {
        this.mContext = mContext;
        this.mRequests = mRequests;
        mOnItemClickedListener = (OnClickedListener) mContext;
    }

    public void swapCursor(Cursor mCursor) {
        this.mCursor = mCursor;
        notifyDataSetChanged();
    }

    public void addRequest(Request request) {
        if (mRequests == null) mRequests = new ArrayList<>();
        mRequests.add(request);
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
        if (mRequests != null) return mRequests.size();
        if (mCursor == null) return 0;
        else return mCursor.getCount();
    }

    public void clear() {
        mRequests = null;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_image)
        CircleImageView requestImg;
        @BindView(R.id.user_about)
        TextView requestAbout;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.add_friend_btn)
        Button confirmBtn;
        @BindView(R.id.view)
        View view;

        @OnClick(R.id.add_friend_btn)
        public void onAddFriendClicked(View view) {
            mOnItemClickedListener.onAcceptRequestClicked(mRequests.get(getAdapterPosition()));
        }

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            confirmBtn.setText(R.string.confirm);
            requestAbout.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }

        public void bind(int position) {
            userName.setText(mRequests.get(position).getmSenderName());
            Picasso.get().load(mRequests.get(position).getmSenderImageUri()).into(requestImg);
        }
    }
}
