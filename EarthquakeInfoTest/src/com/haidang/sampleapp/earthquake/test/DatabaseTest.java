package com.haidang.sampleapp.earthquake.test;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;

import android.database.Cursor;

import com.haidang.sampleapp.earthquake.Earthquake;
import com.haidang.sampleapp.earthquake.Earthquakes;
import com.haidang.sampleapp.earthquake.database.DatabaseAccessHelper;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;
import com.haidang.sampleapp.earthquake.fragment.FragmentChartByMagnitude;
import com.haidang.sampleapp.earthquake.util.DataFormatHelper;

public class DatabaseTest extends MyTestCase {
	private static final String JSON_DATA = "data.json";
	
	

	private Earthquakes getDataFromAssetFileTest(String filename) {
		try {
			InputStream is = assestManager.open(filename);
			ObjectMapper objectMapper = new ObjectMapper();
			Earthquakes earthquakes = objectMapper.readValue(is, Earthquakes.class);
			assertNotNull(earthquakes);
			return earthquakes;
		} catch (IOException e) {
			fail("Data file not presented for the test");
			return null;
		}

	}

	public void testSaveDataToDb() {
		DatabaseAccessHelper.cleanDbTable(CachedEarthquakeDataTable.TABLE_NAME, dbManager);
		DatabaseAccessHelper.insertEarthquakes(getDataFromAssetFileTest(JSON_DATA), System.currentTimeMillis(), false);
		
		String[] projection = {"*"};
		Cursor cursor = contentResolver.query(CachedEarthquakeDataTable.CONTENT_URI, projection, null, null, null);
		assertNotNull(cursor);
		assertTrue(cursor.getCount()>0);
		cursor.close();
	}
	
	public void testLoadDataByDate(){
		testSaveDataToDb();
		String date = "2013-07-15";
		String[] projection = {"*"};
		String selection = CachedEarthquakeDataTable.DATETIME + " like '%" + date + "%'";
		Cursor cursor = contentResolver.query(CachedEarthquakeDataTable.CONTENT_URI, projection, selection, null, null);
		assertNotNull(cursor);
		assertTrue(cursor.getCount()==28);
		for(int i = 0; i < cursor.getCount(); i++){
			cursor.moveToPosition(i);
			Earthquake eq = DataFormatHelper.buildEarthquakeFromCursor(cursor);
			assertNotNull(eq);
			assertTrue(eq.timedate.contains(date));
		}
		cursor.close();
	}
	
	public void testLoadDataByMagnitudeScale(){
		testSaveDataToDb();
		String scale = FragmentChartByMagnitude.SCALE[2];
		String[] projection = {"*"};
		String selection = null;
		if (scale.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[0])) {
			selection = CachedEarthquakeDataTable.MAGNITUDE + " <= 4.5";
		} else if (scale.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[1])) {
			selection = CachedEarthquakeDataTable.MAGNITUDE + " > 4.5 AND " + CachedEarthquakeDataTable.MAGNITUDE + " < 5.2";
		} else if (scale.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[2])) {
			selection = CachedEarthquakeDataTable.MAGNITUDE + " >= 5.2 AND " + CachedEarthquakeDataTable.MAGNITUDE + " < 6.2";
		} else if (scale.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[3])) {
			selection = CachedEarthquakeDataTable.MAGNITUDE + " >= 6.2";
		}
		Cursor cursor = contentResolver.query(CachedEarthquakeDataTable.CONTENT_URI, projection, selection, null, null);
		assertNotNull(cursor);
		assertTrue(cursor.getCount()==54);
		for(int i = 0; i < cursor.getCount(); i++){
			cursor.moveToPosition(i);
			Earthquake eq = DataFormatHelper.buildEarthquakeFromCursor(cursor);
			assertNotNull(eq);
			assertTrue(eq.getType() == Earthquake.Type.STRONG);
		}
		cursor.close();
	}
}