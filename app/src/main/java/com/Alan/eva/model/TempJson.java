package com.Alan.eva.model;

import java.util.Arrays;

public class TempJson {
	private String[] temps;
	private String phone;
	private String y;
	private String m;
	private String d;
	

	@Override
	public String toString() {
		return "TempJson [temps=" + Arrays.toString(temps) + ", phone=" + phone + ", y=" + y + ", m=" + m + ", d=" + d
				+ "]";
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

	public String getM() {
		return m;
	}

	public void setM(String m) {
		this.m = m;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public TempJson(String[] temps, String id,String y,String m,String d) {
		this.temps = temps;
		this.phone = id;
		this.y = y;
		this.m = m;
		this.d = d;
	}

	

	public String[] getTemps() {
		return temps;
	}

	public void setTemps(String[] temps) {
		this.temps = temps;
	}


	
	

}
