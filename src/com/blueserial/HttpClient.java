package com.blueserial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("NewApi")
public class HttpClient {
	private static final String TAG = "HttpClient";

	public static Object SendHttpPost(String URL, JSONObject jsonObjSend) {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
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
	private static String getHttp(String url){
		String output;
		try {
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpGet httpGet = new HttpGet(url);

		    HttpResponse httpResponse = httpClient.execute(httpGet);
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
