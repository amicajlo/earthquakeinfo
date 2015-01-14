package com.haidang.sampleapp.earthquake.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.haidang.sampleapp.earthquake.Earthquakes;
import com.haidang.sampleapp.earthquake.LoaderIdConstants;
import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.database.DatabaseAccessHelper;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;
import com.haidang.sampleapp.earthquake.exception.NetworkNotAvailableException;
import com.haidang.sampleapp.earthquake.network.NetworkManager;
import com.haidang.sampleapp.earthquake.network.NetworkRequest.NetworkCallback;
import com.haidang.sampleapp.earthquake.network.ServiceResponse;

/**
 * @author Hai Dang
 * Splash screen - validate current data and download data to database before proceed to the main app activity (HomeScreen)
 */
public class LoadingScreen extends Screen{
	public static final int MAX_DATA_LIVE = 48;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_loading_screen);
		verifyData();
	}
	

	
	private void verifyData(){
		getSupportLoaderManager().restartLoader(LoaderIdConstants.LOADER_ID_VERIFY_DATA, null, verifyDataLoader);
	}
	
	private void downloadData(){
		try {
			NetworkManager.getInstance().getEarthquakesData(dataDownloadUpdater);
		} catch (NetworkNotAvailableException e) {
			displayNetworkNotAvailableAlert(true);
		}
	}
	
	private void saveData(final Earthquakes earthquakes){
		new AsyncTask<Void, Void, Boolean>(){
			@Override
			protected Boolean doInBackground(Void... params) {
				DatabaseAccessHelper.insertEarthquakes(earthquakes, System.currentTimeMillis(), false);
				return true;
			}
			@Override
			protected void onPostExecute(Boolean result){
				openApp();
			}
		}.execute();
	}
	
	private void openApp(){
		Intent intent = new Intent(LoadingScreen.this, HomeScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		finish();
	}
	
	private LoaderManager.LoaderCallbacks<Cursor> verifyDataLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle data) {
			String[] projection = {CachedEarthquakeDataTable.CREATED_TIME_MS};
			String sortBy = CachedEarthquakeDataTable.CREATED_TIME_MS + " LIMIT 1";
			
			return new CursorLoader(LoadingScreen.this, CachedEarthquakeDataTable.CONTENT_URI, projection, null, null, sortBy);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if(cursor==null || cursor.getCount()==0){
				downloadData();
			}
			else{
				cursor.moveToFirst();
				long lastSavedTimestamp = cursor.getLong(cursor.getColumnIndex(CachedEarthquakeDataTable.CREATED_TIME_MS));
				long dataLife = System.currentTimeMillis() - lastSavedTimestamp;
				// Check if data is more than MAX_DATA_LIVE days or not
				// if it is then download the new data, save it then god to HomeScreen
				if(dataLife > MAX_DATA_LIVE * 1000 * 60 * 60){
					downloadData();
				}
				else{
					openApp();
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> cursor) {}
		
	};
	
	// Updater for Network request
	NetworkCallback<ServiceResponse<Earthquakes>> dataDownloadUpdater = new NetworkCallback<ServiceResponse<Earthquakes>>() {
		
		@Override
		public void update(ServiceResponse<Earthquakes> response) {
			if(response!=null && response.result!=null && response.result.count.intValue()>0){
				saveData(response.result);
			}
			else{
				displayJobFail();
			}
		}
	};
}
