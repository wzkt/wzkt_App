package com.Alan.eva.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.Alan.eva.model.ScheduleDataTag;
import com.Alan.eva.tools.alarm.Alarm;
import com.Alan.eva.tools.alarm.DabaseHelper;

import java.util.ArrayList;

public class ScheduleDAO {
	private DabaseHelper openHelper = null;

	public ScheduleDAO(Context context) {
		openHelper = new DabaseHelper(context, Alarm.DATABASE_NAME);
	}

	// 得到温度数据的日期
	public int getDay(int currentId) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		int day = -1;
		boolean result = false;
		Cursor cursor = db.query("dataline", new String[] { "_id", "date", "temp" }, "_id=?",
				new String[] { String.valueOf(currentId) }, null, null, null);
		if (cursor.moveToFirst()) {
			day = cursor.getInt(cursor.getColumnIndex("date"));
			Log.e("++++++", "cursor不为空");
			cursor.close();
			return day;
		} else {
			Log.e("++++++", "cursorwe空");
			return day;

		}

		/*
		 * if (db.isOpen()) { Cursor cursor = db.rawQuery(
		 * "select * from person where _id=?", new String[] { currentId + "" });
		 * if (cursor.moveToFirst()) { day = cursor.getColumnIndex("date");
		 * cursor.close(); } db.close(); } return day;
		 */
	}

	// 将数据库中的数据归0

	public void saveArray(String[] temps, int date) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		for (int i = 0; i < temps.length; i++) {
			ContentValues values = new ContentValues();
			values.put("date", date);
			Log.e("操作数据库时间修改时间为", date + "");
			values.put("temp", temps[i]);
			db.insert("dataline", null, values);

		}

	}

	/*
	 * public void saveArray(String[] temps, int date) { SQLiteDatabase db =
	 * openHelper.getReadableDatabase(); if (db.isOpen()) { for (int i = 0; i <
	 * temps.length; i++) { db.execSQL(
	 * "insert into dataline (day,temp) values(?,?)", new Object[] { date,
	 * temps[i] }); } }
	 * 
	 * }
	 */

	public void saveText(String temps, int date) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		if (db.isOpen()) {
			db.execSQL("insert into dataline (date,temp) values(?,?)", new Object[] { date, temps });
		}

	}

	// 删除表中的所有数据
	public void deleteAll() {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Log.e("++++++", "删除表");
		db.execSQL("DROP TABLE IF EXISTS dataline");
		db.execSQL(
				"create table if not exists dataline(_id integer primary key autoincrement,date integer,temp varchar(50))");

		db.close();
	}

	/**
	 * 将日程标志日期保存到数据库中
	 */
	public void saveTagDate(ArrayList<ScheduleDataTag> dataTagList) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		ScheduleDataTag dateTag = new ScheduleDataTag();
		for (int i = 0; i < dataTagList.size(); i++) {
			dateTag = dataTagList.get(i);
			ContentValues values = new ContentValues();
			values.put("year", dateTag.getYear());
			values.put("month", dateTag.getMonth());
			values.put("day", dateTag.getDay());
			db.insert("schedule", null, values);

		}

	}

	// 查询数据库中的体温数组
	public String[] getTemps() {
		String[] temps = new String[240];
		int i = 0;
		SQLiteDatabase db = openHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from dataline order by _id asc limit 0, 240", null);
			while (cursor.moveToNext()) {
				String temp = cursor.getString(cursor.getColumnIndex("temp"));
				temps[i] = temp;
				i++;
			}
			cursor.close();
		}
		return temps;
	}

	// 修改数据库中的数据
	public void updateTemp(int currentId, String newTemp) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		if (db.isOpen()) {
			db.execSQL("update dataline set temp=? where _id=?", new Object[] { newTemp, currentId });
		}
	}

	/**
	 * 只查询出当前月的日程日期
	 * 
	 * @param currentYear
	 * @param currentMonth
	 * @return
	 */
	public ArrayList<ScheduleDataTag> getTagDate(int currentYear, int currentMonth) {
		ArrayList<ScheduleDataTag> dateTagList = new ArrayList<ScheduleDataTag>();
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.query("schedule", new String[] { "_id", "year", "month", "day" }, "year=? and month=?",
				new String[] { String.valueOf(currentYear), String.valueOf(currentMonth) }, null, null, null);
		while (cursor.moveToNext()) {
			int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			int year = cursor.getInt(cursor.getColumnIndex("year"));
			int month = cursor.getInt(cursor.getColumnIndex("month"));
			int day = cursor.getInt(cursor.getColumnIndex("day"));
			ScheduleDataTag dataTag = new ScheduleDataTag(_id, month, year, day);
			dateTagList.add(dataTag);

		}
		cursor.close();
		if (dateTagList != null && dateTagList.size() > 0) {
			return dateTagList;
		}
		return null;
	}

}
