package com.blueserial;

public class CaliperHandler extends AbstractHandler {

	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
		try{
		String[] p = a.split(" ");
		String number = p[1];
		int len = number.length()-1;
		String comma = String.valueOf(number.charAt(len-1))+String.valueOf(number.charAt(len));
		len -=2;
		String mm;
		if(len==0)
			mm = String.valueOf(number.charAt(0));
		else
			mm = String.valueOf(number.charAt(0))+String.valueOf(number.charAt(len));
		return mm+"."+comma;
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
