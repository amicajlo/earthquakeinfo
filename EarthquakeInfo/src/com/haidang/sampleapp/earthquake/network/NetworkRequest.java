package com.haidang.sampleapp.earthquake.network;

public interface NetworkRequest {

	public interface NetworkCallback<T> {
		public void update(T response);
	}

	/*
	 * This is download type - help DownloadManager to track which request is currently active
	 */
	public static enum ActiveRequest {
		DOWNLOAD_EARTHQUAKE_DATA, NONE;
	}

	public boolean isCanceled();

	public boolean isFinished();

	public void cancel();

	public void setDataTag(Object tag);

	public void setRequestType(ActiveRequest request);

	public ActiveRequest getRequestType();

}
