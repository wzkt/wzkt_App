package com.Alan.eva.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by CW on 2017/4/20.
 * 孩子概况数据
 */
@Table(name = "childsummary")
public class ChildSummary {
    @Column(name = "name")
    private String name;
    @Column(name = "height")
    private String height;
    @Column(name = "weight")
    private String weight;
    @Column(name = "age")
    private String age;
    @Column(name = "gender")
    private String gender;
    private String portrait;
    @Column(name = "mac")
    private String mac;
    @Column(name = "max")
    private String max;
    @Column(name = "min")
    private String min;
    @Column(name = "count")
    private String count;
    private String tips;
    @Column(name = "id", isId = true)
        private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChildSummary(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public String toString() {
        return "ChildSummary{" +
                "name='" + name + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", portrait='" + portrait + '\'' +
                ", mac='" + mac + '\'' +
                ", max='" + max + '\'' +
                ", min='" + min + '\'' +
                ", count='" + count + '\'' +
                ", tips='" + tips + '\'' +
                ", id=" + id +
                '}';
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}