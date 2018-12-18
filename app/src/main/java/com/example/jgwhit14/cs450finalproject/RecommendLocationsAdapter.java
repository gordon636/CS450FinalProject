package com.example.jgwhit14.cs450finalproject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

class RecommendLocationsAdapter extends RecyclerView.Adapter<RecommendLocationsAdapter.ViewHolder> {
    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private List<String> mDataSet;
    private String mId;
    private Activity activity;
    /**
     * Called when a view has been clicked.
     *
     * @param myLocations
     * @param dataSet Message list
     * @param id      Device id
     */
    RecommendLocationsAdapter(Activity myLocations, List<String> dataSet, String id) {
        activity = myLocations;
        mDataSet = dataSet;
        System.out.println("Attempting DATA: "+dataSet);
        mId = id;
    }

    @Override
    public RecommendLocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {


        return CHAT_START;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String data = mDataSet.get(position);

        System.out.println("Attaching DATA: "+data);
        String []dataArray = data.split("myFriendSPLIT");

        final String friend = dataArray[0];

        final String [] locationData = dataArray[1].split("mySPLIT");

        final Location location = new Location("");
        location.setLatitude(Double.valueOf(locationData[0]));
        location.setLongitude(Double.valueOf(locationData[1]));

        String address = String.valueOf(locationData[3]);
        String title = String.valueOf(locationData[2]);

        holder.tvNickname.setText(title);
        holder.tvAddress.setText(address);
        holder.tvDate.setText("Recommended by: "+friend);


        //holder.mTextView = (TextView) itemView.findViewById(R.id.tvMessage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity,SelectLocation.class);
                intent.putExtra("location",location);
                intent.putExtra("id",(mDataSet.size()-position-1));
                intent.putExtra("Username",friend);

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
