package com.brtbeacon.sdk.demo.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.demo.R;

public class BeaconViewHolder extends RecyclerView.ViewHolder {

    private BRTBeacon beacon;

    TextView tvRssi;
    TextView tvName;
    TextView tvAddr;
    TextView tvMajor;
    TextView tvMinor;
    TextView tvUuid;

    public BeaconViewHolder(View itemView) {
        super(itemView);
        tvRssi = itemView.findViewById(R.id.device_rssi);
        tvName = itemView.findViewById(R.id.device_name);
        tvAddr = itemView.findViewById(R.id.device_address);
        tvMajor = itemView.findViewById(R.id.tv_major);
        tvMinor = itemView.findViewById(R.id.tv_minor);
        tvUuid = itemView.findViewById(R.id.tv_uuid);
    }

    public void bind(BRTBeacon beacon) {
        this.beacon = beacon;
        updateView();
    }

    private void updateView() {
        if (beacon == null)
            return;
        tvRssi.setText(String.valueOf(beacon.getRssi()));
        tvName.setText(String.valueOf(beacon.getName()));
        tvAddr.setText(beacon.getMacAddress());
        tvMajor.setText(String.valueOf(beacon.getMajor()));
        tvMinor.setText(String.valueOf(beacon.getMinor()));
        tvUuid.setText(beacon.getUuid());
    }





}
