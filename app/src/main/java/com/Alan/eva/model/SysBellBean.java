package com.Alan.eva.model;

/**
 * Created by CW on 2017/5/11.
 * 系统铃声实体类
 */
public class SysBellBean {

    private String name;
    private String path;
    private boolean check;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
