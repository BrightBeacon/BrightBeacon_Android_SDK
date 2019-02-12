package com.brtbeacon.sdk.demo;

import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.IBle;
import com.brtbeacon.sdk.utils.L;
import android.app.Application;

public class BleApplication extends Application {

    private BRTBeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // 开启log打印
        L.enableDebugLogging(true);
        // 单例
        beaconManager = BRTBeaconManager.getInstance(this);
        // 注册应用 APPKEY申请地址 http://brtbeacon.com/main/index.shtml
        beaconManager.registerApp("00000000000000000000000000000000");
        // 开启Beacon扫描服务
        //beaconManager.startService();

    }

    public void stopService() {
        //beaconManager.stopService();
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
     * @return BRTBeaconManager
     */
    public BRTBeaconManager getBRTBeaconManager() {
        return beaconManager;
    }

}
