/*
 * Released under MIT License http://opensource.org/licenses/MIT
 * Copyright (c) 2013 Plasty Grove
 * Refer to file LICENSE or URL above for full text 
 */

package com.blueserial;

import java.util.ArrayList;


import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class MyApplication extends Application{
	private ArrayList<BluetoothDevice> staticDevList;
	private ArrayList<Short> staticRSID;
	private static final DefaultHttpClient client = createClient();
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CookieSyncManager.createInstance(this);
	    CookieManager.getInstance().setAcceptCookie(true);
	}
	static DefaultHttpClient getClient(){
        return client;
	}
	private static DefaultHttpClient createClient(){
        BasicHttpParams params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        DefaultHttpClient httpclient = new DefaultHttpClient(cm, params);
        httpclient.getCookieStore().getCookies();
        return httpclient;
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
