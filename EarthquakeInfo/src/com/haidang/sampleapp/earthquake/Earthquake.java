package com.haidang.sampleapp.earthquake;

import java.text.ParseException;

import com.haidang.sampleapp.earthquake.util.DataFormatHelper;

/**
 * @author Hai Dang
 *
 */
public class Earthquake {
	public static enum Type{LIGHT, MODERATE, STRONG, MAJOR}
	
	public String src;
	public String eqid;
	public String timedate;
	public Double lat;
	public Double lon;
	public Double magnitude;
	public Double depth;
	public String region;
	
	public String getDateString(){
		if(timedate==null)
			return null;
		try {
			return DataFormatHelper.getDateString(timedate);
		} catch (ParseException e) {
			return null;
		}
	}
	public Type getType(){
		if(magnitude <= 4.5)
			return Type.LIGHT;
		else if(magnitude >4.5 && magnitude < 5.2)
			return Type.MODERATE;
		else if(magnitude >= 5.2 && magnitude < 6.2)
			return Type.STRONG;
		else
			return Type.MAJOR;
	}
}
