package com.brtbeacon.sdk.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconConfig;
import com.brtbeacon.sdk.BRTBeaconConnection;
import com.brtbeacon.sdk.BRTBeaconConnectionV2;
import com.brtbeacon.sdk.BRTThrowable;
import com.brtbeacon.sdk.callback.BRTBeaconConnectionListener;
import com.brtbeacon.sdk.demo.ParameterListDialogFragment.ParameterListener;
import com.brtbeacon.sdk.utils.Util;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author admin
 * 此Demo为 03XX 05XX系列产品参数配置演示.
 */

public class DeviceConfigActivity extends Activity implements OnClickListener, ParameterListener {
	
	public final static String KEY_BEACON_OBJ = "key_beacon_obj";
	
	public final static String TAG_DIALOG_MS_POWER = "tag_dialog_ms_power";
	public final static String TAG_DIALOG_TX_POWER = "tag_dialog_tx_power";
	public final static String TAG_DIALOG_ADV_INTERVAL = "tag_dialog_adv_interval";
	
	private EditText editName;
	private EditText editMajor;
	private EditText editMinor;
	private EditText editUUID;
	private TextView tvTxPower;
	private TextView tvMsPower;
	private TextView tvAdvInterval;
	
	private BRTBeacon beacon;
	private BRTBeaconConnectionV2 conn;
	private BRTBeaconConfig config;
	
	private ParameterInfo txPowerParam;
	private ParameterInfo msPowerParam;
	private ParameterInfo advIntervalParam;
	
	/**
	 * 设备连接并读取参数成功;
	 */
	private boolean bReadSucc = false;
	private ProgressDialog dialog = null;
	
	private final static int ACTION_NONE = 0;
	private final static int ACTION_READ_BEACON = 1;
	private final static int ACTION_WRITE_BEACON = 2;
	private int actionType = ACTION_READ_BEACON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_config);
		
		beacon = getIntent().getParcelableExtra(KEY_BEACON_OBJ);
		if(beacon == null) {
			Toast.makeText(this, "请选择Beacon设备!", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		initView();
		
		findViewById(R.id.btn_refresh).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		
		//	配置设备前,请读取一次设备配置参数;
		readBeacon();
	}
	
	private void initView() {
		editName = (EditText)findViewById(R.id.edit_name);
		editMajor = (EditText)findViewById(R.id.edit_major);
		editMinor = (EditText)findViewById(R.id.edit_minor);
		editUUID = (EditText)findViewById(R.id.edit_uuid);
		tvTxPower = (TextView)findViewById(R.id.tv_txpower);
		tvMsPower = (TextView)findViewById(R.id.tv_mspower);
		tvAdvInterval = (TextView)findViewById(R.id.tv_adv_interval);
		
		tvTxPower.setOnClickListener(this);
		tvMsPower.setOnClickListener(this);
		tvAdvInterval.setOnClickListener(this);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		disconnect();
	}
	
	private void showProgressDialog(String title, String msg) {
		dialog = new ProgressDialog(this);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				disconnect();
				if(!bReadSucc) {
					finish();
				}
			}
		});
		dialog.show();
	}
	
	private void closeProgressDialog() {
		if(dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	private void connect(BRTBeaconConnectionListener listener) {
		disconnect();
		if(listener == null) {
			listener = createListener();
		}
		conn = new BRTBeaconConnectionV2(this, null, beacon, listener);
		conn.connect();
	}
	
	private void disconnect() {
		if(conn != null) {
			conn.disconnect();
			conn = null;
		}
	}

	//	读取Beacon配置参数;
	private void readBeacon() {
		actionType = ACTION_READ_BEACON;
		showProgressDialog("提示", "正在读取Beacon配置信息, 请等待...");
		if(conn != null) {
			conn.readBeacon();
			return;
		}
		connect(null);
	}
	
	//	保存Beacon配置参数;
	private void writeBeacon() {
		config = new BRTBeaconConfig();
		
		String uuid = editUUID.getText().toString().replace("-", "").trim();
		if(uuid.length() > 0) {
			if(uuid.length() != 32) {
				showToast("UUID长度无效, 请输入32个16进制字符.(当前长度:" + uuid.length() + ")");
				return;
			}
			try {
				Hex.decodeHex(uuid.toCharArray());
			} catch(Exception e) {
				showToast("UUID格式无效, 请输入有效的16进制字符.");
				return;
			}
			config.setUuid(uuid);
		}
		
		String major = editMajor.getText().toString().trim();
		if(major.length() > 0) {
			int nMajor = -1;
			try {
				nMajor = Integer.parseInt(major);
			} catch(Exception e) {}
			if(nMajor < 0 || nMajor > 65535) {
				showToast("Major数值无效,请输入 0-65535 的数字");
				return;
			}
			config.setMajor(nMajor);
		}
		
		String minor = editMinor.getText().toString().trim();
		if(minor.length() > 0) {
			int nMinor = -1;
			try {
				nMinor = Integer.parseInt(minor);
			} catch(Exception e) {}
			if(nMinor < 0 || nMinor > 65535) {
				showToast("Minor数值无效,请输入 0-65535 的数字");
				return;
			}
			config.setMinor(nMinor);
		}
		
		String name = editName.getText().toString().trim();
		if(name.length() > 0) {
			int nameLen = name.getBytes().length;
			if(nameLen > 16) {
				showToast("设备名称长度超过限制,需控制在16个字节以内");
				return;
			}
			config.setName(name);
		}
		
		if(txPowerParam != null) {
			config.setTxPower(Util.matchTXValueV3(txPowerParam.number));
		}
		
		if(msPowerParam != null) {
			config.setMeasuredPower(msPowerParam.number);
		}
		
		if(advIntervalParam != null) {
			config.setAdIntervalMillis(advIntervalParam.number);
		}
		
		actionType = ACTION_READ_BEACON;
		showProgressDialog("提示", "正在写入Beacon配置信息,请等待...");
		if(conn != null) {
			conn.writeBeacon(config);
			return;
		}
		connect(null);
	}
	
	private void clearParams() {
		txPowerParam = null;
		msPowerParam = null;
		advIntervalParam = null;
	}
	
	private BRTBeaconConnectionListener createListener() {
		
		return new BRTBeaconConnectionListener() {
			
			@Override
			public void onError(BRTThrowable arg0) {
				
			}
			
			@Override
			public void onConnectedState(int newState, int status) {
				if(newState == BRTBeaconConnection.CONNECTED) {
					if(actionType == ACTION_READ_BEACON) {
						// 连接成功,开始读取Beacon配置参数;
						conn.readBeacon();
					} else if(actionType == ACTION_WRITE_BEACON) {
						// 连接成功,开始写入Beacon配置参数;
						conn.writeBeacon(config);
					} else {
						closeProgressDialog();
					}
					return;
				} else {
					// 此处为设备连接中断,状态为BRTBeaconConnection.DISCONNECTED
					disconnect();
					closeProgressDialog();
					Toast.makeText(DeviceConfigActivity.this, BRTThrowable.getCodeMsg(status), Toast.LENGTH_SHORT).show();
					if(!bReadSucc) {
						finish();
					}
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
				actionType = ACTION_NONE;
				closeProgressDialog();
				clearParams();
				updateUI(beacon);
				Toast.makeText(DeviceConfigActivity.this, "配置参数写入成功!", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onBeaconRead(BRTBeacon beacon) {
				actionType = ACTION_NONE;
				if(!bReadSucc) {
					bReadSucc = true;
				}
				closeProgressDialog();
				clearParams();
				updateUI(beacon);
			}
		};
	}
	
	private void updateUI(BRTBeacon beacon) {
		editName.setText(beacon.getName());
		editUUID.setText(beacon.getUuid());
		editMajor.setText(String.valueOf(beacon.getMajor()));
		editMinor.setText(String.valueOf(beacon.getMinor()));
		tvTxPower.setText(getParamNameByValue(txPowerV3List, Util.matchTXValueV3(beacon.getTxPower()), "0dBm(80米)"));
		tvMsPower.setText(String.valueOf(beacon.getMeasuredPower()));
		tvAdvInterval.setText(getParamNameByValue(advList, beacon.getAdIntervalMillis(), beacon.getAdIntervalMillis() + "ms"));
	}
	
	public static void startActivity(Context context, BRTBeacon beacon) {
		Intent intent = new Intent(context, DeviceConfigActivity.class);
		intent.putExtra(KEY_BEACON_OBJ, beacon);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_refresh: {
				readBeacon();
				break;
			}
			
			case R.id.btn_save: {
				writeBeacon();
				break;
			}
			
			case R.id.tv_txpower: {
				ParameterListDialogFragment fragment = ParameterListDialogFragment.newInstance("发射功率", txPowerV3List.toArray(new ParameterInfo[0]));
				fragment.show(getFragmentManager(), TAG_DIALOG_TX_POWER);
				break;
			}
			
			case R.id.tv_mspower: {
				ParameterListDialogFragment fragment = ParameterListDialogFragment.newInstance("测量功率", msPowerList.toArray(new ParameterInfo[0]));
				fragment.show(getFragmentManager(), TAG_DIALOG_MS_POWER);
				break;
			}
			
			case R.id.tv_adv_interval: {
				ParameterListDialogFragment fragment = ParameterListDialogFragment.newInstance("发射间隔", advList.toArray(new ParameterInfo[0]));
				fragment.show(getFragmentManager(), TAG_DIALOG_ADV_INTERVAL);
				break;
			}
			
		}
	}
	
	public static final List<ParameterInfo> txPowerV3List;
	public static final List<ParameterInfo> msPowerList;
	public static final List<ParameterInfo> advList;
	
	public static String getParamNameByValue(List<ParameterInfo> params, int value, String defaultName) {
		for(ParameterInfo param: params) {
			if(param.number == value) {
				return param.name;
			}
		}
		return defaultName;
	}
	
	static {
		
		txPowerV3List = new ArrayList<ParameterInfo>();
		txPowerV3List.add(new ParameterInfo("-40dBm(1米)", "", 0));
		txPowerV3List.add(new ParameterInfo("-30dBm(5米)", "", 1));
		txPowerV3List.add(new ParameterInfo("-20dBm(10米)", "", 2));
		txPowerV3List.add(new ParameterInfo("-16dBm(15米)", "", 3));
		txPowerV3List.add(new ParameterInfo("-12dBm(20米)", "", 4));
		txPowerV3List.add(new ParameterInfo("-8dBm(30米)", "", 5));
		txPowerV3List.add(new ParameterInfo("-4dBm(50米)", "", 6));
		txPowerV3List.add(new ParameterInfo("0dBm(80米)", "", 7));
		txPowerV3List.add(new ParameterInfo("4dBm(100米)", "", 8));
		
		
		msPowerList = new ArrayList<ParameterInfo>();
		for(int i = -40; i > -100; --i) {
			msPowerList.add(new ParameterInfo(String.valueOf(i), "", i));
		}
		
		advList = new ArrayList<ParameterInfo>();
		advList.add(new ParameterInfo("100ms", "", 100));
		advList.add(new ParameterInfo("150ms", "", 150));
		advList.add(new ParameterInfo("200ms", "", 200));
		advList.add(new ParameterInfo("400ms", "", 400));
		advList.add(new ParameterInfo("550ms", "", 550));
		advList.add(new ParameterInfo("800ms", "", 800));
		advList.add(new ParameterInfo("1000ms(1s)", "", 1000));
		advList.add(new ParameterInfo("2000ms(2s)", "", 2000));
		advList.add(new ParameterInfo("4000ms(4s)", "", 4000));
		advList.add(new ParameterInfo("10000ms(10s)", "", 10000));
	}


	@Override
	public void onParameterSelected(DialogFragment fragment, ParameterInfo param) {
		String tag = fragment.getTag();
		if(TAG_DIALOG_TX_POWER.equals(tag)) {
			txPowerParam = param;
			tvTxPower.setText(param.name);
		} else if(TAG_DIALOG_MS_POWER.equals(tag)) {
			msPowerParam = param;
			tvMsPower.setText(param.name);
		} else if(TAG_DIALOG_ADV_INTERVAL.equals(tag)) {
			advIntervalParam = param;
			tvAdvInterval.setText(param.name);
		}
	}

}
