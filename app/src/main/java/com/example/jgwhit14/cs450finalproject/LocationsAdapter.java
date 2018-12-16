package com.example.jgwhit14.cs450finalproject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private List<MyLocationsObject> mDataSet;
    private String mId;
    private Activity activity;
    /**
     * Called when a view has been clicked.
     *
     * @param myLocations
     * @param dataSet Message list
     * @param id      Device id
     */
    LocationsAdapter(Activity myLocations, List<MyLocationsObject> dataSet, String id) {
        activity = myLocations;
        mDataSet = dataSet;

        mId = id;
    }

    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);


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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Location location = mDataSet.get(position).myLocation;
        final String username = mDataSet.get(position).username;
        String address = mDataSet.get(position).address;
        String title = mDataSet.get(position).title;
        String date = mDataSet.get(position).date;
        String time = mDataSet.get(position).time;

        holder.tvNickname.setText(title);
        holder.tvAddress.setText(address);
        holder.tvDate.setText("Date Saved: "+date+ " Time: "+time);


        //holder.mTextView = (TextView) itemView.findViewById(R.id.tvMessage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity,SelectLocation.class);
                intent.putExtra("location",location);
                intent.putExtra("id",(mDataSet.size()-position-1));
                intent.putExtra("Username",username);

                activity.startActivity(intent);
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
        TextView tvNickname,tvAddress,tvDate;
        RelativeLayout cardView;
        Location currentLocation;

        ViewHolder(View v) {
            super(v);
            tvNickname = (TextView) v.findViewById(R.id.textViewNickname);
            tvAddress = (TextView) v.findViewById(R.id.textViewAddress);
            tvDate = (TextView) v.findViewById(R.id.textViewDate);
            cardView =  v.findViewById(R.id.card_view);


        }
    }
}
