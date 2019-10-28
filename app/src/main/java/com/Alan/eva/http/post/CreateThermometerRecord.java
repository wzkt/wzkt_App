package com.Alan.eva.http.post;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by houjs on 2018-12-23.
 */

public class CreateThermometerRecord extends AbsHttp {
    private String base_station_mac;
    private String thermometer_mac;
    private  float patient_temperature;
    private int room_temperature;//'
    private int battery_level; //':

    public void setBase_station_mac(String base_station_mac) {
        this.base_station_mac = base_station_mac;
    }

    public void setThermometer_mac(String thermometer_mac) {
        this.thermometer_mac = thermometer_mac;
    }

    public void setPatient_temperature(float patient_temperature) {
        this.patient_temperature = patient_temperature;
    }

    public void setRoom_temperature(int room_temperature) {
        this.room_temperature = room_temperature;
    }

    public void setBattery_level(int battery_level) {
        this.battery_level = battery_level;
    }

    @Override
    protected String domain() {
        return BuildConfig.SERVER_URL_NEW+"api/create_thermometer_record/";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {

        builder.put("base_station_mac", base_station_mac);
        builder.put("thermometer_mac", thermometer_mac);
        builder.put("patient_temperature", patient_temperature,null);
        builder.put("room_temperature", room_temperature,null);
        builder.put("battery_level", battery_level,null);
        return  builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }
}
