package com.fredrick.mixedsignals.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter {

	private static final int DATABASE_VERSION = 7;
	
	private static final String DATABASE_NAME = "MixedSignals.db";
	
	
	public static final String BLOCK_USERS_TABLE = "block_users";


	private static final String KEY_TABLE_ID	= "id";

	private static final String KEY_USER_NAME = "user_name";

	private static final String KEY_USER_PHONE = "user_phone";

	private static final String KEY_USER_SMS = "user_sms";

	private static final String KEY_USER_BLOCK = "user_block";


	private static final String BLOCK_USERS_TABLE_CREATE = "create table " + BLOCK_USERS_TABLE + " ( " + KEY_TABLE_ID + " integer primary key autoincrement NOT NULL, "
																					 + KEY_USER_NAME + " text, "
																					 + KEY_USER_PHONE + " text, "
												   					   				 + KEY_USER_SMS + " text, "
												   					   				 + KEY_USER_BLOCK + " text );";


	private SQLiteDatabase database; 
	
	private final Context context; 

	private DBHelper dbhelper;
	
	public DatabaseAdapter(Context context) {
		this.context = context;
		this.dbhelper = new DBHelper(this.context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public synchronized boolean openToRead() {
		
		if (this.database != null) {
			return false;
		}
		
		try {
			this.database = this.dbhelper.getReadableDatabase();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}

	public synchronized boolean openToWrite() {

		if (this.database != null) {
			return false;
		}
		
		try {
			this.database = this.dbhelper.getWritableDatabase();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public long insert(String strTable, String strUserName, String strUserPhone, String strSMS, String strBlcok) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USER_NAME, strUserName);
		initialValues.put(KEY_USER_PHONE, strUserPhone);
		initialValues.put(KEY_USER_SMS, strSMS);
		initialValues.put(KEY_USER_BLOCK, strBlcok);
		return this.database.insert(strTable, null, initialValues);
	}
	
	public Cursor select(String strTable) {
		Cursor cursor = this.database.rawQuery("SELECT *" + " FROM " + strTable + " order by " + KEY_TABLE_ID + " ;", null);
		return cursor;
	}

	public Cursor selectItem(String table, long strId) {
		Cursor cursor = this.database.rawQuery("SELECT *" + " FROM " + table + " where id = '" + strId + "';", null);
		return cursor;
	}

	public boolean deleteItem(String table, long strId) {
		return this.database.delete(table, KEY_TABLE_ID + "=" + strId, null) > 0;
	}

	public boolean updateName(String strTable, long strID, String strName, String strPhone) {
		ContentValues args = new ContentValues();
		args.put(KEY_USER_NAME, strName);
		args.put(KEY_USER_PHONE, strPhone);
		return this.database.update(strTable, args, KEY_TABLE_ID + "=" + strID, null) > 0;
	}

	public boolean updatePhone(String strTable, long strID, String strSMS, String strChecked) {
		ContentValues args = new ContentValues();
		args.put(KEY_USER_SMS, strSMS);
		args.put(KEY_USER_BLOCK, strChecked);
		return this.database.update(strTable, args, KEY_TABLE_ID + "=" + strID, null) > 0;
	}

	public boolean updateBlock(String strTable, long strID, String strBlock) {
		ContentValues args = new ContentValues();
		args.put(KEY_USER_BLOCK, strBlock);
		return this.database.update(strTable, args, KEY_TABLE_ID + "=" + strID, null) > 0;
	}

	public synchronized void close() {
		
		if (this.database != null) {
			this.database.close();
		}
		
		this.database = null;
	}
	
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(BLOCK_USERS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + BLOCK_USERS_TABLE_CREATE);
			onCreate(db);
		}
	}
}
