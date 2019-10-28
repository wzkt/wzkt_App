package com.Alan.eva.result;

/**
 *
 *
 */
public class QueryDataRes{
    private String year;

    public String getOpt_temp() {
        return opt_temp;
    }

    public void setOpt_temp(String opt_temp) {
        this.opt_temp = opt_temp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    private String month;
    private String time;
    private String  opt_temp;

    private String   day;
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    private String temp;

    private String counts;

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }
}
