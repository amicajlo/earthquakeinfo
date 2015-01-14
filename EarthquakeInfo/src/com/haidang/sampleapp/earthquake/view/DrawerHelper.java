package com.haidang.sampleapp.earthquake.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.activity.HomeScreen;
import com.haidang.sampleapp.earthquake.activity.Screen;
import com.haidang.sampleapp.earthquake.fragment.FragmentChartByDate;
import com.haidang.sampleapp.earthquake.fragment.FragmentChartByMagnitude;
import com.haidang.sampleapp.earthquake.fragment.FragmentEarthquakeMap;
import com.haidang.sampleapp.earthquake.fragment.FragmentScreen;
import com.haidang.sampleapp.earthquake.view.DrawerListItemInterface.DrawerListItemAction;

/**
 * @author Hai Dang
 * 
 * This utility class is for managing Drawer action on HomeScreen
 *
 */
public class DrawerHelper {
	/**
	 * Find a correct fragment and display it for a given drawer menu item
	 * This is called by HomeScreen when it needs to open an app's section by itself (without user tapping on drawer item) 
	 * 
	 */
	public static void selectMenuItem(HomeScreen context, int type, DrawerListAdapter adapter, Bundle bundle) {
		int layoutId = R.id.content_frame;
		displayAssocciatedFragment(context, type, layoutId);
		selectMenuItem(adapter, type);
	}

	/**
	 * This is called by HomeScreen when it needs to open an app's section by itself (without user tapping on drawer item) 
	 * 
	 */
	public static void selectMenuItem(FragmentScreen fragment, DrawerListAdapter adapter) {
		selectMenuItem(adapter, findSelectedItemForFragment(fragment));
	}

	private static void selectMenuItem(DrawerListAdapter adapter, int itemType) {
		switch (itemType) {
		case DrawerListItem.MAP: {
			adapter.setCurrentSelectedPosition(0);
			break;
		}
		case DrawerListItem.CHART_DATE: {
			adapter.setCurrentSelectedPosition(1);
			break;
		}
		case DrawerListItem.CHART_LOCATION: {
			adapter.setCurrentSelectedPosition(2);
			break;
		}

		case DrawerListItem.CHART_MAGNITUDE: {
			adapter.setCurrentSelectedPosition(2);
			break;
		}
		default: {
			break;
		}
		}
	}

	private static int findSelectedItemForFragment(FragmentScreen fragment) {
		switch (fragment.getFragmentId()) {
		case FragmentScreen.FRAGMENT_ID_MAP: {
			return DrawerListItem.MAP;
		}
		case FragmentScreen.FRAGMENT_ID_CHART_LOCATION: {
			return DrawerListItem.CHART_LOCATION;
		}
		case FragmentScreen.FRAGMENT_ID_CHART_DATE: {
			return DrawerListItem.CHART_DATE;
		}
		case FragmentScreen.FRAGMENT_ID_CHART_MAGNITUDE: {
			return DrawerListItem.CHART_MAGNITUDE;
		}
		default: {
			return DrawerListItem.MAP;
		}
		}
	}

	private static FragmentScreen findFragmentForSelectedItem(Screen screen, int itemType) {
		FragmentScreen fragment = null;
		if (itemType == DrawerListItem.MAP) {
			fragment = FragmentEarthquakeMap.newInstance(null);
		} else if (itemType == DrawerListItem.CHART_DATE) {
			fragment = FragmentChartByDate.newInstance(null);
		} else if (itemType == DrawerListItem.CHART_LOCATION) {

		} else if (itemType == DrawerListItem.CHART_MAGNITUDE) {
			fragment = FragmentChartByMagnitude.newInstance(null);
		}

		return fragment;
	}

	private static void displayAssocciatedFragment(HomeScreen activity, int type, int layoutId) {
		FragmentScreen fragment = findFragmentForSelectedItem(activity, type);
		if (fragment != null) {
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			if (activity.getCurrentFragment() != null) {
				FragmentScreen currentFragment = activity.getCurrentFragment();
				if (!fragment.getClass().equals(currentFragment.getClass())) {
					// if the current fragment is stateless then remove it

					if (currentFragment.shouldStateRetained())
						fragmentTransaction.hide((Fragment)currentFragment);
					else {
						fragmentTransaction.remove((Fragment)currentFragment);
						activity.setCurrentFragment(null);
					}

					// If this fragment has not been added before
					if (fragmentManager.findFragmentByTag(String.valueOf(fragment.getFragmentId())) == null) {

						fragmentTransaction.add(layoutId, ((Fragment) fragment), String.valueOf(fragment.getFragmentId()));
						activity.setCurrentFragment(fragment);
					} else {
						Fragment existingFragment = fragmentManager.findFragmentByTag(String.valueOf(fragment.getFragmentId()));
						fragmentTransaction.show(existingFragment);
						activity.setCurrentFragment((FragmentScreen) existingFragment);
					}
				}
			} else {
				fragmentTransaction.replace(layoutId, ((Fragment) fragment));
				activity.setCurrentFragment(fragment);
			}

			fragmentTransaction.commitAllowingStateLoss();
			// activity.setCurrentActiveMenuItem(type);

		}
	}
	/**
	 * Populate data for App Drawer, give earch item an action on user's click too
	 * 
	 */
	public static List<DrawerListItemInterface> populateDrawerListItems(final HomeScreen activity, final int layoutId) {
		DrawerListItemAction action = new DrawerListItemAction() {

			@Override
			public void onItemSelectAction(DrawerListItemInterface item) {
				displayAssocciatedFragment(activity, item.getType(), layoutId);
			}
		};

		List<DrawerListItemInterface> accountSectionRowList = new ArrayList<DrawerListItemInterface>();

		DrawerListItemInterface homeItem = new DrawerListItem(R.drawable.nav_map, null, DrawerListItem.MAP);
		homeItem.setOnItemSelectAction(action);
		accountSectionRowList.add(homeItem);

		DrawerListItemInterface specialsItem = new DrawerListItem(R.drawable.nav_chart_date, null, DrawerListItem.CHART_DATE);
		specialsItem.setOnItemSelectAction(action);
		accountSectionRowList.add(specialsItem);

		DrawerListItemInterface faveProductsItem = new DrawerListItem(R.drawable.nav_chart_magnitude, null, DrawerListItem.CHART_MAGNITUDE);
		faveProductsItem.setOnItemSelectAction(action);
		accountSectionRowList.add(faveProductsItem);

		return accountSectionRowList;
	}
}
