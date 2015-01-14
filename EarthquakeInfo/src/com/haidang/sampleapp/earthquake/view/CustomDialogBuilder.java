package com.haidang.sampleapp.earthquake.view;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Hai Dang
 * 
 *         Utility class to quickly build and display standard dialogs. It has
 *         action associated with buttons on these dialog
 * 
 */
public class CustomDialogBuilder {

	public interface AlertDialogAction {
		public void onActionButtonPressed();

		public void onCancelButtonPressed();
	}

	public interface AlertDialogInputAction {
		public void onActionButtonPressed(DialogInterface dialog, EditText editTextView);
	}

	public interface AlertDialogListItemSelectAction {
		public void onListItemSelected(DialogInterface dialog, String itemText, int pos);

		public void onDialogCanceled(DialogInterface dialog);
	}

	/**
	 * @param context
	 * @param title
	 * @param message
	 * @param actionLabel
	 *            label for positive button
	 * @param cancelTLabel
	 *            label for negative button
	 * @param action
	 *            actions for both buttons on click event
	 * @return AlertDialog
	 */
	public static Dialog displayDialogTwoBtn(Context context, String title, String message, String actionLabel, String cancelTLabel,
			final AlertDialogAction action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message).setCancelable(false).setPositiveButton(actionLabel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				action.onActionButtonPressed();
			}
		}).setNegativeButton(cancelTLabel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				action.onCancelButtonPressed();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
		return alertDialog;
	}

	/**
	 * display 2 buttons dialogs with flag to set whether dialog is cancelable
	 * by tapping back button
	 */
	public static Dialog displayDialogTwoBtn(Context context, String title, String message, String actionLabel, String cancelTLabel,
			boolean cancelable, final AlertDialogAction action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message).setCancelable(cancelable).setPositiveButton(actionLabel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				action.onActionButtonPressed();
			}
		}).setNegativeButton(cancelTLabel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				action.onCancelButtonPressed();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
		return alertDialog;
	}

	/**
	 * display 2 buttons dialogs with an inputbox to text the input
	 */
	public static Dialog displayInputTextDialogTwoBtn(final Context context, String title, String message, String actionLabel, String cancelTLabel,
			final boolean autoDismiss, final AlertDialogInputAction action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		input.setText(message);
		input.setPadding(20, 20, 20, 20);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setView(input);
		alertDialogBuilder.setCancelable(false).setPositiveButton(actionLabel, null);
		alertDialogBuilder.setNegativeButton(cancelTLabel, null);

		final AlertDialog dialog = alertDialogBuilder.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface d) {
				Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

				positive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (autoDismiss)
							dialog.dismiss();
						action.onActionButtonPressed(dialog, input);

					}
				});
				negative.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
					}
				});
			}
		});
		input.selectAll();
		dialog.show();
		return dialog;
	}

	/**
	 * One button dialog - usually used to display a message that does not require user input/action
	 */
	public static Dialog displayDialogOneBtn(Context context, String title, String message, String cancelTLabel, final AlertDialogAction action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message).setCancelable(false).setNegativeButton(cancelTLabel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				if (action != null)
					action.onCancelButtonPressed();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
		return alertDialog;
	}
	/**
	 * One button dialog - usually used to display a list of simple Text
	 */
	public static Dialog displayDialogListView(Context context, String title, final List<CharSequence> list,
			final AlertDialogListItemSelectAction action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		CharSequence[] items = new CharSequence[list.size()];
		for (int i = 0; i < items.length; i++)
			items[i] = list.get(i);
		alertDialogBuilder.setCancelable(true).setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				action.onListItemSelected(dialog, list.get(which).toString(), which);
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				action.onDialogCanceled(dialog);
			}
		});
		alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				action.onDialogCanceled(dialog);
			}
		});
		alertDialog.show();
		return alertDialog;
	}
}
