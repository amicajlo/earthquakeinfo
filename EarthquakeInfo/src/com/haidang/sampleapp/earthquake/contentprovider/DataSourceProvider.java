package com.haidang.sampleapp.earthquake.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.haidang.sampleapp.earthquake.database.DatabaseManager;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;

/**
 * @author Hai Dang
 *
 * Content provider class - provide a clean and easy way to access database with the notifier set up already.
 * This is really useful when using along with Loader class.
 */
public class DataSourceProvider extends ContentProvider {
	private static final int ALL_EARTHQUAKES = 1;
	

	private static final String AUTHORITY = DataSourceProvider.class.getPackage().getName() + ".datasourceprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");


	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, CachedEarthquakeDataTable.TABLE_NAME, ALL_EARTHQUAKES);
	}

	private DatabaseManager dbManager;

	@Override
	public boolean onCreate() {
		// get access to the database helper
		dbManager = DatabaseManager.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = dbManager.getReadableDB();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String groupBy = null;
		String having = null;
		if (sortOrder == null)
			sortOrder = "";
		switch (uriMatcher.match(uri)) {
		case ALL_EARTHQUAKES: {
			queryBuilder.setTables(CachedEarthquakeDataTable.TABLE_NAME);
			break;
		}
		
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		if (sortOrder.trim().isEmpty())
			sortOrder = null;
		else if (sortOrder.trim().startsWith(","))
			sortOrder = sortOrder.trim().substring(1);
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbManager.getWritableDB();
		String tableName = "";
		switch (uriMatcher.match(uri)) {
		case ALL_EARTHQUAKES: {
			tableName = CachedEarthquakeDataTable.TABLE_NAME;
			break;
		}

		
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		int deleteCount = db.delete(tableName, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ALL_EARTHQUAKES:
			return "vnd.android.cursor.dir/com.haidang.sampleapp.data.earthquake";

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		SQLiteDatabase db = dbManager.getWritableDB();
		switch (uriMatcher.match(uri)) {
		case ALL_EARTHQUAKES: {
			long id = db.insert(CachedEarthquakeDataTable.TABLE_NAME, null, initialValues);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(CachedEarthquakeDataTable.CONTENT_URI + "/" + id);
		}
		
		default: {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbManager.getWritableDB();
		String tableName = "";
		switch (uriMatcher.match(uri)) {
		case ALL_EARTHQUAKES: {
			tableName = CachedEarthquakeDataTable.TABLE_NAME;
			break;
		}

		
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		int updateCount = db.update(tableName, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}



}
