package com.haidang.sampleapp.earthquake.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.haidang.sampleapp.earthquake.contentprovider.DataSourceProvider;

/**
 * @author Hai Dang
 *
 */
public final class CachedEarthquakeDataTable implements BaseColumns {

	public static final String TABLE_NAME = "cached_earthquakes";
	public static final Uri CONTENT_URI = Uri.withAppendedPath(DataSourceProvider.CONTENT_URI, TABLE_NAME);

	public static final String CREATED_TIME_MS = "createdTimeMs";
	public static final String EARTHQUAKE_ID = "eqid";
	public static final String SOURCE = "source";
	public static final String DATETIME = "datetime";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String MAGNITUDE = "magnitude";
	public static final String DEPTH = "depth";
	public static final String REGION = "region";

	public static final void createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EARTHQUAKE_ID + " TEXT NOT NULL, " + CREATED_TIME_MS + " LONG, " + SOURCE
				+ " TEXT, " + DATETIME + " TEXT, " + LAT + " REAL, " + LON + " REAL, " + MAGNITUDE + " REAL, " + DEPTH + " REAL, "
				+ REGION + " TEXT); ");

		// Create indexes for faster access
	}

	public static final void reCreateTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		createTable(db);
	}
}
