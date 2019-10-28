package com.Alan.eva.tools.alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author sjz 继承SQLiteOpenHelper帮助类，通过getReadableDatabase()或
 *         getWritableDatabase()方法得到SqliteDabase对象。
 * 
 */
public class DabaseHelper extends SQLiteOpenHelper {

	private final static int VERSION = 8;

	// SQLiteOpenHelper的子类必须有该构造函数
	public DabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public DabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	/*
	 * 该函数是在第一次创建数据库的时候调用，实际上是在得到SqliteDabase对象的时候才调用。 即调用getReadableDatabase()或
	 * getWritableDatabase()时才调用此方法。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists alarms(" + "_id INTEGER PRIMARY KEY, hour INTEGER,"
				+ "minutes INTEGER,repeat varchar(20)," + "bell varchar(50),vibrate INTEGER,"
				+ "label varchar(50),enabled INTEGER,nextMillis INTEGER)");
		db.execSQL(
				"create table if not exists schedule(_id integer primary key autoincrement,year integer,month integer,day integer)");
		db.execSQL(
				"create table if not exists dataline(_id integer primary key autoincrement,date integer,temp varchar(50))");

		System.out.println("create");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS alarms");
			db.execSQL("DROP TABLE IF EXISTS schedule");
			db.execSQL("DROP TABLE IF EXISTS dataline");
			onCreate(db);
		}
	}

}
