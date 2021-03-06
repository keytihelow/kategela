package com.malabon.database;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.malabon.object.PosSettings;

public class PosSettingsDB {
	public static final String TABLE_POS_SETTINGS = "pos_settings";

	public static final String KEY_ID = "id";
	public static final String KEY_BRANCH_ID = "branch_id";
	public static final String KEY_BRANCH_NAME = "branch_name";
	public static final String KEY_IS_MANUAL = "is_manual";
	public static final String KEY_SYNC_FREQUENCY = "sync_frequency";
	public static final String KEY_SYNC_TIME = "sync_time";
	public static final String KEY_CLEAR_FREQUENCY = "clear_frequency";

	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	private final Context context;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DBAdapter.DATABASE_NAME, null,
					DBAdapter.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	public PosSettingsDB(Context ctx) {
		this.context = ctx;
	}

	public PosSettingsDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public PosSettings getAllPosSettings() {	//here na gela
		PosSettings posSettings = null;
		try {
			String selectQuery = "SELECT * FROM " + TABLE_POS_SETTINGS
					+ " LIMIT 1";
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor != null) {
				cursor.moveToFirst();
				
				posSettings = new PosSettings();
				posSettings.branch_id = cursor.getInt(1);
				posSettings.branch_name = cursor.getString(2);
				posSettings.is_manual = cursor.getInt(3)>0;
				if (!posSettings.is_manual){
					posSettings.sync_frequency = cursor.getInt(4);
					posSettings.sync_time = (java.sql.Date) new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(cursor
							.getString(5));
				}
				else{
					posSettings.sync_frequency = -1;
				}
				posSettings.clear_frequency = cursor.getInt(6);
			}
			cursor.close();
			db.close();
			Log.d("pos", "getAllPosSettings - success");
		} catch (Exception e) {
			Log.e("pos_error", "getAllPosSettings" + e);
		}
		return posSettings;
	}
	
	// TODO: delete after testing
	// --------------------------------------------------------------------------

	public int getPosSettingCount() {
		int num = 0;
		try {
			String countQuery = "SELECT " + KEY_ID + " FROM "
					+ TABLE_POS_SETTINGS;
			SQLiteDatabase db = this.DbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			num = cursor.getCount();

			cursor.close();
			db.close();
			Log.d("pos", "getPosSettingCount: " + String.valueOf(num));
		} catch (Exception e) {
			Log.e("pos_error", "getPosSettingCount" + e);
		}
		return num;
	}
	
	public void tempAddPosSettings() {
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			ContentValues values = null;

			values = new ContentValues();
			values.put(KEY_ID, 2);
			values.put(KEY_BRANCH_ID, 3);
			values.put(KEY_BRANCH_NAME, "Cubao");
			values.put(KEY_IS_MANUAL, 1);
			//values.put(KEY_SYNC_FREQUENCY, 1);	//null
			//values.put(KEY_SYNC_TIME, "");		//null
			values.put(KEY_CLEAR_FREQUENCY, 10);
			db.insert(TABLE_POS_SETTINGS, null, values);

			db.close();
			Log.d("pos", "tempAddPosSettings - success");
		} catch (Exception e) {
			Log.e("pos_error", "tempAddPosSettings" + e);
		}
	}
}
