package com.blueserial;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class StringHandler {
	private Map<String,AbstractHandler> mHandlers;
	public StringHandler(){
		mHandlers = new HashMap<String, AbstractHandler>();
		mHandlers.put("TB", new TinggiHandler());
		mHandlers.put("S", new BeratHandler());
		mHandlers.put("LL", new LilaLikaHandler());
		mHandlers.put("LE", new CaliperHandler());
		mHandlers.put("BB",new BBeratHandler());
		mHandlers.put("BI",new BIHandler());
	}
	private boolean isNumeric(String j){
		try{
			double d = Double.parseDouble(j);
		}catch(NumberFormatException ex){
			return false;
		}
		return true;
	}
	public String Handle(String input){
		String[] lines=input.split("\r\n");
		int g=lines.length-1;
		
		if(!Pattern.matches("[a-zA-Z]*(\\s)+(-)?[0-9]*(\\s)*[a-zA-Z]*\\n", lines[g])||lines[g].isEmpty() || !this.mHandlers.containsKey(lines[g].split("\\s")[0])){
			g--;
		}
		String h = "";
		try{
			do{
				String i = new String(lines[g].split("\\s")[0]);
				AbstractHandler l = (AbstractHandler)mHandlers.get(i);
				h=l.Handle(lines[g]);
				Log.i("DATA BACAAN",h);
				g++;
			}
			while(!h.equalsIgnoreCase("") && Double.valueOf(h)==0 && g<lines.length);
			return h;
		}catch(Exception ex){
			ex.printStackTrace();
			return "";
		}
	}
	public String getSatuan(String input){
		String[] lines=input.split("\r\n");
		int g=0;
		if(lines[g].isEmpty() || !this.mHandlers.containsKey(String.valueOf(lines[g].charAt(0)))){
			g++;
		}
		String i = new String(lines[g].split(" ")[0]);
		AbstractHandler l = (AbstractHandler)mHandlers.get(i);
		return l.getSatuan(lines[g]);
	}
}
