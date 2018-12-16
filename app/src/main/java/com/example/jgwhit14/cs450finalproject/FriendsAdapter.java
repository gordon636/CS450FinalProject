package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private List<FriendObject> mDataSet;
    private String mId;
    private Friends activity;
    private String username;
    private RelativeLayout mLayout;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    /**
     * Called when a view has been clicked.
     *  @param dataSet Message list
     * @param id      Device id
     * @param
     */
    FriendsAdapter(ArrayList<FriendObject> dataSet, String id, Friends activity) {
        mDataSet = dataSet;
        mId = id;
        this.activity = activity;
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_start, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getId().equals(mId)) {
            return CHAT_END;
        }

        return CHAT_START;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String name = mDataSet.get(position).username;
        String approved = mDataSet.get(position).approved;

        holder.mTextViewUsername.setText(name);
        if(approved.equals("true")){
            holder.mTextViewApproved.setText("Connected!");
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    username = holder.mTextViewUsername.getText().toString();

                    //view friends location
                    Intent intent = new Intent(activity, MyFriendsLocations.class);
                    activity.startActivity(intent);
                    editor.putString("clicked_username", username).apply();
                }
            });

        }else {
            holder.mTextViewApproved.setText("Not Connected!");
            mLayout.setBackgroundColor(Color.GRAY);
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    username = holder.mTextViewUsername.getText().toString();

                    Toast.makeText(activity,"Sorry! "+username+" has not added you back!", Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Inner Class for a recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView mTextViewUsername, mTextViewApproved;


        ViewHolder(View v) {
            super(v);
            pref = activity.getSharedPreferences("Profile",0);
            editor = pref.edit();
            mTextViewApproved = v.findViewById(R.id.textViewStatus);
            mTextViewUsername = v.findViewById(R.id.textViewUsername);

            mLayout = v.findViewById(R.id.itemLayout);

        }
    }
}
