package com.Alan.eva.model;

public class ScheduleDataTag {
	private int _id;
	private int month;
	private int year;
	private int day;

	public ScheduleDataTag() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ScheduleDataTag(int _id, int month, int year, int day) {
		super();
		this._id = _id;
		this.month = month;
		this.year = year;
		this.day = day;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "ScheduleDataTag [_id=" + _id + ", month=" + month + ", year=" + year + ", day=" + day + "]";
	}

}
