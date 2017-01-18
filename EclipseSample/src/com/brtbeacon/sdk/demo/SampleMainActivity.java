package com.brtbeacon.sdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SampleMainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_main);
		
		findViewById(R.id.btn_device_scan).setOnClickListener(this);
		findViewById(R.id.btn_device_config).setOnClickListener(this);
		findViewById(R.id.btn_device_notify).setOnClickListener(this);
		findViewById(R.id.btn_device_adjust).setOnClickListener(this);
		findViewById(R.id.btn_device_simluate).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch(view.getId()) {
		
			case R.id.btn_device_scan: {
				DeviceScanActivity.startActivity(this, 0);
				break;
			}
		
			case R.id.btn_device_config: {
				DeviceScanActivity.startActivity(this, DeviceScanActivity.ACTION_EDIT);
				break;
			}
			
			case R.id.btn_device_notify: {
				DeviceScanActivity.startActivity(this, DeviceScanActivity.ACTION_NOTIFY);
				break;
			}
			
			case R.id.btn_device_adjust: {
				DeviceScanActivity.startActivity(this, DeviceScanActivity.ACTION_ADJUST);
				break;
			}
			
			case R.id.btn_device_simluate: {
				DeviceSimulateActivity.startActivity(this);
				break;
			}
		}
		
	}
	
	
	
}
