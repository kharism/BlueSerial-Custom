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
		setMode(AbstractHandler.MODE_AVG);
	}
	@Override
	public String Handle(String a) {
		// TODO Auto-generated method stub
		try{
			if(Pattern.matches("BB [0-9]*.[0-9]* gr 1", a))
			{
				String[] p = a.split("(\\s)+");
				data = p[1];
				satuan = p[2];
				double dataKg = Double.parseDouble(data)/1000;
				Log.i("BacaanSensor", data);
				if(curMode == AbstractHandler.MODE_RAW)
					return String.valueOf(dataKg);
				hasil.add(dataKg);
				if(hasil.size()<5){
					return "";
				}
				else{
					double temp = 0;
					for(int i=0;i<hasil.size();i++){
						temp += hasil.get(i);
					}
					temp = temp/hasil.size();
					hasil.remove(0);
					return String.valueOf(temp);
				}				
			}
			else
				return "";
		}
		catch(Exception ex){
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
