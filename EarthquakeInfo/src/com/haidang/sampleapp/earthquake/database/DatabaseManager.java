package com.haidang.sampleapp.earthquake.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;

/**
 * @author Hai Dang
 *
 */
public class DatabaseManager {
	
	public static final int CURRENT_DATABASE_VERSION = 1;

	private MySQLiteHelper sqliteHelper;

	private static DatabaseManager _instance;
	private SQLiteDatabase writableDb, readableDb;

	public static DatabaseManager getInstance(Context context) {
		if (_instance == null)
			_instance = new DatabaseManager(context);
		return _instance;
	}

	private DatabaseManager(Context context) {
		sqliteHelper = new MySQLiteHelper(context);
		writableDb = sqliteHelper.getWritableDatabase();
		readableDb = sqliteHelper.getReadableDatabase();
	}

	public Cursor selectValues(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, SQLiteDatabase readableDB) {

		return readableDB.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	public SQLiteDatabase getWritableDB() {
		return writableDb;
	}

	public SQLiteDatabase getReadableDB() {
		return readableDb;
	}

	public long insertValues(ContentValues values, String tableName) {
		return writableDb.insert(tableName, null, values);
	}
	
	public long insertValuesOrThrow(ContentValues values, String tableName) {
		return writableDb.insertOrThrow(tableName, null, values);
	}


	public int deleteValues(String table, String whereClause, String[] whereArgs) {
		return writableDb.delete(table, whereClause, whereArgs);
	}

	public long updateValues(ContentValues values, String tableName, String whereClause, String[] whereArgs) {
		return writableDb.update(tableName, values, whereClause, whereArgs);
	}
}

class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "EARTHQUAKE_DB.db";

	MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DatabaseManager.CURRENT_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		CachedEarthquakeDataTable.createTable(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.setVersion(newVersion);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.setVersion(newVersion);
	}


}