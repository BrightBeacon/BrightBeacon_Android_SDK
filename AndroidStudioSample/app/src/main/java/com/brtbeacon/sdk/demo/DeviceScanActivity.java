package com.brtbeacon.sdk.demo;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTThrowable;
import com.brtbeacon.sdk.callback.BRTBeaconManagerListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeviceScanActivity extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {
		
	public final static String KEY_ACTION_TYPE = "key_action_type";
	//	Beacon参数配置
	public final static int ACTION_EDIT = 1;
	//	Beacon距离校准
	public final static int ACTION_ADJUST = 2;
	//	Beacon通知
	public final static int ACTION_NOTIFY = 3;
	
	private ListView listView;
	
	private List<BRTBeacon> beaconList = new ArrayList<BRTBeacon>();
	private ArrayAdapter<BRTBeacon> beaconAdapter = null;
	private BRTBeaconManager beaconManager = null;
	
	private int clickAction = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_scan);
		
		clickAction = getIntent().getIntExtra(KEY_ACTION_TYPE, 0);
		listView = (ListView)findViewById(R.id.listView);
		
		beaconAdapter = new ArrayAdapter<BRTBeacon>(this, R.layout.item_device_info, android.R.id.text1, beaconList){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView tvRssi = (TextView)view.findViewById(R.id.device_rssi);
				TextView tvName = (TextView)view.findViewById(R.id.device_name);
				TextView tvAddr = (TextView)view.findViewById(R.id.device_address);
				TextView tvMajor = (TextView)view.findViewById(R.id.tv_major);
				TextView tvMinor = (TextView) view.findViewById(R.id.tv_minor);
				TextView tvUuid = (TextView) view.findViewById(R.id.tv_uuid);
				TextView tvUserData = (TextView) view.findViewById(R.id.tv_userdata);
				
				BRTBeacon beacon = getItem(position);
				
				tvRssi.setText(String.valueOf(beacon.getRssi()));
				tvName.setText(String.valueOf(beacon.getName()));
				tvAddr.setText(beacon.getMacAddress());
				tvMajor.setText(String.valueOf(beacon.getMajor()));
				tvMinor.setText(String.valueOf(beacon.getMinor()));
				tvUuid.setText(beacon.getUuid());
				//tvUserData.setText(String.valueOf(Hex.encodeHex(beacon.getUserData())).toUpperCase());
				
				return view;
			}
		};
		
		listView.setAdapter(beaconAdapter);
		listView.setOnItemClickListener(this);
		
		findViewById(R.id.btn_refresh).setOnClickListener(this);
		
		TextView tvIntro = (TextView) findViewById(R.id.tv_intro);
		if(clickAction == ACTION_ADJUST) {
			tvIntro.setText("请点击Beacon设备,进行距离校准!");
		} else if(clickAction == ACTION_EDIT) {
			tvIntro.setText("请点击Beacon设备,进行参数配置!");
		} else if(clickAction == ACTION_NOTIFY){
			tvIntro.setText("请点击Beacon设备,进行通知测试!");
		} else {
			tvIntro.setVisibility(View.GONE);
		}
		
		checkBluetoothValid();

		beaconManager = BRTBeaconManager.getInstance(this);
		beaconManager.setPowerMode(BRTBeaconManager.POWER_MODE_LOW_POWER);
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
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		BRTBeacon beacon = (BRTBeacon)parent.getItemAtPosition(position);
		switch(clickAction) {
			case ACTION_EDIT: {
				DeviceConfigActivity.startActivity(this, beacon);
				break;
			}
			
			case ACTION_ADJUST: {
				DeviceAdjustActivity.startActivity(this, beacon);
				break;
			}
			
			case ACTION_NOTIFY: {
				DeviceMonitorActivity.startActivity(this, beacon);
				break;
			}
			
		}
	}

	private BRTBeaconManagerListener scanListener = new BRTBeaconManagerListener() {
		
		@Override
		public void onUpdateBeacon(final ArrayList<BRTBeacon> arg0) {

			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					beaconList.clear();
					beaconList.addAll(arg0);
					beaconAdapter.notifyDataSetChanged();
					System.out.println(Calendar.getInstance().getTimeInMillis());
				}
			});

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
	
	public static void startActivity(Context context, int clickType) {
		Intent intent = new Intent(context, DeviceScanActivity.class);
		intent.putExtra(KEY_ACTION_TYPE, clickType);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_refresh: {
				stopScan();
				beaconList.clear();
				beaconAdapter.notifyDataSetChanged();
				startScan();
				break;
			}
		}
			
	}

}
