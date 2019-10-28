package com.Alan.eva.result;

import com.Alan.eva.tools.Tools;

import java.util.ArrayList;

/**
 * Created by CW on 2017/5/4.
 * 异地监测结果
 */
public class MonitorListRes extends Res{
    private String data;

    public ArrayList<QueryMonitorRes> getHistorical_data() {
        return historical_data;
    }

    public String getData() {
        return data;
    }


    public ArrayList<QueryMonitorRes> historical_data;




}
