package com.brtbeacon.sdk.demo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.demo.R;

import java.util.ArrayList;
import java.util.List;

public class BeaconViewAdapter extends RecyclerView.Adapter<BeaconViewHolder> {

    private List<BRTBeacon> beaconList = new ArrayList<>();

    @NonNull
    @Override
    public BeaconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_device_info, parent, false);
        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconViewHolder holder, int position) {
        BRTBeacon beacon = beaconList.get(position);
        holder.bind(beacon);
    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }

    public void replaceAll(List<BRTBeacon> beacons) {
        beaconList.clear();
        if (beacons != null) {
            beaconList.addAll(beacons);
        }
        notifyDataSetChanged();
    }

}
