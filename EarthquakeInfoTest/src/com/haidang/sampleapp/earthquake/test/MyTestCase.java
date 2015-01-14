package com.haidang.sampleapp.earthquake.test;

import com.haidang.sampleapp.earthquake.database.DatabaseManager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

public class MyTestCase extends InstrumentationTestCase{
	protected ContentResolver contentResolver;
	protected DatabaseManager dbManager;
	protected AssetManager assestManager;
	protected Context context;

	@Override
	protected void setUp() {
		context = getInstrumentation().getContext();
		contentResolver = context.getContentResolver();
		dbManager = DatabaseManager.getInstance(context);
		assestManager = context.getAssets();
	}

}
