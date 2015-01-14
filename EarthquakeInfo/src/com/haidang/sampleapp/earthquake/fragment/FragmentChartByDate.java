package com.haidang.sampleapp.earthquake.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.haidang.sampleapp.earthquake.Earthquake;
import com.haidang.sampleapp.earthquake.LoaderIdConstants;
import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.activity.EarthquakeDetailsScreen;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;
import com.haidang.sampleapp.earthquake.util.DataFormatHelper;

/**
 * @author Hai dang
 *
 */

public class FragmentChartByDate extends FragmentScreen {
	private View view;
	private BarChart mChart;
	private Map<String, List<Earthquake>> eqByDateMap = new HashMap<String, List<Earthquake>>();

	public FragmentChartByDate() {
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setHasOptionsMenu(true);
	}

	public static FragmentChartByDate newInstance(Bundle bundle) {
		FragmentChartByDate fragment = new FragmentChartByDate();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chart_by_date, container, false);
		setupView();
		return view;
	}

	private void setupView() {
		if (view == null)
			return;

		mChart = (BarChart) view.findViewById(R.id.chart1);

		mChart.setDrawYValues(false);

		mChart.setUnit("");
		mChart.setDescription("");

		mChart.setDrawYValues(true);

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		mChart.setMaxVisibleValueCount(60);

		// disable 3D
		mChart.set3DEnabled(false);
		// scaling can now only be done on x- and y-axis separately
		mChart.setPinchZoom(false);

		mChart.setDrawBarShadow(false);

		mChart.setDrawVerticalGrid(false);
		mChart.setDrawHorizontalGrid(false);
		mChart.setDrawGridBackground(false);

		XLabels xLabels = mChart.getXLabels();
		xLabels.setPosition(XLabelPosition.BOTTOM);
		xLabels.setCenterXLabelText(true);
		xLabels.setSpaceBetweenLabels(0);

		mChart.setDrawYLabels(false);
		mChart.setDrawLegend(false);

		mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

			@Override
			public void onValueSelected(Entry e, int dataSetIndex) {
				BarData data = (BarData) mChart.getData();
				List<String> xVals = data.getXVals();
				if (e.getXIndex() < xVals.size()) {
					Intent intent = new Intent(getActivity(), EarthquakeDetailsScreen.class);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_DISPLAY_TYPE, EarthquakeDetailsScreen.TYPE_MULTI);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_MULTI_TYPE, EarthquakeDetailsScreen.MULTITYPE_DATE);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_MULTI_SEARCH_TEXT, xVals.get(e.getXIndex()));
					startActivity(intent);

				}
			}

			@Override
			public void onNothingSelected() {

			}
		});

		getActivity().getSupportLoaderManager().restartLoader(LoaderIdConstants.LOADER_ID_CHART_DATE_LOADDATA, null, earthquakeLoader);

	}
	
	/**
	 * This will be called when the Loader finishes loading data and save data to a map grouped by date
	 * Each bar will have value from the group size and label from the group date.
	 */
	private void setData() {
		if (eqByDateMap == null || eqByDateMap.isEmpty())
			return;

		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		SortedSet<String> keys = new TreeSet<String>(eqByDateMap.keySet());

		int i = 0;
		for (String date : keys) {
			yVals1.add(new BarEntry(eqByDateMap.get(date).size(), i));
			i++;
		}

		ArrayList<String> xVals = new ArrayList<String>();

		for (String date : keys) {
			try {
				xVals.add(String.valueOf(date));
			} catch (Exception e) {
				e.printStackTrace();
				xVals.add("No");
			}
		}

		BarDataSet set1 = new BarDataSet(yVals1, "Data Set");
		set1.setColors(ColorTemplate.VORDIPLOM_COLORS);

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);

		BarData data = new BarData(xVals, dataSets);
		mChart.setData(data);
		mChart.invalidate();

		mChart.animateY(2500);

	}

	@Override
	public int getFragmentId() {
		return FRAGMENT_ID_CHART_DATE;
	}
	/**
	 * Load all earthquake data from Database, group them by date then feed the BarChart
	 *
	 */
	LoaderManager.LoaderCallbacks<Cursor> earthquakeLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			String[] projections = { "*" };
			return new CursorLoader(getActivity(), CachedEarthquakeDataTable.CONTENT_URI, projections, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				eqByDateMap.clear();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					Earthquake eq = DataFormatHelper.buildEarthquakeFromCursor(cursor);
					if (eq != null) {
						try {
							String dateWOTime = DataFormatHelper.getDateString(eq.timedate);
							if (eqByDateMap.containsKey(dateWOTime)) {
								eqByDateMap.get(dateWOTime).add(eq);
							} else {
								List<Earthquake> earthquakes = new ArrayList<Earthquake>();
								earthquakes.add(eq);
								eqByDateMap.put(dateWOTime, earthquakes);
							}
						} catch (Exception e) {
						}
					}
				}

				setData();
			}

		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {

		}

	};

}
