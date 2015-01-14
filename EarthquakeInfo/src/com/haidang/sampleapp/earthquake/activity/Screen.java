package com.haidang.sampleapp.earthquake.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.haidang.sampleapp.earthquake.R;
import com.haidang.sampleapp.earthquake.util.PhoneSettingPickerUtil;
import com.haidang.sampleapp.earthquake.view.CustomDialogBuilder;

/**
 * @author Hai Dang
 *This is parent class of all Activity classes used in this app.
 *Contain some common task: display message dialog, error dialog...
 */
public class Screen extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homescreen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}
	
	private Dialog noNetworkAlertDialog;

	public void displayNetworkNotAvailableAlert() {
		displayNetworkNotAvailable(false);
	}

	public void displayNetworkNotAvailableAlert(boolean shouldCloseWindow) {
		displayNetworkNotAvailable(shouldCloseWindow);
	}

	private void displayNetworkNotAvailable(final boolean shouldCloseWindow) {
		if ((noNetworkAlertDialog == null || !noNetworkAlertDialog.isShowing()) && !isFinishing()) {
			noNetworkAlertDialog = CustomDialogBuilder.displayDialogTwoBtn(Screen.this, getResources().getString(R.string.message_no_connection),
					getResources().getString(R.string.message_must_connect_network), getResources().getString(R.string.message_confignetwork),
					getResources().getString(R.string.message_close_dialog), new CustomDialogBuilder.AlertDialogAction() {
						@Override
						public void onCancelButtonPressed() {
							if (shouldCloseWindow)
								finish();
						}

						@Override
						public void onActionButtonPressed() {
							PhoneSettingPickerUtil.pickWifiSetting(Screen.this);
						}
					});
		} else {
			if (!noNetworkAlertDialog.isShowing())
				noNetworkAlertDialog.show();
		}
	}
	
	protected void displayJobFail() {
		CustomDialogBuilder.displayDialogOneBtn(Screen.this, getResources().getString(R.string.message_oops), getResources().getString(R.string.message_network_technicall_issue),
				"Ok", null);
	}
}
