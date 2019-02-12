package com.brtbeacon.sdk.demo;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTThrowable;
import com.brtbeacon.sdk.callback.BRTBeaconManagerListener;
import com.brtbeacon.sdk.demo.adapter.BeaconViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeviceScanActivityV2 extends AppCompatActivity implements View.OnClickListener {

	private RecyclerView beaconListView = null;
	private BeaconViewAdapter beaconViewAdapter = null;
	private BRTBeaconManager beaconManager = null;
	private TextView tvIntro = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_scan_v2);
		tvIntro = findViewById(R.id.tv_intro);

		beaconListView = findViewById(R.id.listView);
		findViewById(R.id.btn_refresh).setOnClickListener(this);
		checkBluetoothValid();

		beaconManager = BRTBeaconManager.getInstance(this);
		beaconManager.setPowerMode(BRTBeaconManager.POWER_MODE_LOW_POWER);

		beaconViewAdapter = new BeaconViewAdapter();
		beaconListView.setAdapter(beaconViewAdapter);
		beaconListView.setLayoutManager(new LinearLayoutManager(this));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startScan();
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
		stopScan();
	}

	private void startScan() {
		beaconManager.setBRTBeaconManagerListener(scanListener);
		beaconManager.startRanging();
	}

	private void stopScan() {
		beaconManager.stopRanging();
		beaconManager.setBRTBeaconManagerListener(null);
	}
	
	private void checkBluetoothValid() {
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter == null) {
			AlertDialog dialog = new AlertDialog.Builder(this).setTitle("错误").setMessage("你的设备不具备蓝牙功能!").create();
			dialog.show();
			return;
		}
		
		if(!adapter.isEnabled()) {
			AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
					.setMessage("蓝牙设备未打开,请开启此功能后重试!")
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
				            startActivityForResult(mIntent, 1);
						}
					})
					.create();
			dialog.show();
		}
	}

	private BRTBeaconManagerListener scanListener = new BRTBeaconManagerListener() {
		
		@Override
		public void onUpdateBeacon(final ArrayList<BRTBeacon> arg0) {
			beaconViewAdapter.replaceAll(arg0);
			tvIntro.setText("周边设备数量：" + beaconViewAdapter.getItemCount());
		}
		
		@Override
		public void onNewBeacon(BRTBeacon arg0) {
			
		}
		
		@Override
		public void onGoneBeacon(BRTBeacon arg0) {
			
		}
		
		@Override
		public void onError(BRTThrowable arg0) {
			
		}
	};
	
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, DeviceScanActivityV2.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_refresh: {
				stopScan();
				beaconViewAdapter.replaceAll(null);
				tvIntro.setText("");
				startScan();
				break;
			}
		}
			
	}

}
