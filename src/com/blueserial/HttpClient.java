package com.blueserial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

@SuppressLint("NewApi")
public class HttpClient {
	private static final String TAG = "HttpClient";
	private static ArrayList<Header> headers;
	public static CookieStore cookieStore = new BasicCookieStore();
	public static DefaultHttpClient httpclient = MyApplication.getClient();
	public static Object SendHttpPost(String URL, JSONObject jsonObjSend) {
		try {
			
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression
			if(headers!=null){
				for(int i=0;i<headers.size();i++){
					httpPostRequest.setHeader(headers.get(i));
				}
			}
			long t = System.currentTimeMillis();
			HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            httpclient.setCookieStore(cookieStore);
            
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest,localContext);
			/*List<Cookie> cookies = cookieStore.getCookies();
			
	        CookieManager cookieManager = CookieManager.getInstance();
			for (int i = 0; i < cookies.size(); i++) {
				Cookie sessionCookie = cookies.get(i);
				String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue() + "; domain=" + sessionCookie.getDomain();
				cookieManager.setCookie(sessionCookie.getDomain(), cookieString);
	            CookieSyncManager.getInstance().sync(); 
			}*/
			if(headers == null)
			{
				Header[] kk = response.getAllHeaders();
				headers = new ArrayList<Header>();
				String SetCookie = "";
				for(int i=0;i<kk.length;i++){
					if(kk[i].getName().equals("Set-Cookie"))
					SetCookie+=kk[i].getValue();
				}
				headers.add(new BasicHeader("Set-Cookie", SetCookie));
			}
			else{
				Header[] kk = response.getAllHeaders();
				
				for(int i=0;i<kk.length;i++){
					if(kk[i].getName().equals("Set-Cookie"))
					headers.add(kk[i]);
				}
			}
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				String resultString= convertStreamToString(instream);
				Log.i("RESPONSE",resultString);
				instream.close();
				if(resultString.charAt(0)=='{')
				{
					// Transform the String into a JSONObject
					JSONObject jsonObjRecv = new JSONObject(resultString);
					// Raw DEBUG output of our received JSON object:
					Log.i(TAG,"<JSONObject>\n"+jsonObjRecv.toString()+"\n</JSONObject>");
					return jsonObjRecv;
				}
				else if(resultString.charAt(0)=='[')
				{
					JSONArray jsonObjRecv = new JSONArray(resultString);
					// Raw DEBUG output of our received JSON object:
					Log.i(TAG,"<JSONArray>\n"+jsonObjRecv.toString()+"\n</JSONArray>");
					return jsonObjRecv;
				}
			} 

		}
		catch (Exception e)
		{
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
			String mes = e.getMessage();
			Log.e(TAG, mes);
		}
		return null;
	}
	
	public static CookieStore getCookieStore(){
		return cookieStore;
	}

	public static Object SendHttpGet(String URL){
		String jsonRaw = getHttp(URL);
		if(!jsonRaw.isEmpty()){
			if(jsonRaw.charAt(0)=='{'){
				try {
					JSONObject o = new JSONObject(jsonRaw);
					return o;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(jsonRaw.charAt(0)=='['){
				try {
					JSONArray o = new JSONArray(jsonRaw);
					return o;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else return null;
		}else
			return null;
		return null;
	}
	public static String getHttp(String url){
		String output;
		try {
			httpclient.setCookieStore(cookieStore);
			HttpGet httpGet = new HttpGet(url);
		    if(headers!=null){
				for(int i=0;i<headers.size();i++){
					httpGet.setHeader(headers.get(i));
					Log.i("HttpHeader", headers.get(i).getName()+":"+headers.get(i).getValue());
				}
			}
		    HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            
		    HttpResponse httpResponse = httpclient.execute(httpGet,localContext);
		    HttpEntity httpEntity = httpResponse.getEntity();
		    output = EntityUtils.toString(httpEntity);
		    return output;
		}catch(Exception ex){
			ex.printStackTrace();
			return "";
		}
	}
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 * 
		 * (c) public domain: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
