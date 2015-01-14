package com.haidang.sampleapp.earthquake.activity;

import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.fragment.FragmentScreen;
import com.haidang.sampleapp.earthquake.view.CustomDialogBuilder;
import com.haidang.sampleapp.earthquake.view.DrawerHelper;
import com.haidang.sampleapp.earthquake.view.DrawerListAdapter;
import com.haidang.sampleapp.earthquake.view.DrawerListItem;
import com.haidang.sampleapp.earthquake.view.DrawerListItemInterface;

/**
 * @author Hai Dang
 * This is the main activity which contains the App navigation drawer.
 * Each drawer item is associated with a Fragment. HomeScreen uses DrawerHelper to navigate between Fragments.
 * 
 */
public class HomeScreen extends Screen {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ViewGroup drawerLayout;
	private FragmentScreen currentFragment;
	private static final String SAVED_BUNDLE_ACTIVE_FRAGMENT_ID = "SAVED_BUNDLE_ACTIVE_FRAGMENT_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homescreen);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer_listview);
		drawerLayout = (ViewGroup) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		DrawerListAdapter adapter = new DrawerListAdapter(this, R.layout.drawer_listview_item, DrawerHelper.populateDrawerListItems(this,
				R.id.content_frame));
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DrawerListAdapter adapter = (DrawerListAdapter) parent.getAdapter();
				DrawerListItemInterface item = adapter.getItem(position);
				item.selectItem();
				adapter.setCurrentSelectedPosition(position);
				mDrawerLayout.closeDrawers();
			}
		});

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(HomeScreen.this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {

			}

			public void onDrawerOpened(View drawerView) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDrawerLayout.getWindowToken(), 0);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			openMenuItem(DrawerListItem.MAP);

		} else {
			int id = savedInstanceState.getInt(SAVED_BUNDLE_ACTIVE_FRAGMENT_ID);
			FragmentManager fragmentManager = getSupportFragmentManager();
			List<Fragment> fragmentList = fragmentManager.getFragments();
			if (fragmentList != null && !fragmentList.isEmpty()) {
				FragmentTransaction fragmentTrans = fragmentManager.beginTransaction();
				for (Fragment fragment : fragmentList) {
					if (fragment != null) {
						if (fragment instanceof FragmentScreen) {
							FragmentScreen FragmentInterface = (FragmentScreen) fragment;
							if (FragmentInterface.getFragmentId() == id) {
								DrawerHelper.selectMenuItem(FragmentInterface, adapter);
								fragmentTrans.show((Fragment) FragmentInterface);
								this.setCurrentFragment(FragmentInterface);
							} else {
								fragmentTrans.hide((Fragment) FragmentInterface);
							}
						}

					}
				}
				fragmentTrans.commit();
			} else {
				openMenuItem(DrawerListItem.MAP);
			}
			onNewIntent(getIntent());
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(SAVED_BUNDLE_ACTIVE_FRAGMENT_ID, currentFragment.getFragmentId());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public FragmentScreen getCurrentFragment() {
		return currentFragment;
	}

	public void setCurrentFragment(FragmentScreen fragment) {
		this.currentFragment = fragment;
	}

	public void openMenuItem(int menuItemType) {
		openMenuItem(menuItemType, null);
	}

	public void openMenuItem(int menuItemType, Bundle bundle) {
		DrawerHelper.selectMenuItem(HomeScreen.this, menuItemType, (DrawerListAdapter) mDrawerList.getAdapter(), bundle);
		mDrawerLayout.closeDrawer(drawerLayout);
	}

	@Override
	public void onBackPressed() {
		CustomDialogBuilder.displayDialogTwoBtn(this, getResources().getString(R.string.message_exit_app), getResources().getString(R.string.message_exit_app_confirm), getResources().getString(R.string.message_label_confirm), getResources().getString(R.string.message_label_decline),
				new CustomDialogBuilder.AlertDialogAction() {
					@Override
					public void onCancelButtonPressed() {
					}

					@Override
					public void onActionButtonPressed() {
						HomeScreen.super.onBackPressed();
					}
				});

	}
}
