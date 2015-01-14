package com.haidang.sampleapp.earthquake.view;

import android.app.Application;

import com.haidang.sampleapp.earthquake.EarthquakeInfoApp;
import com.haidang.sampleapp.earthquake.R;

public class DrawerListItem implements DrawerListItemInterface {
	public static final int MAP = 0;
	public static final int CHART_DATE = 1;
	public static final int CHART_LOCATION = 2;
	public static final int CHART_MAGNITUDE = 3;

	private int iconResource;
	private String text;
	private Object status;
	private int type;
	private DrawerListItemAction action;

	public DrawerListItem(int iconResource, String text, Object status) {
		super();
		this.iconResource = iconResource;
		this.text = text;
		this.status = status;
	}

	public DrawerListItem(int iconResource, String text, Object status, int type) {
		super();
		this.iconResource = iconResource;
		this.text = text;
		this.status = status;
		this.type = type;
	}

	public DrawerListItem(int iconResource, Object status, int type) {
		super();
		this.iconResource = iconResource;
		this.text = getMenuItemName(type);
		this.status = status;
		this.type = type;
	}

	@Override
	public void selectItem() {
		if (action != null)
			action.onItemSelectAction(this);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIconResource() {
		return iconResource;
	}

	public void setIconResource(int iconResource) {
		this.iconResource = iconResource;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Object getStatus() {
		return status;
	}

	public void setStatus(Object status) {
		this.status = status;
	}

	public DrawerListItemAction getOnItemSelectAction() {
		return action;
	}

	@Override
	public void setOnItemSelectAction(DrawerListItemAction action) {
		this.action = action;
	}

	public static String getMenuItemName(int id) {
		Application app = EarthquakeInfoApp.getInstance();

		switch (id) {
		case MAP:
			return app.getResources().getString(R.string.nav_item_map);
		case CHART_DATE:
			return app.getResources().getString(R.string.nav_item_chart_date);
		case CHART_LOCATION:
			return app.getResources().getString(R.string.nav_item_chart_location);
		case CHART_MAGNITUDE:
			return app.getResources().getString(R.string.nav_item_chart_by_magnitude);
		default:
			return null;
		}

	}
}
