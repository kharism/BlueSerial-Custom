package com.blueserial;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.util.Log;

public class BBeratHandler extends AbstractHandler {
	String data;
	String satuan;
	ArrayList<Double> hasil;
	public BBeratHandler(){
		hasil = new ArrayList<Double>();
		setMode(AbstractHandler.MODE_RAW);
	}
	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
		try{
			Log.i("BBerat", a);
			Log.i("Len", String.valueOf(a.length()));
			if(a.length()<10){
				return "";
			}
			if(Pattern.matches("BB(\\s)+[0-9]*(\\s)+gr(\\s)*", a))
			{
				String[] p = a.split("(\\s)+");
				data = p[1];
				satuan = p[2];
				double dataKg = Double.parseDouble(data)/1000;
				
				if(curMode == AbstractHandler.MODE_RAW)
					return String.valueOf(dataKg);
				if(hasil.size()>0 && hasil.get(hasil.size()-1)-dataKg<0.02)
					hasil.add(dataKg);
				else if(hasil.size()==0){
					hasil.add(dataKg);
				}
				Log.v("Hasil.Size", String.valueOf(hasil.size()));
				
				if(hasil.size()<15){
					return "";
				}
				else{
					double temp = 0;
					for(int i=0;i<hasil.size();i++){
						Log.i("averaging", String.valueOf(hasil.get(i)));
						temp += hasil.get(i);
					}
					temp = temp/hasil.size();
					for(int i=hasil.size()-1;i>=0;i--){
						Log.i("removing", String.valueOf(hasil.get(i)));
						hasil.remove(i);
					}
					return String.valueOf(temp);
				}				
			}
			else{
				Log.i("BBerat", "Pattern Unmatched");
				return "";
			}
				
		}
		catch(Exception ex){
			ex.printStackTrace();
			return "";
		}
	}

	@Override
	public String getSatuan(String a) {
		// TODO Auto-generated method stub
		String[] p = a.split(" ");
		data = p[1];
		satuan = p[2];
		//double dataKg = Double.parseDouble(data);
		return "Kg";
	}

}
