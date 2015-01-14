package com.haidang.sampleapp.earthquake.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haidang.sampleapp.earthquake.Earthquake;
import com.haidang.sampleapp.earthquake.LoaderIdConstants;
import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.activity.EarthquakeDetailsScreen;
import com.haidang.sampleapp.earthquake.database.table.CachedEarthquakeDataTable;
import com.haidang.sampleapp.earthquake.util.DataFormatHelper;

/**
 * @author Hai Dang
 * Display earthquake data in a Google map
 */
public class FragmentEarthquakeMap extends FragmentScreen {
	private static View view;
	private GoogleMap mMap;

	private CameraUpdate cu;
	private boolean cameraAnimationEnabled = true;
	private boolean isReturned = false;
	private Map<String, Earthquake> earthquakes = new HashMap<String, Earthquake>();
	private static final String SEPARATOR = "_@_HAIDANG_@_";

	public FragmentEarthquakeMap() {
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setHasOptionsMenu(true);
	}

	public static FragmentEarthquakeMap newInstance(Bundle bundle) {
		FragmentEarthquakeMap fragment = new FragmentEarthquakeMap();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_earthquake_map, container, false);
		} catch (android.view.InflateException e) {
			e.printStackTrace();
			/* map is already there, just return view as it is */
			isReturned = true;
		}

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (view == null)
			return;

		view.findViewById(R.id.progress_bar).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.locate_us_map)).getMap();

		if (mMap == null) {
			int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
			if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
				GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, this.getActivity(), 1).show();
				return;
			}
		}

		if (isReturned && earthquakes.size() == 0)
			mMap.clear();

		mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
			float currentZoom = -1;

			@Override
			public void onCameraChange(CameraPosition cPos) {
				if (currentZoom == -1) {
					currentZoom = cPos.zoom;
				} else {
					if (cPos.zoom < currentZoom || cPos.zoom == currentZoom) {
						int distance = (int) calculateRadius() / 2000;
						if (distance < 1)
							distance = 1;

					}
					currentZoom = cPos.zoom;
				}

			}

		});
		mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			@Override
			public View getInfoContents(Marker clickedMarker) {
				// marker id is mxxxx, where xxxx can be use as position
				// in items.

				View v = getActivity().getLayoutInflater().inflate(R.layout.map_pin, null);
				v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				TextView titleView = (TextView) v.findViewById(R.id.locate_us_map_pin_title);
				TextView addressView = (TextView) v.findViewById(R.id.locate_us_map_pin_address);
				String snippet = clickedMarker.getSnippet();
				String[] snippets = snippet.split(SEPARATOR);
				if (snippets != null && snippets.length == 2) {
					addressView.setText("Magnitude: " + snippets[1]);
					titleView.setText(snippets[0]);
				}

				return v;
			}
		});
		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker clickedMarker) {
				if (earthquakes == null || earthquakes.isEmpty())
					return;
				String eqId = String.valueOf(clickedMarker.getTitle().trim());
				Earthquake earthquake = earthquakes.get(eqId);
				if (earthquake != null) {
					Intent intent = new Intent(getActivity(), EarthquakeDetailsScreen.class);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_DISPLAY_TYPE, EarthquakeDetailsScreen.TYPE_SINGLE);
					intent.putExtra(EarthquakeDetailsScreen.BUNDLE_KEY_MULTI_SEARCH_TEXT, earthquake.eqid);
					startActivity(intent);

				}
			}
		});

		getActivity().getSupportLoaderManager().restartLoader(LoaderIdConstants.LOADER_ID_MAP_LOADDATA, null, earthquakeLoader);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (getActivity() != null)
			this.getActivity().getSupportLoaderManager().destroyLoader(LoaderIdConstants.LOADER_ID_MAP_LOADDATA);
	}

	private void plotPins(final List<Earthquake> items) {
		if (view == null)
			return;
		if (items != null && items.size() > 0) {
			earthquakes.clear();
			ArrayList<LatLng> llMarkers = new ArrayList<LatLng>();
			// double nearestDistance = -1;
			for (int i = 0; i < items.size(); i++) {
				Earthquake earthquake = items.get(i);
				LatLng ll = null;
				ll = new LatLng(earthquake.lat, earthquake.lon);
				earthquakes.put(earthquake.eqid, earthquake);
				int markerDrawable = R.drawable.map_pin_light;
				Earthquake.Type type = earthquake.getType();
				if (type == Earthquake.Type.MODERATE) {
					markerDrawable = R.drawable.map_pin_moderate;
				} else if (type == Earthquake.Type.STRONG) {
					markerDrawable = R.drawable.map_pin_strong;
				} else if (type == Earthquake.Type.MAJOR) {
					markerDrawable = R.drawable.map_pin_major;
				}
				mMap.addMarker(new MarkerOptions().position(ll).title(String.valueOf(earthquake.eqid))
						.snippet(earthquake.region + SEPARATOR + earthquake.magnitude)
						.icon(BitmapDescriptorFactory.fromResource(markerDrawable)));

				if (ll != null)
					llMarkers.add(ll);

			}

			if (llMarkers.size() > 0) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (LatLng m : llMarkers) {
					builder.include(m);
				}
				LatLngBounds bounds = builder.build();
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int padding = displaymetrics.heightPixels / 10; // offset from
				// edges of
				// the map in
				// pixels
				cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				if (cameraAnimationEnabled) {
					cameraAnimationEnabled = false;
					try {
						mMap.animateCamera(cu);
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private double calculateRadius() {
		return distance(mMap.getProjection().getVisibleRegion().latLngBounds.northeast,
				mMap.getProjection().getVisibleRegion().latLngBounds.southwest);
	}

	private double distance(LatLng StartP, LatLng EndP) {
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return 6366000 * c;
	}

	@Override
	public int getFragmentId() {
		return FRAGMENT_ID_MAP;
	}

	LoaderManager.LoaderCallbacks<Cursor> earthquakeLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			String[] projections = { "*" };

			return new CursorLoader(getActivity(), CachedEarthquakeDataTable.CONTENT_URI, projections, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				List<Earthquake> earthquakeList = new ArrayList<Earthquake>();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					Earthquake eq = DataFormatHelper.buildEarthquakeFromCursor(cursor);
					if (eq != null)
						earthquakeList.add(eq);
				}
				plotPins(earthquakeList);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub

		}

	};
}
