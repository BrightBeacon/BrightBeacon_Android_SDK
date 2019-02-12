package com.brtbeacon.sdk.demo;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class SampleMainActivity extends AppCompatActivity implements OnClickListener {

	private static final int BRTMAP_PERMISSION_CODE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_main);
		
		findViewById(R.id.btn_device_scan).setOnClickListener(this);
		findViewById(R.id.btn_device_config).setOnClickListener(this);
		findViewById(R.id.btn_device_notify).setOnClickListener(this);
		findViewById(R.id.btn_device_adjust).setOnClickListener(this);
		findViewById(R.id.btn_device_simluate).setOnClickListener(this);

		checkPermission();
	}

	@Override
	public void onClick(View view) {

		switch(view.getId()) {
		
			case R.id.btn_device_scan: {
				//DeviceScanActivity.startActivity(this, 0);
				DeviceScanActivityV2.startActivity(this);
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

	private void checkPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断当前系统的SDK版本是否大于23
			List<String> permissionNeedRequest = new LinkedList<>();
			for (String permssion: permissionsNeedCheck) {
				if(ActivityCompat.checkSelfPermission(this, permssion) != PackageManager.PERMISSION_GRANTED) {
					permissionNeedRequest.add(permssion);
				}
			}
			if (permissionNeedRequest.isEmpty()) {
				return;
			}

			ActivityCompat.requestPermissions(this, permissionNeedRequest.toArray(new String[0]), BRTMAP_PERMISSION_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			// requestCode即所声明的权限获取码，在requestPermissions时传入
			case BRTMAP_PERMISSION_CODE:
				boolean isAllGrant = true;
				for (int grantResult: grantResults) {
					if (grantResult != PackageManager.PERMISSION_GRANTED) {
						isAllGrant = false;
						break;
					}
				}
				if (!isAllGrant) {
					Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动前往设置开启", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
	}

	private static final List<String> permissionsNeedCheck;
	static {
		permissionsNeedCheck = new LinkedList<>();
		permissionsNeedCheck.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
		permissionsNeedCheck.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
		permissionsNeedCheck.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}
	
	
	
}
