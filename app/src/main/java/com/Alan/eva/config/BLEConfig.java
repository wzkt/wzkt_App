package com.Alan.eva.config;

/**
 * Created by CW on 2017/2/21.
 * 蓝牙状态描述
 */
public class BLEConfig {

    //配置key
    public static final String MSG_KEY = "msg";
    public static final String CMD_EXTRA = "cmd";
    public static final String DEVICE_KEY = "device";


    public static final int BLE_CONNECT_ERROR = 0x0009; //连接体温计异常
    //命令
    public static final int BLE_OPEN = 0x0001;//打开手机蓝牙
    public static final int BLE_SCAN_CMD = 0x0011;  //扫描体温计
    public static final int BLE_CONNECT_CMD = 0x0012; //连接体温计
    public static final int BLE_DISCONNECT_CMD = 0x0013; //连接体温计
    public static final int STOP_SERVICE_CMD = 0x0014;  //结束蓝牙服务
    public static final int CHILD_ID_CMD = 0x0015; //绑定孩子id，有网的时候上传数据
    public static final int READ_DEVISE_POWER_CMD = 0x0017; //读取电池电量
    public static final int MAC_CONNECT_CMD = 0x0019; //连接体温计
    public static final int BLE_CLOSE = 0x0002;//关闭手机蓝牙

    //结果返回
    public static final int BLE_DEVICE_NOT_FOUND = 0x0023;// 体温计无法正常连接

    public static final int BLE_IS_SCANNING = 0x00031;//正在扫描体温计
    public static final int BLE_NEW_DEVICE = 0x0032;//发现新体温计
    public static final int BLE_SCAN_FINISH = 0x0033;  //扫描结束了

    public static final int BLE_CONNECTING = 0x0040;  //正在尝试连接
    public static final int BLE_CONNECTED = 0x0041; //蓝牙已连接
    public static final int BLE_SERVER_DISCONNECTED = 0x0043; //蓝牙服务被断开了
    public static final int BLE_OFF_LINE = 0x0048;//手机蓝牙关闭了
    public static final int BLE_ON_LINE = 0x0049; //手机蓝牙被打开了
    public static final int BLE_DEVICE_DISCOVERY = 0x0044;//体温计被发现
    public static final int BLE_SERVICE_NOT_AVAILABLE = 0x0045; //体温计服务不可用

    public static final int BLE_TEMP_GET = 0x00045;  //得到了温度数据
    public static final int BLE_BATTERY_POWER = 0x0046;//得到了体温计电量

    public static final int BLE_LOOP_STOP = 0x0047; //轮询数据已结束

    public static final int BLE_EVM_TEMP_GET = 0x00051;  //得到了环境温度数据

    public static final int BLE_EVM_TEMP_GET_2 = 0x00055;  //得到了温度数据

    public static final int BLE_RELEASE_DEVICE = 0x0050; //体温计解除后的行为操作

    /*特征值属性名称*/
    public static String TEMPERATURE_SERVICES = "0000fff0-0000-1000-8000-00805f9b34fb";// 得到体温计温度服务特征属性
    public static String TEMPERATURE_CHARACTERISTICS = "0000fff5-0000-1000-8000-00805f9b34fb"; //温度特征值
    public static String TEMPERATURE_CHARACTERISTICS_FORMER = "0000fff6-0000-1000-8000-00805f9b34fb"; // 前一小时的数据
    public static String TEMPERATURE_SERVICES_POWER = "0000180f-0000-1000-8000-00805f9b34fb";// 得到体温计电量特征属性
    public static String  TEMPERATURE_CHARACTERISTICS_POWER = "00002a19-0000-1000-8000-00805f9b34fb";// 得到电量的character

//08-20 21:54:40.902 21630-21630/com.Alan.eva I/hjs: parentUuid==00001800-0000-1000-8000-00805f9b34fb
//08-20 21:54:40.902 21630-21630/com.Alan.eva I/hjs: parentUuid==00001801-0000-1000-8000-00805f9b34fb
//08-20 21:54:40.902 21630-21630/com.Alan.eva I/hjs: parentUuid==0000180a-0000-1000-8000-00805f9b34fb
//08-20 21:54:40.902 21630-21630/com.Alan.eva I/hjs: parentUuid==0000fff0-0000-1000-8000-00805f9b34fb
//08-20 21:54:40.912 21630-21630/com.Alan.eva I/hjs: parentUuid==0000180f-0000-1000-8000-00805f9b34fb

}
