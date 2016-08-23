package com.coolweatherbh.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 专门建表的类
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	/**
	 * Province表建表语句
	 */
	public static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement,"//自增长主键
			+ "province_name text,"//省名
			+ "province_code text)";//省级代号
	/**
	 * City表建表语句
	 */
	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement,"//自增长主键
			+ "city_name text,"//城市名
			+ "city_code text," //市级代号
			+ "province_id integer)";//province_id是City表关联Provice表的外键

	public static final String CREATE_COUNTY = "create table County ("
			+ "id integer primary key autoincrement,"//自增长主键
			+ "county_name text,"//县名
			+ "county_code text,"//县级代号
			+ "city_id integet)";//city_id是County表关联City表的外键

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);//创建Province表
		db.execSQL(CREATE_CITY);//创建City表
		db.execSQL(CREATE_COUNTY);//创建County表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
