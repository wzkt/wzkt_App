package com.Alan.eva.result;

/**
 * Created by CW on 2017/5/4.
 * 异地监测结果
 */
public class QueryMonitorRes extends Res {
    private String data;
    private String body_temperature;
    private String room_temperature;
    private String power;
    private String fever_times;

    public String getFever_times() {
        return fever_times;
    }

    public void setFever_times(String fever_times) {
        this.fever_times = fever_times;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    private int count;
    private String create_time;

    public void setData(String data) {
        this.data = data;
    }

    public String getBody_temperature() {
        return body_temperature;
    }

    public void setBody_temperature(String body_temperature) {
        this.body_temperature = body_temperature;
    }

    public String getRoom_temperature() {
        return room_temperature;
    }

    public void setRoom_temperature(String room_temperature) {
        this.room_temperature = room_temperature;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getData() {
        return data;
    }
}
