package com.brtbeacon.sdk.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconConfig;
import com.brtbeacon.sdk.BRTBeaconConnection;
import com.brtbeacon.sdk.BRTBeaconConnectionV2;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTThrowable;
import com.brtbeacon.sdk.Utils;
import com.brtbeacon.sdk.callback.BRTBeaconConnectionListener;
import com.brtbeacon.sdk.callback.BRTBeaconManagerListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceAdjustActivity extends Activity implements OnClickListener {
	
	public final static String KEY_BEACON_OBJ = "key_beacon_obj";
	public final static int ADJUST_MAX_COUNT = 20;
	
	private BRTBeacon mBeacon;
	private BRTBeaconManager beaconManager;
	private BRTBeaconConnectionV2 conn;
	
	private TextView tvName;
	private TextView tvAddr;
	private TextView tvRssi;
	
	private ProgressDialog dialog = null;
	
	private boolean adjustFlag = false;
	private LinkedList<Integer> adjustRssiList = new LinkedList<Integer>();
	private int adjustRssi = 0;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_adjust);
		
		mBeacon = getIntent().getParcelableExtra(KEY_BEACON_OBJ);
		if(mBeacon == null) {
			Toast.makeText(this, "请选择Beacon设备!", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		initView();
		beaconManager = BRTBeaconManager.getInstance(this);
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
	
	private void startAdjust() {
		adjustRssiList.clear();
		adjustFlag = true;
	}
	
	private void stopAdjust() {
		adjustFlag = false;
	}
	
	private void calcAdjust() {
		Collections.sort(adjustRssiList);
		adjustRssiList.removeFirst();
		adjustRssiList.removeLast();
		adjustRssi = 0;
		int rssiSum = 0;
		for(Integer rssi: adjustRssiList) {
			rssiSum += rssi;
		}
		adjustRssi = rssiSum / adjustRssiList.size();
	}
	
	private void updateAdjust() {
		if(adjustRssi > -20 || adjustRssi < -100) {
			showToast("距离校准无效,请重试!");
			closeProgressDialog();
			return;
		}
		updateProgressDialog("正在更新测量功率值为:" + adjustRssi + ",请等待...");
		connect(null);
	}
	
	private void connect(BRTBeaconConnectionListener listener) {
		disconnect();
		if(listener == null) {
			listener = createListener();
		}
		conn = new BRTBeaconConnectionV2(this, null, mBeacon, listener);
		conn.connect();
	}
	
	private void disconnect() {
		if(conn != null) {
			conn.disconnect();
			conn = null;
		}
	}
	
	private BRTBeaconConnectionListener createListener() {
		
		return new BRTBeaconConnectionListener() {
			
			@Override
			public void onError(BRTThrowable arg0) {
				
			}
			
			@Override
			public void onConnectedState(int newState, int status) {
				if(newState == BRTBeaconConnection.CONNECTED) {
					// 连接成功,开始写入Beacon配置参数;
					BRTBeaconConfig config = new BRTBeaconConfig();
					config.setMeasuredPower(adjustRssi);
					conn.writeBeacon(config);
				} else {
					// 此处为设备连接中断,状态为BRTBeaconConnection.DISCONNECTED
					disconnect();
					closeProgressDialog();
					Toast.makeText(DeviceAdjustActivity.this, BRTThrowable.getCodeMsg(status), Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onCharacteristicWrite(String arg0, int arg1, byte[] arg2) {
				
			}
			
			@Override
			public void onCharacteristicRead(String arg0, int arg1, byte[] arg2) {
				
			}
			
			@Override
			public void onCharacteristicChanged(String arg0, int arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onBeaconWrite(BRTBeacon beacon, int status) {
				closeProgressDialog();
				disconnect();
				Toast.makeText(DeviceAdjustActivity.this, "距离校验成功!", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onBeaconRead(BRTBeacon beacon) {}
		};
	}
	
	private void showProgressDialog(String title, String msg) {
		dialog = new ProgressDialog(this);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setOnCancelListener(new OnCancelListener() {	
			@Override
			public void onCancel(DialogInterface arg0) {
				stopAdjust();
				disconnect();
				startScan();
			}
		});
		dialog.show();
	}
	
	private void updateProgressDialog(String message) {
		if(dialog != null) {
			dialog.setMessage(message);
		}
	}
	
	private void closeProgressDialog() {
		if(dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
	
	private void initView() {
		tvName = (TextView) findViewById(R.id.tv_name);
		tvAddr = (TextView) findViewById(R.id.tv_addr);
		tvRssi = (TextView) findViewById(R.id.tv_rssi);
		
		tvName.setText(mBeacon.getName());
		tvAddr.setText(mBeacon.getMacAddress());
		tvRssi.setText(String.format("%d(%.2fm)", mBeacon.getRssi(), Utils.computeAccuracy(mBeacon)));
		
		findViewById(R.id.btn_start).setOnClickListener(this);
	}
	
	private BRTBeaconManagerListener scanListener = new BRTBeaconManagerListener() {
		
		@Override
		public void onUpdateBeacon(final ArrayList<BRTBeacon> beacons) {
			
			for(final BRTBeacon beacon: beacons) {
				if(mBeacon.getMacAddress().equals(beacon.getMacAddress())) {
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvName.setText(beacon.getName());
							tvRssi.setText(String.format("%d(%.2fm)", beacon.getRssi(), Utils.computeAccuracy(beacon)));
							
							if(adjustFlag) {
								int rssi = beacon.getRssi();
								if(rssi <= -20 && rssi >= -100) {
									adjustRssiList.add(rssi);
									updateProgressDialog("校准进度: " + adjustRssiList.size() * 100 / ADJUST_MAX_COUNT + "%");
								}
								
								if(adjustRssiList.size() >= ADJUST_MAX_COUNT) {
									stopAdjust();
									calcAdjust();
									updateAdjust();
								}
							}
							
						}
					});
					return;
				}
			}
			
			
			
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
	
	public static void startActivity(Context context, BRTBeacon beacon) {
		Intent intent = new Intent(context, DeviceAdjustActivity.class);
		intent.putExtra(KEY_BEACON_OBJ, beacon);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_start: {
				showProgressDialog("距离校准", "正在进行距离校准...");
				startScan();
				startAdjust();
				break;
			}
		}
		
	}
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
}
