package com.haidang.sampleapp.earthquake.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.database.Cursor;

import com.haidang.sampleapp.earthquake.Earthquake;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;

/**
 * @author Hai Dang
 *
 * Utility class - in charge of data conversion
 */
public class DataFormatHelper {
	public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	
	public static Date getDateFromString(String datetime) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_PATTERN, Locale.ENGLISH);
		return sdf.parse(datetime);
	}
	
	public static String getDateString(String datetime) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
		return sdf.format(getDateFromString(datetime));
	}
	
	public static int getDayInDateString(String datetime) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
		Date date = sdf.parse(datetime);
		Calendar myCal = new GregorianCalendar();
		myCal.setTime(date);
		
		return myCal.get(Calendar.DAY_OF_MONTH);
	}
	
	public static Earthquake buildEarthquakeFromCursor(Cursor cursor){
		if(cursor==null || cursor.getCount() <= 0)
			return null;
		Earthquake eq = new Earthquake();
		
		eq.depth = cursor.getDouble(cursor.getColumnIndex(CachedEarthquakeDataTable.DEPTH));
		eq.lat = cursor.getDouble(cursor.getColumnIndex(CachedEarthquakeDataTable.LAT));
		eq.lon = cursor.getDouble(cursor.getColumnIndex(CachedEarthquakeDataTable.LON));
		eq.magnitude = cursor.getDouble(cursor.getColumnIndex(CachedEarthquakeDataTable.MAGNITUDE));
		eq.eqid = cursor.getString(cursor.getColumnIndex(CachedEarthquakeDataTable.EARTHQUAKE_ID));
		eq.region = cursor.getString(cursor.getColumnIndex(CachedEarthquakeDataTable.REGION));
		eq.src = cursor.getString(cursor.getColumnIndex(CachedEarthquakeDataTable.SOURCE));
		eq.timedate = cursor.getString(cursor.getColumnIndex(CachedEarthquakeDataTable.DATETIME));
		
		return eq;
	}
}
