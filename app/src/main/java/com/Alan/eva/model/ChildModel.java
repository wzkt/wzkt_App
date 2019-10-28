package com.Alan.eva.model;

/**
 * Created by CW on 2017/3/9.
 * 我的孩子数据模型
 */
public class ChildModel {
    private String cid;
    private String pid;
    private String name;
    private String weight;
    private String height;
    private String age;
    private String gender;
    private String highestTemp;
    private String lowestTemp;
    private String kickTime;
    private String feverTime;
    private String createTime;
    private String portrait;
    private boolean owner;
    private boolean isDefault;

    public String getCid() {
        return cid;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getAge() {
        return age;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHighestTemp(String highestTemp) {
        this.highestTemp = highestTemp;
    }

    public void setLowestTemp(String lowestTemp) {
        this.lowestTemp = lowestTemp;
    }

    public void setKickTime(String kickTime) {
        this.kickTime = kickTime;
    }

    public void setFeverTime(String feverTime) {
        this.feverTime = feverTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getGender() {
        return gender;
    }

    public String getHighestTemp() {
        return highestTemp;
    }

    public String getLowestTemp() {
        return lowestTemp;
    }

    public String getKickTime() {
        return kickTime;
    }

    public String getFeverTime() {
        return feverTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getPortrait() {
        return portrait;
    }

    public boolean isOwner() {
        return owner;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
