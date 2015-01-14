package com.haidang.sampleapp.earthquake.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.haidang.sampleapp.earthquake.Earthquake;
import com.haidang.sampleapp.earthquake.EarthquakeInfoApp;
import com.haidang.sampleapp.earthquake.Earthquakes;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;

/**
 * @author Hai Dang
 *
 * Helper class - provide static methods for quick access to database from anywhere within the app
 * Method by default run on Uithread, so it should be put in a background thread when used.
 */
public class DatabaseAccessHelper {
	/**
	 * Insert individual earthquake object into database
	 * DB connection should be opened prior to this function call and should be closed by the caller
	 * for faster execution of batch data insert.
	 */
	public static void insertEarthquake(Earthquake earthquake, long timestamp, DatabaseManager dbManager){
		ContentValues values = new ContentValues();
		values.put(CachedEarthquakeDataTable.CREATED_TIME_MS, timestamp);
		values.put(CachedEarthquakeDataTable.DATETIME, earthquake.timedate);
		values.put(CachedEarthquakeDataTable.DEPTH, earthquake.depth);
		values.put(CachedEarthquakeDataTable.EARTHQUAKE_ID, earthquake.eqid);
		values.put(CachedEarthquakeDataTable.LAT, earthquake.lat);
		values.put(CachedEarthquakeDataTable.LON, earthquake.lon);
		values.put(CachedEarthquakeDataTable.MAGNITUDE, earthquake.magnitude);
		values.put(CachedEarthquakeDataTable.REGION, earthquake.region);
		values.put(CachedEarthquakeDataTable.SOURCE, earthquake.src);

		dbManager.insertValues(values, CachedEarthquakeDataTable.TABLE_NAME);
	}
	
	/**
	 * Insert a list of earthquake objects into database.
	 * By default this runs on UiThread
	 */
	public static void insertEarthquakes(Earthquakes earthquakes, long timestamp, boolean notifyWhenDone){
		DatabaseManager dbManager = DatabaseManager.getInstance(EarthquakeInfoApp.getInstance());
		DatabaseAccessHelper.cleanDbTable(CachedEarthquakeDataTable.TABLE_NAME, dbManager);
		SQLiteDatabase writableDB = dbManager.getWritableDB();
		writableDB.beginTransaction();
		
		for(Earthquake earthquake: earthquakes.earthquakes){
			insertEarthquake(earthquake, timestamp, dbManager);
		}
		writableDB.setTransactionSuccessful();
		writableDB.endTransaction();
		if(notifyWhenDone)
			EarthquakeInfoApp.getInstance().getContentResolver().notifyChange(CachedEarthquakeDataTable.CONTENT_URI, null);

	}
	
	/**
	 * Clear all data from this table
	 * @param tableName
	 * @param dbMananger
	 * @return
	 */
	public static boolean cleanDbTable(String tableName, DatabaseManager dbMananger) {
		try {
			dbMananger.deleteValues(tableName, null, null);
		} catch (SQLException sqlE) {
			// sqlE.printStackTrace();
			return false;
		}
		return true;
	}
}
