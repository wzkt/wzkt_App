package com.Alan.eva.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.Alan.eva.R;
import com.Alan.eva.config.BLEConfig;
import com.Alan.eva.config.BleEvent;
import com.Alan.eva.config.DownloadConfig;
import com.Alan.eva.http.core.ReqParam;
import com.Alan.eva.service.callback.DataBleCallBackEx;
import com.Alan.eva.service.fastble.comm.ObserverManager;
import com.Alan.eva.service.receiver.BleConnectReceiver;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.ui.activity.HomeActivity;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.Alan.eva.config.BLEConfig.BLE_RELEASE_DEVICE;

/**
 * Created by CW on 2017/2/21.
 * 蓝牙服务
 * //开始顺序是打开蓝牙->查找体温计->连接体温计->搜索服务->读取数据
 * //结束顺序应该是先结束轮询->断开服务->断开蓝牙
 */
public class BleService extends Service {
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetooth;

    private final static int GOHNSON_ID = 1000;
//    public static BluetoothAdapter BluetoothAdapter;

    private BleEvent bleEvent;
    /*蓝牙状态变化接收器*/
    private BleConnectReceiver connectReceiver;
    /*循环请求机*/
    public ScheduledExecutorService scheduledExecutor;
    private final int LOOPER_CODE = 0x00100;
    /**
     * 电池电量，如果有值了，就不要去获取电量信息了
     */
    public static String batteryPower;
    private String cid;
    private DataBleCallBackEx callBack;

  public static   BleDevice evebleDevice;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new Notification();
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0);
        startForeground(0x111, notification);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bleEvent = new BleEvent();
        EventBus.getDefault().register(this);

        IntentFilter bleFilter = new IntentFilter();
        //bleFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
       // bleFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       // bleFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bleFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        connectReceiver = new BleConnectReceiver(this);
        registerReceiver(connectReceiver, bleFilter);
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(60000)
                .setOperateTimeout(60000);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
//        BluetoothAdapter = bluetooth;
        openBle();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        unregisterReceiver(connectReceiver);
        connectReceiver = null;
        stopLoop(); //停止轮询
        stopScan(); //停止扫描
        LogUtil.info("onDestroy");
        disconnect(); //断开连接
        //closeBle(); //关闭蓝牙
        BleManager.getInstance().destroy();
        super.onDestroy();
    }

//    private boolean openflag = true;
    /**
     * 打开蓝牙
     */
    private void openBle() {
        Log.e("hjs","==============openBle========="+(!bluetooth.isEnabled()));
        if (!bluetooth.isEnabled()){
//            openflag =false;
            BleManager.getInstance().enableBluetooth();
            LogUtil.inf("bluetooth.enable");
            //bluetooth.enable();

        }


    }


    private void setScanRule() {
        String[] uuids;
        String str_uuid = "";
        if (TextUtils.isEmpty(str_uuid)) {
            uuids = null;
        } else {
            uuids = str_uuid.split(",");
        }
        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5){
                    serviceUuids[i] = null;
                }else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

        String[] names;
        String str_name = "EVE";
        if (TextUtils.isEmpty(str_name)) {
            names = null;
        } else {
            names = str_name.split(",");
        }

        String mac = "";

        boolean isAutoConnect =false;

        BleManager.getInstance().enableLog(true)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(55000);

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                //.setScanTimeOut(18000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        setScanRule();

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                sendMsg(BLEConfig.BLE_IS_SCANNING, "正在扫描，请提前打开体温计");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                LogUtil.inf("BLe onScanning ====");
                evebleDevice = bleDevice;
                LogUtil.inf("BLe onScanning ===="+bleDevice.getDevice().getName());
                BluetoothDevice device = evebleDevice.getDevice();
                LogUtil.info(""+device.getName()+" | "+device.getAddress());
                LogUtil.inf("checkBluetoothAddress ===="+ BluetoothAdapter.checkBluetoothAddress(device.getAddress()));
                if ( BluetoothAdapter.checkBluetoothAddress(device.getAddress())) {  //mac地址是否符合要求
                    String name = device.getName();

                    LogUtil.inf("name ===="+ name);

                    if (!TextUtils.isEmpty(name) && name.length() > 3) { //名称是否不为空且长度大于3
                        LogUtil.inf("name ===="+ name);
                        if (name.toUpperCase().contains("EVE")) { //体温计是否包含EVE字符
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(BLEConfig.DEVICE_KEY, device);
                            LogUtil.inf("BLE_NEW_DEVICE ====");
                            sendMsg(BLEConfig.BLE_NEW_DEVICE, bundle);
                            //stopScan();
                            LogUtil.inf("                               connect(bleDevice) ====");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                           // connect(bleDevice);
                            byte[] scanrst = bleDevice.getScanRecord();
                            try {

                                if((scanrst[0]==0x0b)&&(scanrst[1]==0x08)){
                                    byte[] Envdata = new byte[4];
                                    Envdata[0] =  scanrst[3];
                                    Envdata[1] = scanrst[4];
                                    Envdata[3] = scanrst[5];
                                    LogUtil.info("onCharacteristicRead=data==" + Util.bytesToHexString(Envdata));
                                    getEmvActualTemp(Envdata);
                                    sendBatteryPower(scanrst[6]);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                LogUtil.inf("onScanFinished ====");
                //sendMsg(BLEConfig.BLE_SCAN_FINISH, "扫描结束,连接中...");
            }
        });
    }

    private void startScan_for() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                LogUtil.inf("BLe onScanning ====");
                evebleDevice = bleDevice;
                LogUtil.inf("BLe onScanning ===="+bleDevice.getDevice().getName());
                BluetoothDevice device = evebleDevice.getDevice();
                LogUtil.info(""+device.getName()+" | "+device.getAddress());
                LogUtil.inf("checkBluetoothAddress ===="+ BluetoothAdapter.checkBluetoothAddress(device.getAddress()));
                if ( BluetoothAdapter.checkBluetoothAddress(device.getAddress())) {  //mac地址是否符合要求
                    String name = device.getName();

                    LogUtil.inf("name ===="+ name);

                    if (!TextUtils.isEmpty(name) && name.length() > 3) { //名称是否不为空且长度大于3
                        LogUtil.inf("name ===="+ name);
                        if (name.toUpperCase().contains("EVE")) { //体温计是否包含EVE字符
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(BLEConfig.DEVICE_KEY, device);
                            LogUtil.inf("BLE_NEW_DEVICE ====");
                            sendMsg(BLEConfig.BLE_NEW_DEVICE, bundle);
                            //stopScan();
                            LogUtil.inf("                               connect(bleDevice) ====");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // connect(bleDevice);
                            byte[] scanrst = bleDevice.getScanRecord();
                            try {

                                if((scanrst[0]==0x0b)&&(scanrst[1]==0x08)){
                                    byte[] Envdata = new byte[4];
                                    Envdata[0] =  scanrst[3];
                                    Envdata[1] = scanrst[4];
                                    Envdata[3] = scanrst[5];
                                    LogUtil.info("onCharacteristicRead=data==" + Util.bytesToHexString(Envdata));
                                    getEmvActualTemp(Envdata);
                                    sendBatteryPower(scanrst[6]);
                                }

//                                try {
//                                    Thread.sleep(30000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                LogUtil.inf("                         30000=");
//                                startScan();

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                LogUtil.inf("onScanFinished ====");
                //sendMsg(BLEConfig.BLE_SCAN_FINISH, "扫描结束,连接中...");
            }
        });
    }
//    String[] uuids = {
//    "0000fff0-0000-1000-8000-00805f9b34fb",
//    "0000fff5-0000-1000-8000-00805f9b34fb",
//   "0000fff6-0000-1000-8000-00805f9b34fb",
//    "0000180f-0000-1000-8000-00805f9b34fb",
//  "00002a19-0000-1000-8000-00805f9b34fb"
//    };

    private void setmacScanRule(String mac) {
        String[] uuids;
        String str_uuid = "";
        if (TextUtils.isEmpty(str_uuid)) {
            uuids = null;
        } else {
            uuids = str_uuid.split(",");
        }
        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5){
                    serviceUuids[i] = null;
                }else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

//        String[] names;
//        String str_name = "";
//        if (TextUtils.isEmpty(str_name)) {
//            names = null;
//        } else {
//            names = str_name.split(",");
//        }

        Log.e("hjs","mac "+mac);

        boolean isAutoConnect =true;

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(18000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }
    private void startmacScan(String mac) {
        setmacScanRule(mac);

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                //sendMsg(BLEConfig.BLE_IS_SCANNING, "正在扫描，请提前打开体温计");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                evebleDevice = bleDevice;
                BluetoothDevice device = evebleDevice.getDevice();
                LogUtil.info(""+device.getName()+" | "+device.getAddress());

                if ((device!=null) && BluetoothAdapter.checkBluetoothAddress(device.getAddress())) {  //mac地址是否符合要求
                    {  //mac地址是否符合要求
                        String name = device.getName();

                        LogUtil.inf("name ===="+ name);

                        if (!TextUtils.isEmpty(name) && name.length() > 3) { //名称是否不为空且长度大于3
                            LogUtil.inf("name ===="+ name);
                            if (name.toUpperCase().contains("EVE")) { //体温计是否包含EVE字符
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(BLEConfig.DEVICE_KEY, device);
                                LogUtil.inf("BLE_NEW_DEVICE ====");
                                sendMsg(BLEConfig.BLE_NEW_DEVICE, bundle);
                                //stopScan();
                                LogUtil.inf("                               connect(bleDevice) ====");
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // connect(bleDevice);
                                byte[] scanrst = bleDevice.getScanRecord();
                                try {

                                    if((scanrst[0]==0x0b)&&(scanrst[1]==0x08)){
                                        byte[] Envdata = new byte[4];
                                        Envdata[0] =  scanrst[3];
                                        Envdata[1] = scanrst[4];
                                        Envdata[3] = scanrst[5];
                                        LogUtil.info("onCharacteristicRead=data==" + Util.bytesToHexString(Envdata));
                                        getEmvActualTemp(Envdata);
                                        sendBatteryPower(scanrst[6]);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                LogUtil.inf("onScanFinished ====");
                //sendMsg(BLEConfig.BLE_SCAN_FINISH, "扫描结束,连接中...");
            }
        });
    }


    /**
     * 关闭蓝牙
     */
    private void closeBle() {
        BleManager.getInstance().disableBluetooth();
       // bluetooth.disable();
    }

    /**
     * 获取主线线程发来的命令
     *
     * @param bleEvent 事件体
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(BleEvent bleEvent) {
        int code = bleEvent.getCode();
        switch (code) {
            case BLEConfig.BLE_OPEN: //打开手机蓝牙
                openBle();
                break;
            case BLEConfig.BLE_CLOSE: //关闭手机蓝牙
                //closeBle();
                break;
            case BLEConfig.BLE_SCAN_CMD:  //扫描体温计
                startLoopTemp();
                startScan();
                break;
//            case BLEConfig.BLE_CONNECT_CMD:  //连接体温计
//                LogUtil.info("(evebleDevice != null )"+(evebleDevice != null ));
//                LogUtil.info("getMac == "+ evebleDevice.getMac());
//
//                synchronized (this) {
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (evebleDevice != null) {
//                        connect(evebleDevice);
//                    } else {
//                        //resetBleStatus("体温计已解除，请重新扫描");
//                    }
//                }
//                break;
            case BLEConfig.MAC_CONNECT_CMD:  //连接体温计
                stopLoop();
                Bundle macExtra = bleEvent.getExtra();
                if (macExtra != null) {
                   String macExtraString = macExtra.getString(BLEConfig.CMD_EXTRA);
                    if (macExtraString != null) {
                        Log.e("hjs","macExtraString="+macExtraString);
                        startmacScan(macExtraString);
                    } else {
                    }
                }
                startLoopTemp();
                LogUtil.info("MAC_CONNECT_CMD");
                break;
//            case BLEConfig.BLE_DISCONNECT_CMD:
//
//               // stopLoop();
//                LogUtil.info("BLE_DISCONNECT_CMD");
//               // disconnect();
//                resetBleStatus("体温计已解除，请重新扫描");
//                break;
//            case BLEConfig.CHILD_ID_CMD:  //  切换孩子id
//                Bundle cidExtra = bleEvent.getExtra();
//                if (cidExtra != null) {
//                    this.cid = cidExtra.getString(BLEConfig.CMD_EXTRA);
//                }
//                break;
//            case BLEConfig.READ_DEVISE_POWER_CMD: //读取电池电量
//                batteryPower = "";
//                LogUtil.info("readPower start");
//                readPower();
//                break;
//            case  BLEConfig.BLE_EVM_TEMP_GET:
//                LogUtil.info("BLE_EVM_TEMP_GET start");
//                //readEvmTemp();
//                break;
            case BLEConfig.STOP_SERVICE_CMD:  //结束连接
                cancelDownload();
                LogUtil.info("STOP_SERVICE_CMD");
                disconnect();
                stopSelf();
                break;
            case DownloadConfig.DOWN_LOAD_START_CMD: //开始下载命令
                Bundle pathExtra = bleEvent.getExtra();
                if (pathExtra != null && pathExtra.containsKey(BLEConfig.CMD_EXTRA)) {
                    String path = pathExtra.getString(BLEConfig.CMD_EXTRA);
                    startDownLoad(path, DownloadConfig.FILE_PATH);
                }
                break;
        }
    }

    private void resetBleStatus(String tips) {
        batteryPower= "";
        stopLoop();
        stopScan();
        LogUtil.info("resetBleStatus");
        disconnect();

        sendMsg(BLE_RELEASE_DEVICE, tips);
        LogUtil.info("体温计解除执行完毕了");
    }

    /**
     * 扫描体温计
     */
//    private void startScan() {
//        if (bluetooth.isDiscovering()) { //正在扫描不能重复扫描
//            bluetooth.cancelDiscovery();
//            LogUtil.info("正在扫描中");
//            return;
//        }
//        boolean isScanning = bluetooth.startLeScan(new BluetoothAdapter.LeScanCallback() {
//           @Override
//             public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//
//
//             }
//
//            }
//        );
//        LogUtil.info("是否开始扫描?" + isScanning);
//    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        BleManager.getInstance().cancelScan();

//        if (bluetooth.isDiscovering()) {
//            bluetooth.cancelDiscovery();
//        }
    }

    /**
     * 尝试连接
     *
     * @param
     */
    private void connect(BleDevice bleDevice) {
       // openBle();
        //stopScan();  //停止扫描
        if(bleDevice!=null &&  (!BleManager.getInstance().isConnected(bleDevice))) {
            LogUtil.inf("callBack===" + (callBack == null));
            if(callBack==null) {
                callBack = new DataBleCallBackEx(this);
            }

            try {
                bluetoothGatt = BleManager.getInstance().connect(bleDevice, callBack);
                //bluetoothGatt = bleDevice.getDevice().connectGatt(this, false, callBack);
                LogUtil.inf("bluetoothGatt==");
            }catch (Exception e){
                e.printStackTrace();
                sendMsg(BLEConfig.BLE_DEVICE_NOT_FOUND, "体温计连接失败，请尝试重启体温计，并重新连接");
            }
            LogUtil.info("连接代码执行完毕");
           // callBack.onConnectSuccess(bleDevice,bluetoothGatt,0);
        }else{
            LogUtil.inf("(!BleManager.getInstance().isConnected(bleDevice))" );
        }
//        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
//            return;
//        }
//        BluetoothDevice device = bluetooth.getRemoteDevice(address);
//        if (device == null) {  //蓝牙体温计无法连接，被其他体温计占用
//            sendMsg(BLEConfig.BLE_DEVICE_NOT_FOUND, "体温计连接失败，请尝试重启体温计，并重新连接");
//            return;
//        }
    }

    /**
     * 尝试连接
     *
     * @param
     */
    private void connectmac(String  bleDevicemac) {


            try {
                BleManager.getInstance().connect(bleDevicemac, callBack);
                BluetoothDevice bluetoothDevice = BleManager.getInstance().getBluetoothAdapter().getRemoteDevice(bleDevicemac);
                BleDevice blce = new BleDevice(bluetoothDevice, 0, null, 0);
                bleDevice =blce;
                bluetoothGatt = bleDevice.getDevice().connectGatt(this, false, callBack);

                LogUtil.inf("bluetoothGatt==");
            }catch (Exception e){
                e.printStackTrace();
                sendMsg(BLEConfig.BLE_DEVICE_NOT_FOUND, "体温计连接失败，请尝试重启体温计，并重新连接");
            }

    }

    /**
     * 断开连接
     */
    public void disconnect() {
        Log.e("hjsbleserver","disconnect");

        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();

            LogUtil.info("bluetoothGatt.close");
        callBack = null;
        }


//        if (BleManager.getInstance().isConnected(evebleDevice)) {
//
//            Log.e("hjsbleserver","isConnected.isConnected"+evebleDevice.getName());
//            BleManager.getInstance().disconnect(evebleDevice);
//        }
        BleManager.getInstance().disconnectAllDevice();


        //BleManager.getInstance().disconnect(evebleDevice);
        //BleManager.getInstance().disconnectAllDevice();
        tempCharacteristic = null;
        //evebleDevice = null; 重新连接

    }

    /**
     * 循环获取温度数据
     */
    public void startLoopTemp() {
        LogUtil.info("  startLoopTemp");
        if (scheduledExecutor == null) {
            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        }else{
            return;
        }

        LogUtil.info("startLoopTemp          startLoopTemp");

        scheduledExecutor.scheduleWithFixedDelay(() ->
                tempHandler.sendEmptyMessage(LOOPER_CODE), 1, 30, TimeUnit.SECONDS);
    }

    ////遍历记录startLoopTemp

    /**
     * 关闭循环遍历数据
     */
    public void stopLoop() {
        LogUtil.info("stopLoop          stopLoop");
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
        scheduledExecutor = null;
    }


    public void scanDeviceService() {
        if (bluetoothGatt != null) {
            bluetoothGatt.discoverServices();
        }
    }

    public void setBatteryPower(String batteryPower) {
        this.batteryPower = batteryPower;
        sendMsg(BLEConfig.BLE_BATTERY_POWER, String.valueOf(this.batteryPower + "%"));
    }

    public String getCid() {
        return cid;
    }

    /**
     * 向页面发消息
     *
     * @param code  code
     * @param extra 消息
     */
    public void sendMsg(int code, String extra) {
        Bundle bundle = new Bundle();
        bundle.putString(BLEConfig.MSG_KEY, extra);
        sendMsg(code, bundle);
    }

    /**
     * 向页面发消息
     *
     * @param code  code
     * @param extra 消息
     */
    public void sendMsg(int code, Bundle extra) {
        bleEvent.setCode(code);
        bleEvent.setExtra(extra);
        EventBus.getDefault().post(bleEvent);
    }




        /***
     * 这是一个内部成员
     */
    private  Handler tempHandler = new Handler(msg -> {
        if (msg.what == LOOPER_CODE) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //readTemp();
                    startScan_for();
                }
            }).start();
        }
        return false;
    });



    private BluetoothGattCharacteristic tempCharacteristic;

//    /**
//     * 读取温度数据
//     */
//    private void readTemp() {
//        LogUtil.info("获取一次温度数据=="+tempCharacteristic);
//        if (tempCharacteristic != null) {
//            bluetoothGatt.readCharacteristic(tempCharacteristic);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            callBack.onCharacteristicRead(bluetoothGatt,tempCharacteristic, BluetoothGatt.GATT_SUCCESS);
//            return;
//        }
//        BleDevice bleDevice = evebleDevice;
//        String name = bleDevice.getName();
//        String mac = bleDevice.getMac();
//        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
//        bluetoothGatt = gatt;
////        List<BluetoothGattService> serviceList =new List<BluetoothGattService>() {
////        };
////        for (BluetoothGattService service : gatt.getServices()) {
////            serviceList.add(service)
////        }
//        //LogUtil.info("二二二次温度数据=="+tempCharacteristic);
//        if(bluetoothGatt==null)return;
//        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
//        if (serviceList == null || serviceList.size() <= 0) {
//            LogUtil.info("服务特征值列表为空了");
//            return;
//        }
//        for (BluetoothGattService gattService : serviceList) { //遍历 体温计的所有特征值
//            String parentUuid = gattService.getUuid().toString();
//
//            if (TextUtils.equals(parentUuid, BLEConfig.TEMPERATURE_SERVICES)) { //体温计包含所需的服务列表  根据特征值查找服务
//                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//
//                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {  //遍历特征值服务中存在的数据存储点
//                    String childUuid = gattCharacteristic.getUuid().toString();
//                    if (TextUtils.equals(childUuid, BLEConfig.TEMPERATURE_CHARACTERISTICS)) { //服务列表中有所需的特征值数据
//                        int charaProp = gattCharacteristic.getProperties();
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                            tempCharacteristic = gattCharacteristic;
//                            bluetoothGatt.readCharacteristic(gattCharacteristic);
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            callBack.onCharacteristicRead(bluetoothGatt,tempCharacteristic, BluetoothGatt.GATT_SUCCESS);
//                        }
//                    }
//                }
//            }
//        }
//    }
    BleDevice bleDevice;
    private void readTemp() {
        LogUtil.info("获取一次温度数据=="+tempCharacteristic);
        if (tempCharacteristic != null) {
            bluetoothGatt.readCharacteristic(tempCharacteristic);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(callBack!=null){
                callBack.onCharacteristicRead(bluetoothGatt,tempCharacteristic, BluetoothGatt.GATT_SUCCESS);
            }else return;
            return;
        }
        bleDevice = evebleDevice;
        String name = bleDevice.getName();
        String mac = bleDevice.getMac();
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        bluetoothGatt = gatt;
        if(bluetoothGatt==null)return;
        BluetoothGattService gattService = bluetoothGatt.getService(UUID.fromString(BLEConfig.TEMPERATURE_SERVICES));
        if (gattService == null) {
            LogUtil.info("服务特征值列表为空了");
            return;
        }
               BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString( BLEConfig.TEMPERATURE_CHARACTERISTICS));
                        int charaProp = gattCharacteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            tempCharacteristic = gattCharacteristic;
                            bluetoothGatt.readCharacteristic(gattCharacteristic);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            callBack.onCharacteristicRead(bluetoothGatt,tempCharacteristic, BluetoothGatt.GATT_SUCCESS);
                        }
        gatt =null;
        gattService = null;
        gattCharacteristic = null;
    }

    /**
     * 读取信息
     */
    private void readEvmTemp() {
         List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
        if (serviceList == null || serviceList.size() <= 0) {
            return;
        }
        for (BluetoothGattService gattService : serviceList) { //遍历 体温计的所有特征值
            String parentUuid = gattService.getUuid().toString();
           // LogUtil.info("parentUuid "+parentUuid);
            if (TextUtils.equals(parentUuid, BLEConfig.TEMPERATURE_SERVICES)) { //体温计包含所需的服务列表  根据特征值查找服务
                LogUtil.info("TEMPERATURE_SERVICES "+parentUuid);
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {  //遍历特征值服务中存在的数据存储点
                    String childUuid = gattCharacteristic.getUuid().toString();
                    if (TextUtils.equals(childUuid, BLEConfig.TEMPERATURE_CHARACTERISTICS_FORMER)) { //服务列表中有所需的特征值数据
                        int charaProp = gattCharacteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            ////tempCharacteristic = gattCharacteristic;
                            bluetoothGatt.readCharacteristic(gattCharacteristic);
                        }
                    }
                }


            }
        }
    }

    /**
     * 读取电量信息
     */
    private void readPower() {
        if (!TextUtils.isEmpty(batteryPower)) {
            sendMsg(BLEConfig.BLE_BATTERY_POWER, String.valueOf(batteryPower + "%"));
            return;
        }

try{

            if(bluetoothGatt==null)return;
        BluetoothGattService gattService2 = bluetoothGatt.getService(UUID.fromString(BLEConfig.TEMPERATURE_SERVICES_POWER));
    if(gattService2==null)return;
        BluetoothGattCharacteristic gattCharacteristic2 =gattService2.getCharacteristic(UUID.fromString(BLEConfig.TEMPERATURE_CHARACTERISTICS_POWER));
//        List<BluetoothGattCharacteristic> gattCharacteristics2 = gattService2.getCharacteristics();
//        for (BluetoothGattCharacteristic gattCharacteristic2 : gattCharacteristics2) {  //遍历特征值服务中存在的数据存储点
            String childUuid2 = gattCharacteristic2.getUuid().toString();
            LogUtil.info("readPower=========== " + childUuid2);
            if (TextUtils.equals(childUuid2, BLEConfig.TEMPERATURE_CHARACTERISTICS_POWER)) {
                int charaProp = gattCharacteristic2.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    bluetoothGatt.readCharacteristic(gattCharacteristic2);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                           String a = gattCharacteristic.getStringValue(0);
//                            LogUtil.info("a==="+a);
                    byte[] data = gattCharacteristic2.getValue();
                    LogUtil.info("data======" + data);
                    if (data == null) {
                        bluetoothGatt.readCharacteristic(gattCharacteristic2);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtil.info("onCharacteristicRead=data==" + Util.bytesToHexString(data));
//                            data = gattCharacteristic.getValue();
//                            LogUtil.info("onCharacteristicRead=data=="+Util.bytesToHexString(data));

                   if(callBack!=null)callBack.onCharacteristicRead(bluetoothGatt, gattCharacteristic2, BluetoothGatt.GATT_SUCCESS);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

            return;
//        BleDevice bleDevice = evebleDevice;
//        String name = bleDevice.getName();
//        String mac = bleDevice.getMac();
//        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
//
//        bluetoothGatt = gatt;

//        List<BluetoothGattService> serviceList = bluetoothGatt.getServices();
//        if (serviceList == null || serviceList.size() <= 0) {
//            return;
//        }
//        for (BluetoothGattService gattService : serviceList) { //遍历 体温计的所有特征值
//            String parentUuid = gattService.getUuid().toString();
//
//            if (TextUtils.equals(parentUuid, BLEConfig.TEMPERATURE_SERVICES_POWER)) { //电量服务
//                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {  //遍历特征值服务中存在的数据存储点
//                    String childUuid = gattCharacteristic.getUuid().toString();
//                    LogUtil.info("readPower "+childUuid);
//                    if (TextUtils.equals(childUuid, BLEConfig.TEMPERATURE_CHARACTERISTICS_POWER)) {
//
//
//                        int charaProp = gattCharacteristic.getProperties();
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                            bluetoothGatt.readCharacteristic(gattCharacteristic);
//
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
////                           String a = gattCharacteristic.getStringValue(0);
////                            LogUtil.info("a==="+a);
//                            byte[] data = gattCharacteristic.getValue();
//                              LogUtil.info("data======"+data);
//                            if(data==null){
//                                bluetoothGatt.readCharacteristic(gattCharacteristic);
//
//                                try {
//                                    Thread.sleep(500);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            LogUtil.info("onCharacteristicRead=data=="+Util.bytesToHexString(data));
////                            data = gattCharacteristic.getValue();
////                            LogUtil.info("onCharacteristicRead=data=="+Util.bytesToHexString(data));
//
//                            callBack.onCharacteristicRead(bluetoothGatt,gattCharacteristic, BluetoothGatt.GATT_SUCCESS);
//                        }
//                    }
//                }
//            }
//        }
    }

    private Callback.Cancelable cancelable;

    /**
     * 开始下载
     *
     * @param url      外网路径
     * @param localUri 本地路径
     */
    private void startDownLoad(String url, String localUri) {
        Callback.ProgressCallback<File> callback = new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                sendMsg(DownloadConfig.DOWN_LOAD_STARTED, "开始下载...");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                if (isDownloading) {
                    String process = "下载:" + (int) (current * 100 / total) + "%";
                    LogUtil.info(process);
                }
            }

            @Override
            public void onSuccess(File result) {
                LogUtil.info("下载完成了");
                String path = result.getPath();
                LogUtil.info("path=" + path);
                sendMsg(DownloadConfig.DOWN_LOAD_SUCCESS, path);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.info("下载失败了");
                sendMsg(DownloadConfig.DOWN_LOAD_FAILED, "下载失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.info("下载被取消了");
            }

            @Override
            public void onFinished() {
                LogUtil.info("下载结束了");
            }
        };
        LogUtil.info("url=" + url);
        ReqParam params = new ReqParam(url);
        params.setAutoResume(true);
        params.setAutoRename(true);
        params.setSaveFilePath(localUri);
        Executor executor = new PriorityExecutor(2);
        params.setExecutor(executor);
        params.setCancelFast(true);
        cancelable = x.http().get(params, callback);
    }

    private void cancelDownload() {
        if (cancelable != null) {
            cancelable.cancel();
        }
    }



    /**
     * 获取实时温度数据
     */
    private void getEmvActualTemp(byte[] data) {
        LogUtil.info("getEmvActualTemp=="+ Util.bytesToHexString(data));
        //data[1]  = 0x5;

        byte[] tempdata = new byte[2];
        tempdata[0] = data[0];
        tempdata[1] = data[1];

        if (tempdata != null && tempdata.length > 0) {
            String integralPart = String.format("%s", tempdata[0]);
            String decimalPart = String.format("%s", tempdata[1]);
            if(decimalPart.length()==1){
                decimalPart="0"+decimalPart;
            }
            String temp = integralPart + "." + decimalPart;
            sendMsg(BLEConfig.BLE_TEMP_GET, String.valueOf(temp + "℃"));
            LogUtil.info("获取温度数据" + temp);
            //submit(temp);
        }
        byte[] Envdata = new byte[2];
        Envdata[0] = data[2];
        Envdata[1] = data[3];
        if (Envdata != null && Envdata.length > 0) {
            String integralPart = String.format("%s", Envdata[0]);
            String decimalPart = String.format("%s", Envdata[1]);
            if(decimalPart.length()==1){
                decimalPart="0"+decimalPart;
            };
            //String temp = integralPart + "." + decimalPart;
            // String   temp = String.valueOf(Integer.getInteger(integralPart));
            String   temp = decimalPart;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    sendMsg(BLEConfig.BLE_EVM_TEMP_GET_2, String.valueOf(temp + "℃"));
                }}).start();
            LogUtil.info("获取环境温度数据" + temp);
            // submit(temp);
        }
    }

    /**
     * 电池电量
     */
    private void sendBatteryPower(byte data) {
        LogUtil.info("sendBatteryPower=="+ (data));
        StringBuilder sb = new StringBuilder(2);
        int index  =data;
        sb.append(index);
        String power = sb.toString();
        setBatteryPower(power);
    }
}
