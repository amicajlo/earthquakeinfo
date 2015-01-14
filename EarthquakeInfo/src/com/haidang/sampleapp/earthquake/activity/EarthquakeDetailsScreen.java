package com.haidang.sampleapp.earthquake.activity;

import android.app.ActionBar;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.haidang.sampleapp.earthquake.Earthquake;
import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;
import com.haidang.sampleapp.earthquake.fragment.FragmentChartByMagnitude;
import com.haidang.sampleapp.earthquake.util.DataFormatHelper;

/**
 * @author Hai Dang
 * Activity in charge of displaying Earthquake details
 * It can handle multiple earthquakes ---> display in the listview or a single earthquake object.
 * For multiple earthquakes, it will filter data by date or by magnitude scale base on parameters set by caller activity.
 * 
 */
public class EarthquakeDetailsScreen extends Screen {
	public static final int TYPE_SINGLE = 0, TYPE_MULTI = 1;
	public static final int MULTITYPE_MAG_SCALE = 0, MULTITYPE_DATE = 1;

	public static final String BUNDLE_KEY_DISPLAY_TYPE = "BUNDLE_KEY_DISPLAY_TYPE";

	public static final String BUNDLE_KEY_SINGLE_EARTHQUAKE_ID = "BUNDLE_KEY_SINGLE_EARTHQUAKE_ID";
	public static final String BUNDLE_KEY_MULTI_TYPE = "BUNDLE_KEY_MULTI_TYPE";
	public static final String BUNDLE_KEY_MULTI_SEARCH_TEXT = "BUNDLE_KEY_MULTI_SEARCH_TEXT ";
	private int displayType = TYPE_SINGLE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
			return;
		}
		displayType = extras.getInt(BUNDLE_KEY_DISPLAY_TYPE);

		if (displayType == TYPE_SINGLE) {
			setContentView(R.layout.earthquake_details_item);
		} else {
			setContentView(R.layout.activity_earthquake_details);
		}
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		setupViews();
	}
	
	/**
	 * Setup Views based on display type then load correct data, bind them into views.
	 */
	private void setupViews() {
		if (displayType == TYPE_SINGLE) {
			getSupportLoaderManager().restartLoader(displayType, getIntent().getExtras(), loadEarthquake);
			getActionBar().setTitle("Earthquake details");
		} else if (displayType == TYPE_MULTI) {
			if (!getIntent().getExtras().containsKey(BUNDLE_KEY_MULTI_TYPE))
				return;
			getActionBar().setTitle(getIntent().getExtras().getString(BUNDLE_KEY_MULTI_SEARCH_TEXT));

			ListView listview = (ListView) findViewById(R.id.listview);
			String[] from = { CachedEarthquakeDataTable.REGION, CachedEarthquakeDataTable.MAGNITUDE, CachedEarthquakeDataTable.DEPTH,
					CachedEarthquakeDataTable.LAT, CachedEarthquakeDataTable.LON, CachedEarthquakeDataTable.SOURCE,
					CachedEarthquakeDataTable.DATETIME };
			int[] to = { R.id.text_region, R.id.text_magnitude, R.id.text_depth, R.id.text_lat, R.id.text_lon, R.id.text_src, R.id.text_timedate };
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.earthquake_details_item, null, from, to, -1);
			listview.setAdapter(adapter);

			getSupportLoaderManager().restartLoader(displayType, getIntent().getExtras(), loadEarthquakes);
		}
	}

	private void setupSingleEarthquakeData(Earthquake eq) {
		TextView regionTv = (TextView) findViewById(R.id.text_region);
		TextView magnitudeTv = (TextView) findViewById(R.id.text_magnitude);
		TextView depthTv = (TextView) findViewById(R.id.text_depth);
		TextView latTv = (TextView) findViewById(R.id.text_lat);
		TextView lonTv = (TextView) findViewById(R.id.text_lon);
		TextView srcTv = (TextView) findViewById(R.id.text_src);
		TextView timedateTv = (TextView) findViewById(R.id.text_timedate);

		regionTv.setText(eq.region);
		depthTv.setText(String.valueOf(eq.depth));
		magnitudeTv.setText(String.valueOf(eq.magnitude));
		latTv.setText(String.valueOf(eq.lat));
		lonTv.setText(String.valueOf(eq.lon));
		srcTv.setText(eq.src);
		timedateTv.setText(eq.timedate);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			onBackPressed();
			return true;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.no_change, R.anim.slide_out_down);
	}
	/*
	 * Loader to load multiple data
	 */
	LoaderManager.LoaderCallbacks<Cursor> loadEarthquakes = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle data) {
			if (data == null)
				return null;

			int multiType = data.getInt(BUNDLE_KEY_MULTI_TYPE);
			String searchText = data.getString(BUNDLE_KEY_MULTI_SEARCH_TEXT);
			String[] projections = { "*" };
			String selection = null;
			String[] where = null;
			if (multiType == MULTITYPE_DATE) {
				selection = CachedEarthquakeDataTable.DATETIME + " like '%" + searchText + "%'";
				// where = new String[]{searchText};
			} else if (multiType == MULTITYPE_MAG_SCALE) {
				if (searchText.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[0])) {
					selection = CachedEarthquakeDataTable.MAGNITUDE + " <= 4.5";
				} else if (searchText.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[1])) {
					selection = CachedEarthquakeDataTable.MAGNITUDE + " > 4.5 AND " + CachedEarthquakeDataTable.MAGNITUDE + " < 5.2";
				} else if (searchText.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[2])) {
					selection = CachedEarthquakeDataTable.MAGNITUDE + " >= 5.2 AND " + CachedEarthquakeDataTable.MAGNITUDE + " < 6.2";
				} else if (searchText.equalsIgnoreCase(FragmentChartByMagnitude.SCALE[3])) {
					selection = CachedEarthquakeDataTable.MAGNITUDE + " >= 6.2";
				}
			}
			return new CursorLoader(EarthquakeDetailsScreen.this, CachedEarthquakeDataTable.CONTENT_URI, projections, selection, where, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				ListView listview = (ListView) findViewById(R.id.listview);
				SimpleCursorAdapter adapter = (SimpleCursorAdapter) listview.getAdapter();
				adapter.swapCursor(cursor);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> cursor) {
			ListView listview = (ListView) findViewById(R.id.listview);
			SimpleCursorAdapter adapter = (SimpleCursorAdapter) listview.getAdapter();
			adapter.swapCursor(null);

		}

	};
	/*
	 * Loader to load single earthquake object by its id.
	 */
	LoaderManager.LoaderCallbacks<Cursor> loadEarthquake = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle data) {
			if (data == null || !data.containsKey(BUNDLE_KEY_MULTI_SEARCH_TEXT))
				return null;
			String[] projections = { "*" };
			String selection = CachedEarthquakeDataTable.EARTHQUAKE_ID + " = ?";
			String[] where = { data.getString(BUNDLE_KEY_MULTI_SEARCH_TEXT) };
			return new CursorLoader(EarthquakeDetailsScreen.this, CachedEarthquakeDataTable.CONTENT_URI, projections, selection, where, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {

				cursor.moveToFirst();
				Earthquake eq = DataFormatHelper.buildEarthquakeFromCursor(cursor);
				if (eq != null)
					setupSingleEarthquakeData(eq);

			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {

		}

	};
}
