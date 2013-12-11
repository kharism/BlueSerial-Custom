package com.blueserial;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class CaliperHandler extends AbstractHandler {

	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
		try{
		String[] p = a.split("(\\s)+");
		String number = p[1];
		if(number.isEmpty())
			number = p[2];
		return number;
		}catch(Exception ex){
			return "";
		}
	}

	@Override
	public String getSatuan(String a) {
		// TODO Auto-generated method stub
		return "mm";
	}

}
