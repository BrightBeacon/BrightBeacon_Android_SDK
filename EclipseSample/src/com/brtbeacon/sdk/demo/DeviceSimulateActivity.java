package com.brtbeacon.sdk.demo;

import com.brtbeacon.sdk.BRTAdvertiseData;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTThrowable;
import com.brtbeacon.sdk.callback.BRTBeaconAdvertiseListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeviceSimulateActivity extends Activity implements OnClickListener {
	
	private BRTBeaconManager beaconManager;
	
	private EditText editMajor;
	private EditText editMinor;
	private EditText editUUID;
	private Button btnStart;
	
	private boolean mAdvFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_simulate);
		beaconManager = BRTBeaconManager.getInstance(this);
		initView();
	}
	
	private void initView() {
		editMajor = (EditText)findViewById(R.id.edit_major);
		editMinor = (EditText)findViewById(R.id.edit_minor);
		editUUID = (EditText)findViewById(R.id.edit_uuid);
		
		btnStart = (Button) findViewById(R.id.btn_start);
		findViewById(R.id.btn_start).setOnClickListener(this);
	}
	
	private void startAdvertise() {
		
		String uuid = editUUID.getText().toString().replace("-", "").trim();
		int uuidLen = uuid.length();
		if(uuidLen != 32) {
			showToast("UUID长度不对,请输入32个16进制字符");
			return;
		}
		
		String major = editMajor.getText().toString();
		int nMajor = -1;
		try {
			nMajor = Integer.parseInt(major);
		} catch(Exception e) {}
		if(nMajor < 0 || nMajor > 65535) {
			showToast("Major为无效数字, 范围为0-65535");
			return;
		}
		
		String minor = editMinor.getText().toString();
		int nMinor = -1;
		try {
			nMinor = Integer.parseInt(minor);
		} catch(Exception e) {}
		if(nMinor < 0 || nMinor > 65535) {
			showToast("Minor为无效数字, 范围为0-65535");
			return;
		}
	
		BRTAdvertiseData data = new BRTAdvertiseData(uuid, nMajor, nMinor);
		beaconManager.startAdvertising(data, advertiseListener);
	}
	
	private void stopAdvertise() {
		beaconManager.stopAdvertising();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopAdvertise();
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_start: {
				
				if(!mAdvFlag) {
					btnStart.setText("停止");
					btnStart.setEnabled(false);
					startAdvertise();
					
				} else {
					btnStart.setText("开始");
					stopAdvertise();
				}
				
				mAdvFlag = !mAdvFlag;
				
				break;
			}
		}
		
	}
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, DeviceSimulateActivity.class);
		context.startActivity(intent);
	}
	
	private BRTBeaconAdvertiseListener advertiseListener = new BRTBeaconAdvertiseListener() {
		
		@Override
		public void onStartSuccess() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					btnStart.setEnabled(true);
					showToast("广播开启成功!");
				}
			});
		}
		
		@Override
		public void onStartFailure(final BRTThrowable arg0) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					btnStart.setEnabled(true);
					showToast("广播开启出错:" + arg0.getError());
				}
			});
		}
	};
	
}
