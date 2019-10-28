package com.Alan.eva.model;

import java.util.Arrays;

public class TempTimeJson {
	private String phone;
	private String y;
	private String[] fs;
	
	public TempTimeJson(String phone, String y, String[] fs) {
		super();
		this.phone = phone;
		this.y = y;
		this.fs = fs;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String[] getFs() {
		return fs;
	}
	public void setFs(String[] fs) {
		this.fs = fs;
	}
	@Override
	public String toString() {
		return "TempTimeJson [phone=" + phone + ", y=" + y + ", fs=" + Arrays.toString(fs) + "]";
	}


}
