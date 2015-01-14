package com.haidang.sampleapp.earthquake;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author Hai Dang
 *
 */
public class EarthquakeInfoApp extends Application {

	private static EarthquakeInfoApp _instance;

	public static EarthquakeInfoApp getInstance() {
		if (_instance == null)
			_instance = new EarthquakeInfoApp();

		return _instance;
	}

	@Override
	public void onCreate() {
		_instance = this;
	}
	

	public boolean isNetworkConnected() {
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			for (int i = 0; i < 4; i++) {
				NetworkInfo netInfo = null;
				if (i == 0)
					netInfo = cm
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				else if (i == 1)
					netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				else if (i == 2)
					netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
				else if (i == 3)
					netInfo = cm
							.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public int getScreenWidth() {
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	public int getScreenHeight() {
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}
}
