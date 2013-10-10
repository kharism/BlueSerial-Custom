package com.blueserial;

import java.util.HashMap;
import java.util.Map;

public class StringHandler {
	private Map<String,AbstractHandler> mHandlers;
	public StringHandler(){
		mHandlers = new HashMap<String, AbstractHandler>();
		mHandlers.put("T", new TinggiHandler());
		mHandlers.put("S", new BeratHandler());
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
		int g=0;
		if(!this.mHandlers.containsKey(String.valueOf(lines[g].charAt(0)))){
			g++;
		}
		String i = new String(lines[g].substring(0, 1));
		AbstractHandler l = (AbstractHandler)mHandlers.get(i);
		return l.Handle(lines[g]);
	}
	public String getSatuan(String input){
		String[] lines=input.split("\r\n");
		int g=0;
		if(!this.mHandlers.containsKey(String.valueOf(lines[g].charAt(0)))){
			g++;
		}
		String i = new String(lines[g].substring(0, 1));
		AbstractHandler l = (AbstractHandler)mHandlers.get(i);
		return l.getSatuan(lines[g]);
	}
}
