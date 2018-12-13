package com.example.jgwhit14.cs450finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {
    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;
    private Button Accept;
    private Button Reject;

    private FirebaseDatabase database;
    private String loggedInUser;
    private SharedPreferences pref;

    private List<FriendRequestObject> mDataSet;
    private String mId;
    private FriendRequests activity;
    private String username;
    private RelativeLayout mLayout;
    private SharedPreferences.Editor editor;

    /**
     * Called when a view has been clicked.
     *  @param dataSet Message list
     * @param id      Device id
     * @param
     */
    FriendRequestsAdapter(ArrayList<FriendRequestObject> dataSet, String id, FriendRequests activity) {
        mDataSet = dataSet;
        mId = id;
        this.activity = activity;

    }

    @Override
    public FriendRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_start_requests, parent, false);
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

        holder.mTextViewUsername.setText(name);


        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = holder.mTextViewUsername.getText().toString();

                //view friends location
                Intent intent = new Intent(activity, Friends.class);
                activity.startActivity(intent);
            }
        });

        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = holder.mTextViewUsername.getText().toString();
                database  = FirebaseDatabase.getInstance();
                loggedInUser = pref.getString("Username","none");

                final DatabaseReference ref = database.getReference("users");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot user = dataSnapshot.child(loggedInUser);
                        Iterable<DataSnapshot> friendRequests = user.child("friendRequests").getChildren();
                        for(DataSnapshot request:friendRequests){
                            System.out.println("REQUEST " + request);
                            Object requestUsername = request.getValue();
                            if(requestUsername.equals(username)){
                                request.getRef().removeValue();
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Inner Class for a recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewUsername;



        ViewHolder(View v) {
            super(v);
            pref = activity.getSharedPreferences("Profile",0);
            editor = pref.edit();
            mTextViewUsername = v.findViewById(R.id.mTextViewUsername);

            mLayout = v.findViewById(R.id.itemLayout);
            Accept = v.findViewById(R.id.acceptRequest);
            Reject = v.findViewById(R.id.rejectRequest);
        }
    }

}

