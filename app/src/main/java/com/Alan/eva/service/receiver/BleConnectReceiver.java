package com.Alan.eva.service.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;

import com.Alan.eva.config.BLEConfig;
import com.Alan.eva.service.BleService;
import com.Alan.eva.service.ToastUtil;
import com.Alan.eva.tools.LogUtil;

/**
 * Created by CW on 2017/3/1.
 * 蓝牙连接广播回调
 */
public class BleConnectReceiver extends BroadcastReceiver {

    private BleService service;

    public BleConnectReceiver(BleService service) {
        this.service = service;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.info("action=" + action);
        if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            //正在搜索
            service.sendMsg(BLEConfig.BLE_IS_SCANNING, "正在扫描，请提前打开体温计");
        } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            //搜索结束
            service.sendMsg(BLEConfig.BLE_SCAN_FINISH, "扫描结束");
        } else if (false &&TextUtils.equals(action, BluetoothDevice.ACTION_FOUND)) {
            //搜索到新体温计
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String address = device.getAddress();
           if(device!=null)ToastUtil.show(context,""+device.getName()+" | "+device.getAddress());
            if (BluetoothAdapter.checkBluetoothAddress(address)) {  //mac地址是否符合要求
                String name = device.getName();
                if (!TextUtils.isEmpty(name) && name.length() > 3) { //名称是否不为空且长度大于3
                    if (name.toUpperCase().contains("EVE")) { //体温计是否包含EVE字符
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(BLEConfig.DEVICE_KEY, device);

                        service.sendMsg(BLEConfig.BLE_NEW_DEVICE, bundle);
                    }
                }
            }
        } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            int state = adapter.getState();
            if (state == BluetoothAdapter.STATE_OFF) {
                service.stopScan();
                service.stopLoop();
                service.disconnect();
                service.sendMsg(BLEConfig.BLE_OFF_LINE, "手机蓝牙被关闭了，无法正常连接体温计，请重新打开手机蓝牙。");
            } else if (state == BluetoothAdapter.STATE_ON) {
                service.sendMsg(BLEConfig.BLE_ON_LINE, "手机蓝牙已打开");
            }
        }
    }

}
