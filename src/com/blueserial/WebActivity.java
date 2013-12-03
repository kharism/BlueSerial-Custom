package com.blueserial;

import java.net.CookieHandler;
import java.net.URL;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {
	public static String WEB_URL="com.blueserial.WebActivity";
	private String url;
	WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		webView = (WebView)findViewById(R.id.webView);
		DefaultHttpClient mClient = MyApplication.getClient();
		url = getIntent().getExtras().getString(WEB_URL);
		CookieStore cookieStore = mClient.getCookieStore();
		Cookie sessionCookie = null;
		List<Cookie> cookies = cookieStore.getCookies();
		CookieSyncManager cookieSync = CookieSyncManager.createInstance(WebActivity.this);
        cookieSync.sync();
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
        for (int i = 0; i < cookies.size(); i++) {
			sessionCookie = cookies.get(i);
			String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue()+ "; domain=" + sessionCookie.getDomain()+";path=/";
			Log.i("Domain","http://"+sessionCookie.getDomain());
			Log.i("CookieString",cookieString);
			cookieManager.setCookie("http://"+sessionCookie.getDomain(), cookieString);
            CookieSyncManager.getInstance().sync();
            break;
		}
        //webView = new WebView(getApplicationContext());		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				super.shouldOverrideUrlLoading(view, url);
				view.loadUrl(url);
				return true;
			}
		});
		webView.loadUrl(url);
	    Log.i("DomainCookie",cookieManager.getCookie("http://"+sessionCookie.getDomain()));
	    //new GetHttpContentTask().execute();
		//webView.loadData(HttpClient.getHttp(url), "text/html", null);
	}
	private class GetHttpContentTask extends AsyncTask<Void, Void, Void>{
		String gg="";
		@Override
		protected Void doInBackground(Void... arg0) {
			gg = HttpClient.getHttp(url);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			webView.loadData(gg, "text/html", null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

}
