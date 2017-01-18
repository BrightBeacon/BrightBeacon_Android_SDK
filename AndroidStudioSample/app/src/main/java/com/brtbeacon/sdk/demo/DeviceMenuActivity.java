package com.brtbeacon.sdk.demo;

import com.brtbeacon.sdk.BRTBeacon;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class DeviceMenuActivity extends Activity {
	
	public final static String KEY_BEACON_OBJ = "key_beacon_obj";
	
	private BRTBeacon beacon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_menu);
		
		beacon = getIntent().getParcelableExtra(KEY_BEACON_OBJ);
		if(beacon == null) {
			Toast.makeText(this, "请选择Beacon后重试!", Toast.LENGTH_SHORT).show();
			finish();
		}
		
	}
	
	public static void startActivity(Context context, BRTBeacon beacon) {
		Intent intent = new Intent(context, DeviceMenuActivity.class);
		intent.putExtra(KEY_BEACON_OBJ, beacon);
		context.startActivity(intent);
	}
	
}
