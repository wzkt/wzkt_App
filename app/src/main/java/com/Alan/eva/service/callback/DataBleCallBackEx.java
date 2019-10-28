package com.Alan.eva.service.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.Alan.eva.config.BLEConfig;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.post.ChildTempPost;
import com.Alan.eva.result.Res;
import com.Alan.eva.service.BleService;
import com.Alan.eva.service.Util;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.activity.HomeActivity;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.security.spec.ECField;

/**
 * Created by CW on 2017/2/23.
 * 蓝牙数据回调实现类
 */
public class DataBleCallBackEx extends BleGattCallback implements IResultHandler {
    private BleService service;

    public DataBleCallBackEx(BleService service) {
        this.service = service;
    }

    private int currState = -1;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//        LogUtil.inf("currState====="+currState);
//        LogUtil.inf("onConnectionStateChange====="+newState);
//        if(newState==0){
//           //service.sendMsg(BLEConfig.BLE_CONNECT_CMD, "连接成功，正在读取体温计信息");//连接成功
//            LogUtil.inf("STATE_CONNECTED====="+newState);
//           // service.scanDeviceService();
//        }
//        if (newState == BluetoothProfile.STATE_CONNECTING) {
//            service.sendMsg(BLEConfig.BLE_CONNECTING, "正在连接体温计，请稍候");//正在连接
//            LogUtil.inf("BLE_CONNECTING====="+newState);
//        } else
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//            service.sendMsg(BLEConfig.BLE_CONNECTED, "连接成功，正在读取体温计信息");//连接成功
//            LogUtil.inf("STATE_CONNECTED====="+newState);
//            service.scanDeviceService();
//        }
//        else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//            LogUtil.inf("STATE_DISCONNECTED====="+newState);
//            if (currState != newState) {
//                currState = newState;
//                service.stopLoop();
//                service.disconnect();
//                service.sendMsg(BLEConfig.BLE_SERVER_DISCONNECTED, "体温计连接已断开");
//            }
//        }
//        super.onConnectionStateChange(gatt, status, newState);
//    }

//    @Override
//    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//        LogUtil.inf("onServicesDiscovered====="+status);
//        if (status == BluetoothGatt.GATT_SUCCESS) {  //读取服务成功
//            if(service.scheduledExecutor==null) {
//                service.sendMsg(BLEConfig.BLE_DEVICE_DISCOVERY, "体温监测中...");
//                service.startLoopTemp();
//            }
//        } else if (status == BluetoothGatt.GATT_FAILURE) {  //服务不能正常使用
//            service.sendMsg(BLEConfig.BLE_SERVICE_NOT_AVAILABLE, "体温计不能正常工作，请尝试连接其他体温计");
//            service.stopLoop();
//        }
        super.onServicesDiscovered(gatt, status);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            String receivedUUID = characteristic.getUuid().toString();

            LogUtil.info("receivedUUID=="+receivedUUID);
            byte[] data = characteristic.getValue();
            LogUtil.info("onCharacteristicRead=data=="+Util.bytesToHexString(data));

            if (TextUtils.equals(receivedUUID, BLEConfig.TEMPERATURE_CHARACTERISTICS)) {  //温度数据
                getActualTemp(data);
            } else if (TextUtils.equals(receivedUUID, BLEConfig.TEMPERATURE_CHARACTERISTICS_POWER)) { //电池电量
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if(data!=null){
                    sendBatteryPower(data);
                }else{

                }
            }else  if (TextUtils.equals(receivedUUID, BLEConfig.TEMPERATURE_CHARACTERISTICS_FORMER)) {  //温度数据
                getEmvActualTemp(data);
            }
        }
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }

    /**
     * 电池电量
     */
    private void sendBatteryPower(byte[] data) {
        LogUtil.info("sendBatteryPower=="+ Util.bytesToHexString(data));
        StringBuilder sb = new StringBuilder(data.length);
        int index;
        for (byte aData : data) {
            index = aData;
            sb.append(index);
        }
        String power = sb.toString();
        service.setBatteryPower(power);
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
           // service.sendMsg(BLEConfig.BLE_TEMP_GET, String.valueOf(temp + "℃"));
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
            String   temp = integralPart;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    service.sendMsg(BLEConfig.BLE_EVM_TEMP_GET_2, String.valueOf(temp + "℃"));
                }}).start();
            LogUtil.info("获取环境温度数据" + temp);
           // submit(temp);
        }
    }
    /**
     * 获取实时温度数据
     */
    private void getActualTemp(byte[] data) {
        LogUtil.info("getActualTemp=="+ Util.bytesToHexString(data));
        //data[1]  = 0x5;

        if (data != null && data.length > 0) {
            String integralPart = String.format("%s", data[0]);
            String decimalPart = String.format("%s", data[1]);
            if(decimalPart.length()==1){
                decimalPart="0"+decimalPart;
            }
            String temp = integralPart + "." + decimalPart;
            service.sendMsg(BLEConfig.BLE_TEMP_GET, String.valueOf(temp + "℃"));
            LogUtil.info("获取温度数据" + temp);
            //submit(temp);

            byte[] Envdata = new byte[2];
            Envdata[0] = data[2];
            Envdata[1] = data[3];
            if (Envdata != null && Envdata.length > 0) {
                String integralPart2 = String.format("%s", Envdata[0]);
                String decimalPart2 = String.format("%s", Envdata[1]);
                if(decimalPart2.length()==1){
                    decimalPart="0"+decimalPart;
                }
//                String temp2 = integralPart2 + "." + decimalPart2;


                String  temp2 = integralPart2;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        service.sendMsg(BLEConfig.BLE_EVM_TEMP_GET_2, String.valueOf(temp2 + "℃"));
                    }
                }).start();
                LogUtil.info("获取环境温度数据" + temp2);
                // submit(temp);
            }
        }
    }

    private ChildTempPost tempPost;
    private final int TEMP_POST = 0x011;
    private int tempCount = 0;

    private void submit(String temp) {
        tempCount += 1;
        if (tempCount >= 6) {
            tempCount = 0;
            String cid = service.getCid();
            if (TextUtils.isEmpty(cid)) {  //如果孩子id为空了，就不要上传了
                LogUtil.info("孩子id为空了，不能上传");
                return;
            }
            if (tempPost == null) {
                tempPost = new ChildTempPost();
                tempPost.code(TEMP_POST);
                tempPost.handler(this);
            }
            tempPost.setCid(cid);
            tempPost.setTemp(temp);
            tempPost.post();
        }
    }

    @Override
    public void handleStart(int code) {
        if (code == TEMP_POST) {
            LogUtil.info("上传温度开始");
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == TEMP_POST) {
            Res res = Tools.json2Bean(result, Res.class);
            LogUtil.info(res.msg());
        }
    }

    @Override
    public void handleFinish(int code) {
        if (code == TEMP_POST) {
            LogUtil.info("上传温度结束了");
        }
    }

    @Override
    public void handleError(int code) {
        if (code == TEMP_POST) {
            LogUtil.info("上传温度结束了");
        }
    }

    @Override
    public void onStartConnect() {
        LogUtil.info("onStartConnect");
        //service.sendMsg(BLEConfig.BLE_CONNECTING, "正在连接体温计，请稍候");//正在连接
    }

    @Override
    public void onConnectFail(BleDevice bleDevice, BleException exception) {
        LogUtil.info("onConnectFail");
        if(HomeActivity.ifwhiletruefailed) {
            try {
                if (HomeActivity.getswithc) {
                    service.sendMsg(BLEConfig.MAC_CONNECT_CMD, HomeActivity.phonemac);
                } else {
                    HomeActivity.ifwhiletruefailed = false;
                    service.sendMsg(BLEConfig.BLE_SCAN_CMD, "");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        LogUtil.inf("onConnectFail exception");
//        service.sendMsg(BLEConfig.BLE_CONNECT_ERROR, "连接体温计异常，请重试");//正在连接
//        service.scheduledExecutor = null;
    }

    @Override
    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        LogUtil.info("onConnectSuccess11"+bleDevice.getMac());
        LogUtil.info("onConnectSuccess11=="+status);
        if (status == BluetoothGatt.GATT_SUCCESS) {  //读取服务成功
            LogUtil.info("ervice.scheduledExecutor==null"+((service.scheduledExecutor==null)));
            if(service.scheduledExecutor==null) {
                LogUtil.info("onConnectSuccess11 service.scheduledExecutor==null");
                service.sendMsg(BLEConfig.BLE_CONNECTED, "体温监测中...");
                service.startLoopTemp();
            }else{
                try {
                    service.scheduledExecutor.shutdown();
                    service.scheduledExecutor = null;

                    service.sendMsg(BLEConfig.BLE_CONNECTED, "体温监测中...");
                    service.startLoopTemp();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        } else if (status == BluetoothGatt.GATT_FAILURE) {  //服务不能正常使用
            service.sendMsg(BLEConfig.BLE_SERVICE_NOT_AVAILABLE, "体温计不能正常工作，请尝试连接其他体温计");
            service.stopLoop();
        }
    }

    @Override
    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
        LogUtil.inf("onDisConnected====="+status);
        LogUtil.inf("isActiveDisConnected====="+isActiveDisConnected);

        if ((status == 8)&&(!isActiveDisConnected)) {
            service.stopLoop();
            service.disconnect();
            service.scheduledExecutor= null;
            service.sendMsg(BLEConfig.BLE_SERVER_DISCONNECTED, "体温计连接已断开");
        }
    }
}
