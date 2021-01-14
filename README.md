
## BrightBeacon SDK for Android 集成指南
---
#### 1、将BRTSDK添加到工程libs文件夹；


SDK下载：[https://github.com/BrightBeacon/BrightBeacon_Android_SDK](https://github.com/BrightBeacon/BrightBeacon_Android_SDK)
	
#### 2、配置AndroidManifest.xml文件

	
 	<!-- BRTSDK需要的权限. -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <!-- BRTSDK服务. -->
    <service
        android:name="com.brtbeacon.sdk.BleService"
        android:enabled="true" />
        
#### 3、初始化BRTSDK


	public class BleApplication extends Application {

		private BRTBeaconManager beaconManager;

		@Override
		public void onCreate() {
			super.onCreate();
			// 开启log打印
			L.enableDebugLogging(true);
			//获取单例
			beaconManager = BRTBeaconManager.getInstance(this);
			// 注册应用 APPKEY申请:http://brtbeacon.com/main/index.shtml
			beaconManager.registerApp("00000000000000000000000000000000");
			// 开启Beacon扫描服务
			beaconManager.startService();

		}
		/**
	 	* 创建Beacon连接需要传递此参数
	 	* @return IBle
	 	*/
		public IBle getIBle() {
			return beaconManager.getIBle();
		}

		/**
	 	* 获取Beacon管理对象
	 	* 
	 	* @return BRTBeaconManager
	 	*/
		public BRTBeaconManager getBRTBeaconManager() {
			return beaconManager;
		}

	}
	
#### 4、扫描周边Beacon

你可以通过实现并设置 BRTBeaconManagerListener 接口，来检测Beacon的出现,显示以及更新。样例代码如下：

	BRTBeaconManagerListener beaconManagerListener = new BRTBeaconManagerListener() {

    	@Override
    	public void onUpdateBeacon(ArrayList<BRTBeacon> beacons) {
        	// Beacon信息更新                  
    	}

    	@Override
    	public void onNewBeacon(BRTBeacon beacon) {
        	// 发现一个新的Beacon        
    	}

    	@Override
    	public void onGoneBeacon(BRTBeacon beacon) {
        	// 一个Beacon消失     
    	}
		};
	
	BRTBeaconManager.setBRTBeaconManagerListener(beaconManagerListener);
在这个接口中，Beacon信息更新频率为 1 秒；发现一个新的Beacon后，如果在 8 秒内没有再次扫描到这个设备，则会回调Beacon消失。

	BRTBeaconManager.startRanging();
这个调用开启Beacon的扫描;

	
提示：

回调函数是在非 UI 线程中运行的，请不要在回调函数中进行任何 UI 的相关相关操作，否则会导致 SDK 运行异常。如有需要，请通过 Handler 或者 Activity.runOnUiThread 方式来运行你的代码。

#### 5、监控Beacon进出状态

通常我们进入或离开某些Beacon设备时需要进行一些操作。下面是判断是否进入和离开MacAddress为"000000000001"的Beacon的样例代码：

	BRTBeaconManagerListener beaconManagerListener = new BRTBeaconManagerListener() {

    	@Override
    	public void onUpdateBeacon(ArrayList<BRTBeacon> beacons) {
        	// Beacon信息更新，每秒更新一次，内部设备列表默认有8秒缓存                  
    	}

    	@Override
    	public void onNewBeacon(BRTBeacon beacon) {
		//新设备出现
    	public void onNewBeacon(BRTBeacon beacon) {
        	if (beacon.getMacAddress().equals("000000000001")){
            	// 进入 MacAddress 为"000000000001 的Beacon
        	}
    	}

    	@Override
    	public void onGoneBeacon(BRTBeacon beacon) {
	        //设备信号消失，默认有8s缓存
        	if (beacon.getSerialNumber().equals("000000000001")){
            	// 离开 MacAddress 为"000000000001 的Beacon
        	}      
    	}
		};
	BRTBeaconManager.setBRTBeaconManagerListener(beaconManagerListener);
	
#### 6. 关闭Beacon扫描

如果我们不需要Beacon的扫描任务, 可以进行如下操作:

	BRTBeaconManager.stopRanging();
这个调用停止Beacon的扫描;

	BRTBeaconManager.setBRTBeaconManagerListener(null);
这个调用将扫描回调清空;

#### 7. 连接读取Beacon配置

如果我们要获取和配置Beacon的参数, 第一步需要连接进Beacon;

下面的代码片断需在Activity里面执行

BRTBeaconConnectionV2 conn = new BRTBeaconConnectionV2(this, null, beacon, connectionListener);

BRTBeaconConnectionListener connectionListener = new BRTBeaconConnectionListener() {

	void onConnectedState(int newState, int status) {
	
		if (newState == BRTBeaconConnection.CONNECTED) {
			// 连接设备成功, 可以读取配置参数;
			conn.readBeacon();
		} else {
			// 连接设备失败;
			conn.disconnect();
		}
	}

	void onBeaconRead(BRTBeacon beacon) { 
		// Beacon配置读取完成;
	}

	void onBeaconWrite(BRTBeacon beacon, int status) {
		// Beacon配置更新完成;
	}

	void onError(BRTThrowable throwable) { }

	void onCharacteristicChanged(String uuid, int status, byte[] value) { }

	void onCharacteristicWrite(String uuid, int status, byte[] value) { }

	void onCharacteristicRead(String uuid, int status, byte[] value) { }
}

#### 8. 更新Beacon参数

在连接Beacon成功以后,可以执行下面的代码更新Beacon参数;

	BRTBeaconConfig config = new BRTBeaconConfig();
	config.setName("BrtBeacon");
	config.setMajor(1234);
	config.setMinor(5678);
	conn.writeBeacon(config);

如果设备参数更新完成, connectionListener 对象的 void onBeaconWrite(BRTBeacon beacon, int status)方法会被调用;











