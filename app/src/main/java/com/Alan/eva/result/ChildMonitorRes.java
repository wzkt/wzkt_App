package com.Alan.eva.result;

import com.Alan.eva.model.ChildMonitorData;

import java.util.ArrayList;

/**
 * Created by CW on 2017/3/23.
 * 孩子监护人列表结果
 */
public class ChildMonitorRes extends Res {
    private ArrayList<ChildMonitorData> data;

    public ArrayList<ChildMonitorData> getData() {
        return data;
    }
}
