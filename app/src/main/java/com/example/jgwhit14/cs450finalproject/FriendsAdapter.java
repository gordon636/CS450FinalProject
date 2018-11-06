package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private String email;

    /**
     * Called when a view has been clicked.
     *  @param dataSet Message list
     * @param id      Device id
     * @param
     */
    FriendsAdapter(List<FriendObject> dataSet, String id, Friends activity) {
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
        String email = mDataSet.get(position).email;
        ArrayList locations = mDataSet.get(position).locations;


        holder.mTextViewUsername.setText(name);
        holder.mTextViewEmail.setText("Email: "+email);


    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Inner Class for a recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewEmail,mTextViewUsername;

        ViewHolder(View v) {
            super(v);
            pref = activity.getSharedPreferences("Profile",0);
            editor = pref.edit();
            mTextViewEmail = (TextView) itemView.findViewById(R.id.mTextViewEmail);
            mTextViewUsername = (TextView) itemView.findViewById(R.id.mTextViewUsername);

            mLayout = (RelativeLayout) v.findViewById(R.id.itemLayout);



            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    username = mTextViewUsername.getText().toString();
                    email = mTextViewEmail.getText().toString();

              //      username = mDataSet.get(position).username;

                    //view friends location
                    Intent intent = new Intent(activity,MyFriendsLocations.class);
                    activity.startActivity(intent);
                    editor.putString("clicked_username",username).apply();
                    editor.putString("clicked_email",email).apply();
                }
            });

        }
    }
}
