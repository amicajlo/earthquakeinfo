package com.haidang.sampleapp.earthquake.util;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class PhoneSettingPickerUtil {
	
	public static void pickWifiSetting(Activity activity){
		activity.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
	}
}
