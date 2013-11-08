package com.blueserial;

public abstract class AbstractHandler {
	final static int MODE_RAW = 0;
	final static int MODE_AVG = 1;
	protected int curMode = 0;
	public abstract String Handle(String a);
	public abstract String getSatuan(String a);
	public void setMode(int mode){
		curMode = mode;
	}
	
}
