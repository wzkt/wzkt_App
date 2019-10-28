package com.Alan.eva.model.profile;

import java.util.ArrayList;

/**
 * Created by CW on 2017/4/11.
 * 孩子月报数据
 */
public class MonthProfileData {
    private int score;
    private String summary;
    private String feverTips;
    private String rateTips;
    private String epilogue;
    private ArrayList<TempTrendItem> trend;
    private ArrayList<FeverCountItem> fever;
    private TempRateData rate;

    public int getScore() {
        return score;
    }

    public String getSummary() {
        return summary;
    }

    public String getFeverTips() {
        return feverTips;
    }

    public String getEpilogue() {
        return epilogue;
    }

    public String getRateTips() {
        return rateTips;
    }

    public ArrayList<TempTrendItem> getTrend() {
        return trend;
    }

    public ArrayList<FeverCountItem> getFever() {
        return fever;
    }

    public TempRateData getRate() {
        return rate;
    }
}
