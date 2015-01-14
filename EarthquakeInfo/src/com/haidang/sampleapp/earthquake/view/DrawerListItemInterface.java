package com.haidang.sampleapp.earthquake.view;


public interface DrawerListItemInterface {
	public interface DrawerListItemAction {
		public void onItemSelectAction(DrawerListItemInterface item);
	}
	public int getIconResource() ;
	public String getText();
	public Object getStatus() ;
	public void selectItem();
	public void setOnItemSelectAction(DrawerListItemAction action);
	public int getType();
}
