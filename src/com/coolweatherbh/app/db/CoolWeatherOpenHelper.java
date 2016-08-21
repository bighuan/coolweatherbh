package com.coolweatherbh.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * ר�Ž�������
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	/**
	 * Province���������
	 */
	public static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement,"//����������
			+ "privince_name text,"//ʡ��
			+ "privince_code text)";//ʡ������
	/**
	 * City���������
	 */
	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement,"//����������
			+ "city_name text,"//������
			+ "city_code text," //�м�����
			+ "privince_id integer)";//province_id��City������Provice�������

	public static final String CREATE_COUNTY = "create table County ("
			+ "id integer primary key autoincrement,"//����������
			+ "county_name text,"//����
			+ "county_code text,"//�ؼ�����
			+ "city_id integet)";//city_id��County������City�������

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);//����Province��
		db.execSQL(CREATE_CITY);//����City��
		db.execSQL(CREATE_COUNTY);//����County��
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}