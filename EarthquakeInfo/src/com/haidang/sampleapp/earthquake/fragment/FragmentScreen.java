package com.haidang.sampleapp.earthquake.fragment;

import android.support.v4.app.Fragment;

/**
 * @author Hai Dang
 * Parent class for all Fragments used in this app. Provide custom Fragment ID so it can be managed by DrawerHelper
 */
public abstract class FragmentScreen extends Fragment{
	public static final int FRAGMENT_ID_MAP = 101;
	public static final int FRAGMENT_ID_CHART_DATE = 102;
	public static final int FRAGMENT_ID_CHART_LOCATION = 103;
	public static final int FRAGMENT_ID_CHART_MAGNITUDE = 104;
	public abstract int getFragmentId();
	public boolean shouldStateRetained() {
		return true;
	}
}
