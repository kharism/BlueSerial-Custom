/*
 * Released under MIT License http://opensource.org/licenses/MIT
 * Copyright (c) 2013 Plasty Grove
 * Refer to file LICENSE or URL above for full text 
 */

package com.blueserial;

import java.util.ArrayList;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

public class MyApplication extends Application{
	private ArrayList<BluetoothDevice> staticDevList;
	private ArrayList<Short> staticRSID;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	public ArrayList<BluetoothDevice> getStaticDevList() {
		return staticDevList;
	}
	public void setStaticDevList(ArrayList<BluetoothDevice> staticDevList) {
		this.staticDevList = staticDevList;
	}
	public ArrayList<Short> getStaticRSID() {
		return staticRSID;
	}
	public void setStaticRSID(ArrayList<Short> staticRSID) {
		this.staticRSID = staticRSID;
	}
	

}
