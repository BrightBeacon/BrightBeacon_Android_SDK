package com.brtbeacon.sdk.demo;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTMonitor;
import com.brtbeacon.sdk.BRTRegion;
import com.brtbeacon.sdk.callback.BRTBeaconMonitorListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DeviceMonitorActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	
	public final static String KEY_BEACON_OBJ = "key_beacon_obj";
	
	private BRTBeacon mBeacon;
	private BRTBeaconManager beaconManager;
	private BRTMonitor monitor;
	
	private CheckBox cbox_notify_inside;
	private CheckBox cbox_notify_outside;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_monitor);
		
		mBeacon = getIntent().getParcelableExtra(KEY_BEACON_OBJ);
		if(mBeacon == null) {
			Toast.makeText(this, "请选择Beacon设备!", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		
		beaconManager = BRTBeaconManager.getInstance(this);
		
		cbox_notify_inside = (CheckBox)findViewById(R.id.cbox_notify_inside);
		cbox_notify_outside = (CheckBox)findViewById(R.id.cbox_notify_outside);
		
		cbox_notify_inside.setOnCheckedChangeListener(this);
		cbox_notify_outside.setOnCheckedChangeListener(this);
		findViewById(R.id.btn_stop_all).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
			case R.id.btn_stop_all: {
				beaconManager.removeAllMonitor();
				break;
			}

		default:
			break;
		}
		
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		beaconManager.removeMonitor(monitor);
		if(cbox_notify_inside.isChecked() || cbox_notify_outside.isChecked()) {
			monitor = new BRTMonitor(mBeacon.getMacAddress(), null, null, null, true, true);
			beaconManager.addMonitor(monitor);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		beaconManager.setBeaconMonitorListener(monitorListener);
		beaconManager.startMonitoring();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		beaconManager.stopMonitoring();
		beaconManager.setBeaconMonitorListener(null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		beaconManager.removeAllMonitor();
	}
	
	private BRTBeaconMonitorListener monitorListener = new BRTBeaconMonitorListener() {		
		@Override
		public void onRegion(BRTBeacon beacon, BRTRegion region, int state) {
			String toastMsg;
			if(state == 0) {
				toastMsg = "离开Beacon所在区域!";
			} else {
				toastMsg = "进入Beacon所在区域!";
			}
			Toast.makeText(DeviceMonitorActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
		}
	};
	
	public static void startActivity(Context context, BRTBeacon beacon) {
		Intent intent = new Intent(context, DeviceMonitorActivity.class);
		intent.putExtra(KEY_BEACON_OBJ, beacon);
		context.startActivity(intent);
	}

}
