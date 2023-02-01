package com.example.healthappttt.adapter;//package com.example.healthappttt.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthappttt.Activity.ChatActivity;
import com.example.healthappttt.Activity.MainActivity;
import com.example.healthappttt.Activity.ProfileActivity;
import com.example.healthappttt.Data.User;
import com.example.healthappttt.R;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MainViewHolder> {
    private ArrayList<User> mDataset;


    private Context mContext;
    private User thisUser;
    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public UserAdapter(Context Context, ArrayList<User> myDataset) {
        this.mDataset = myDataset;
        this.mContext = Context;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public UserAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //클릭됐을시 행동 여기에 적으면 돼네.....시벌...
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {

        CardView cardView = holder.cardView;
        TextView Name =  cardView.findViewById(R.id.UNE);
        TextView LocaName = cardView.findViewById(R.id.GT);
        ImageView photoImageVIew = cardView.findViewById(R.id.PRI);
        TextView PreferredTime = cardView.findViewById(R.id.GoodTime);
        TextView ExerciseArea = cardView.findViewById(R.id.EArea);

        User userInfo = mDataset.get(position);
      //  Log.d(TAG, "onBindViewHolder: "+ userInfo.getUserName().toString());
        Name.setText(userInfo.getUserName().toString());
        LocaName.setText(userInfo.getLocationName());
        PreferredTime.setText("11~13");
        ExerciseArea.setText("가슴");

       if(userInfo.getProfileImg() != null){
          Glide.with(mContext).load(userInfo.getProfileImg()).centerCrop().override(500).into(photoImageVIew);
       }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("userId",thisUser.getKey());
                Log.i(ContentValues.TAG,thisUser.getKey());
                intent.putExtra("username",thisUser.getUserName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}