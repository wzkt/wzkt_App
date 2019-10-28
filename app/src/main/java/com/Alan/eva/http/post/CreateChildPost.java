package com.Alan.eva.http.post;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

import java.io.File;

/**
 * Created by CW on 2017/3/14.
 * 创建孩子提交
 */
public class CreateChildPost extends AbsHttp {
    private String pid;
    private String name;
    private String gender;

    public String getThermometer_add() {
        return thermometer_add;
    }

    public void setThermometer_add(String thermometer_add) {
        this.thermometer_add = thermometer_add;
    }

    private String age;
    private String height;
    private String weight;
    private String portrait;
    private String thermometer_add;
   // private String mac_address;


    @Override
    protected String domain() {
        return BuildConfig.SERVER_URL_NEW+"api/patient_creation/";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("mobile_num", pid);
        builder.put("name", name);
        builder.put("gender", gender);
        builder.put("age", age);
        builder.put("height", height);
        builder.put("weight", weight);
        builder.put("mac_address", thermometer_add);
        //builder.put("mac_address", mac_address);

        return builder;
    }

//    public void setMac_address(String mac_address) {
//        this.mac_address = mac_address;
//    }

    @Override
    protected boolean addFile(ReqParam params) {
        //params.put("portrait", new File(portrait));
        return true;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

}
