package com.haidang.sampleapp.earthquake.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.haidang.sampleapp.earthquake.Earthquake;
import com.haidang.sampleapp.earthquake.LoaderIdConstants;
import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.activity.EarthquakeDetailsScreen;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;
import com.haidang.sampleapp.earthquake.util.DataFormatHelper;

/**
 * @author Hai Dang
 * Display Earthquake data on a PieChart, categorized by Magnitude scale : Light, Moderate, Strong and Major
 */
public class FragmentChartByMagnitude extends FragmentScreen {
	private View view;

	private PieChart mChart;
	public static String[] SCALE = new String[] { "Light", "Moderate", "Strong", "Major" };
	private List<MagnitudeScale> magnitudeTypes;

	public FragmentChartByMagnitude() {
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setHasOptionsMenu(true);
	}

	public static FragmentChartByMagnitude newInstance(Bundle bundle) {
		FragmentChartByMagnitude fragment = new FragmentChartByMagnitude();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_chart_by_magnitude, container, false);

		setupView();
		return view;
	}

	private void setupView() {
		if (view == null)
			return;

		mChart = (PieChart) view.findViewById(R.id.chart1);

		// change the color of the center-hole
		mChart.setHoleColor(Color.rgb(235, 235, 235));

		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");

		mChart.setValueTypeface(tf);
		mChart.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf"));

		mChart.setHoleRadius(60f);

		mChart.setDescription("");

		mChart.setDrawYValues(true);
		mChart.setDrawCenterText(false);

		mChart.setRotationAngle(0);

		// draws the corresponding description value into the slice
		mChart.setDrawXValues(true);

		// enable rotation of the chart by touch
		mChart.setRotationEnabled(true);

		// display percentage values
		mChart.setUsePercentValues(false);
		// mChart.setDrawUnitsInChart(true);

		// add a selection listener
		// mChart.setTouchEnabled(false);

		mChart.setCenterText("Earthquakes magnitude");

		mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

			@Override
			public void onValueSelected(Entry e, int dataSetIndex) {
				PieData data = mChart.getData();
				List<String> xVals = data.getXVals();
				if (e.getXIndex() < xVals.size()) {
					Intent intent = new Intent(getActivity(), EarthquakeDetailsScreen.class);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_DISPLAY_TYPE, EarthquakeDetailsScreen.TYPE_MULTI);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_MULTI_TYPE, EarthquakeDetailsScreen.MULTITYPE_MAG_SCALE);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_MULTI_SEARCH_TEXT, xVals.get(e.getXIndex()));
					startActivity(intent);
				}
			}

			@Override
			public void onNothingSelected() {
				// TODO Auto-generated method stub

			}
		});

		getActivity().getSupportLoaderManager().restartLoader(LoaderIdConstants.LOADER_ID_CHART_MAGNITUDE_LOADDATA, null, earthquakeLoader);

	}
	/**
	 * This will be called when the Loader finishes loading data and save data to a list of magnitude object.
	 * Each item in that list will be displayed as a piece of the Pie chart.
	 */
	private void setData() {
		if (magnitudeTypes == null || magnitudeTypes.isEmpty())
			return;

		ArrayList<Entry> yVals1 = new ArrayList<Entry>();

		// IMPORTANT: In a PieChart, no values (Entry) should have the same
		// xIndex (even if from different DataSets), since no values can be
		// drawn above each other.
		for (int i = 0; i < magnitudeTypes.size(); i++) {
			yVals1.add(new Entry((float) magnitudeTypes.get(i).earthquakes.size(), i));
		}

		ArrayList<String> xVals = new ArrayList<String>();

		for (MagnitudeScale mType : magnitudeTypes)
			if (mType.type == Earthquake.Type.LIGHT) {
				xVals.add(SCALE[0]);
			} else if (mType.type == Earthquake.Type.MODERATE) {
				xVals.add(SCALE[1]);
			} else if (mType.type == Earthquake.Type.STRONG) {
				xVals.add(SCALE[2]);
			} else if (mType.type == Earthquake.Type.MAJOR) {
				xVals.add(SCALE[3]);
			}

		PieDataSet set1 = new PieDataSet(yVals1, "Magnitude scale");
		set1.setSliceSpace(3f);

		// add a lot of colors

		ArrayList<Integer> colors = new ArrayList<Integer>();


		colors.add(getResources().getColor(R.color.color_scale_light));
		colors.add(getResources().getColor(R.color.color_scale_moderate));
		colors.add(getResources().getColor(R.color.color_scale_strong));
		colors.add(getResources().getColor(R.color.color_scale_major));

		set1.setColors(colors);

		PieData data = new PieData(xVals, set1);
		mChart.setData(data);

		// undo all highlights
		mChart.highlightValues(null);

		mChart.setDrawHoleEnabled(false);

		mChart.invalidate();

		mChart.animateXY(1500, 1500);
		// mChart.spin(2000, 0, 360);

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.RIGHT_OF_CHART);
		l.setXEntrySpace(7f);
		l.setYEntrySpace(5f);

	}

	@Override
	public int getFragmentId() {
		return FRAGMENT_ID_CHART_MAGNITUDE;
	}

	/**
	 * Load all earthquake data from Database, convert them to a list of earthquake magnitude scale objects 
	 * then feed the piechart
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
				MagnitudeScale small = new MagnitudeScale();
				MagnitudeScale medium = new MagnitudeScale();
				MagnitudeScale large = new MagnitudeScale();
				MagnitudeScale extreme = new MagnitudeScale();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					Earthquake eq = DataFormatHelper.buildEarthquakeFromCursor(cursor);
					if (eq != null) {
						if (eq.getType() == Earthquake.Type.LIGHT) {
							small.earthquakes.add(eq);
							small.type = Earthquake.Type.LIGHT;
						} else if (eq.getType() == Earthquake.Type.MODERATE) {
							medium.earthquakes.add(eq);
							medium.type = Earthquake.Type.MODERATE;
						} else if (eq.getType() == Earthquake.Type.STRONG) {
							large.earthquakes.add(eq);
							large.type = Earthquake.Type.STRONG;
						} else if (eq.getType() == Earthquake.Type.MAJOR) {
							extreme.earthquakes.add(eq);
							extreme.type = Earthquake.Type.MAJOR;
						}
					}
				}

				magnitudeTypes = new ArrayList<FragmentChartByMagnitude.MagnitudeScale>();
				magnitudeTypes.add(small);
				magnitudeTypes.add(medium);
				magnitudeTypes.add(large);
				magnitudeTypes.add(extreme);
				setData();
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {

		}

	};

	private static class MagnitudeScale {
		Earthquake.Type type = Earthquake.Type.LIGHT;
		List<Earthquake> earthquakes = new ArrayList<Earthquake>();
	}

}
