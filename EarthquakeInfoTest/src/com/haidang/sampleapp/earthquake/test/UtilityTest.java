package com.haidang.sampleapp.earthquake.test;

import java.text.ParseException;
import java.util.Date;

import com.haidang.sampleapp.earthquake.util.DataFormatHelper;


public class UtilityTest extends MyTestCase {
	String dataDate = "2013-07-16 12:22:23";
	public void testGetDateFormString(){
		try {
			Date date = DataFormatHelper.getDateFromString(dataDate);
			assertNotNull(date);
		} catch (ParseException e) {
			fail();
		}
	}
	
	public void testGetDateString(){
		try {
			String date = DataFormatHelper.getDateString(dataDate);
			assertNotNull(date);
			assertEquals("2013-07-16", date);
		} catch (ParseException e) {
			fail();
		}
	}
	
	public void testgetDayInDateString(){
		try {
			int day = DataFormatHelper.getDayInDateString(dataDate);
			assertEquals(16, day);
		} catch (ParseException e) {
			fail();
		}
	}
	
}
