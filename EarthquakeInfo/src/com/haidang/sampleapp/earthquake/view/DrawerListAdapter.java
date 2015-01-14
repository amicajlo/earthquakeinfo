package com.haidang.sampleapp.earthquake.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haidang.sampleapp.earthquake.R;

public class DrawerListAdapter extends ArrayAdapter<DrawerListItemInterface> {
	private View currentSelectedView;
	private int currentSelectedPosition = 0;

	public DrawerListAdapter(Context context, int resource) {
		super(context, resource);
	}

	public DrawerListAdapter(Context context, int textViewResourceId,
			DrawerListItemInterface[] objects) {
		super(context, textViewResourceId, objects);
	}

	public DrawerListAdapter(Context context, int resource,
			int textViewResourceId, DrawerListItemInterface[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public DrawerListAdapter(Context context, int resource,
			int textViewResourceId, List<DrawerListItemInterface> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public DrawerListAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public DrawerListAdapter(Context context, int textViewResourceId,
			List<DrawerListItemInterface> objects) {
		super(context, textViewResourceId, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.drawer_listview_item,
					null);
		}
		convertView.setBackgroundResource(R.drawable.state_white_grey);

		DrawerListItemInterface rowData = getItem(position);

		TextView text = (TextView) convertView
				.findViewById(R.id.drawer_listview_item_text);
		text.setText(rowData.getText());
		ImageView image = (ImageView) convertView
				.findViewById(R.id.drawer_listview_item_icon);
		ViewGroup imageLayout = (ViewGroup) convertView
				.findViewById(R.id.drawer_listview_item_icon_panel);
		if (rowData.getIconResource() != 1) {
			imageLayout.setVisibility(View.VISIBLE);
			image.setImageResource(rowData.getIconResource());
		} else {
			imageLayout.setVisibility(View.GONE);
		}

		if (currentSelectedPosition == position) {
			convertView.setBackgroundResource(R.drawable.state_grey_white);
			this.currentSelectedView = convertView;
		}
		return convertView;
	}

	public void setCurrentSelectedView(View currentSelectedView) {
		this.currentSelectedView = currentSelectedView;
	}

	public View getCurrentSelectedView() {
		return currentSelectedView;
	}
	
	
	public void setCurrentSelectedPosition(int currentSelectedPosition) {
		this.currentSelectedPosition = currentSelectedPosition;
		notifyDataSetChanged();
	}

	public int getCurrentSelectedPosition() {
		return currentSelectedPosition;
	}
}
