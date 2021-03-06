package com.malabon.database;

import java.sql.Blob;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.malabon.object.Sync;

public class LogUserTimeSheetDB {
	public static final String TABLE_LOG_USER_TIME_SHEET = "log_user_time_sheet";

	public static final String KEY_ID = "id";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_TIMEIN = "timein";
	public static final String KEY_TIMEIN_IMAGE = "timein_image";
	public static final String KEY_TIMEOUT = "timeout";
	public static final String KEY_TIMEOUT_IMAGE = "timeout_image";
	public static final String KEY_SALES_SUMMARY_ID = "sales_summary_id";

	private DatabaseHelper DbHelper;
	private SQLiteDatabase db;
	private final Context context;

	Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

	public LogUserTimeSheetDB(Context ctx) {
		this.context = ctx;
	}

	public LogUserTimeSheetDB open() throws SQLException {
		this.DbHelper = new DatabaseHelper(this.context);
		this.db = this.DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DbHelper.close();
	}

	public int addTimein(int user_id, byte[] timein_image) {
		int num = 0;
		try {
			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			String query = "INSERT INTO " + TABLE_LOG_USER_TIME_SHEET + " ("
					+ KEY_USER_ID + ", " + KEY_TIMEIN + ", " + KEY_TIMEIN_IMAGE
					+ ", " + KEY_TIMEOUT + ", " + KEY_TIMEOUT_IMAGE + ", "
					+ KEY_SALES_SUMMARY_ID + ")" + " SELECT " + user_id + ", "
					+ formatter.format(new Date()) + ", " + timein_image
					+ ", null, null, null" + " WHERE NOT EXISTS (SELECT "
					+ KEY_ID + " FROM " + TABLE_LOG_USER_TIME_SHEET
					+ " WHERE date(" + KEY_TIMEIN + ") = date('now') and "
					+ KEY_USER_ID + " = " + user_id + ")";

			/*
			 * String query = "INSERT INTO log_user_time_sheet " +
			 * "(user_id, timein, timein_image, timeout, timeout_image, sales_summary_id) "
			 * + "SELECT 1, datetime('now'), "+ timein_image
			 * +", null, null, null " +
			 * "WHERE NOT EXISTS (SELECT id FROM log_user_time_sheet where date(timein) = date('now') and user_id = 1)"
			 * ; db.execSQL(query);
			 */

			Log.d("temp_time", "timein..");
			Log.d("temp_time", String.valueOf(user_id));

			db.close();
			num = 1;
			Log.d("pos", "addTimein - success");
		} catch (Exception e) {
			Log.e("pos_error", "add_timein" + e);
		}
		return num;
	}

	public int addTimeout(int user_id, Blob timeout_image) {
		int num = 0;
		try {
			String salessummaryid = Sync.GetUserSalesSummaryID();

			SQLiteDatabase db = this.DbHelper.getWritableDatabase();
			String query = "UPDATE " + TABLE_LOG_USER_TIME_SHEET + " SET "
					+ KEY_TIMEOUT + " = '" + formatter.format(new Date())
					+ "', " + KEY_TIMEOUT_IMAGE + " = " + timeout_image + ", "
					+ KEY_SALES_SUMMARY_ID + " = " + salessummaryid + " "
					+ " WHERE date(" + KEY_TIMEIN + ") = date('now') and "
					+ KEY_USER_ID + " = " + user_id + "";
			db.execSQL(query);

			Log.d("temp_time", "timeout..");
			Log.d("temp_time", String.valueOf(user_id));
			Log.d("temp_time", salessummaryid);

			db.close();
			num = 1;
			Log.d("pos", "addTimeout - success");
		} catch (Exception e) {
			Log.e("pos_error", "add_timeout" + e);
		}
		return num;
	}

	/*
	 * public int addSalesSummary(int user_id, String sales_summary_id) { int
	 * num = 0; try { SQLiteDatabase db = this.DbHelper.getWritableDatabase();
	 * String query = "UPDATE " + TABLE_LOG_USER_TIME_SHEET + " SET " +
	 * KEY_SALES_SUMMARY_ID + " = '" + sales_summary_id + "'" + " WHERE date(" +
	 * KEY_TIMEIN + ") = date('now') and " + KEY_USER_ID + " = " + user_id + "";
	 * db.execSQL(query);
	 * 
	 * db.close(); num = 1; Log.d("pos", "addSalesSummary - success"); } catch
	 * (Exception e) { Log.e("pos_error", "add_salesSummary" + e); } return num;
	 * }
	 */
}
