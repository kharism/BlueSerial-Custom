package com.blueserial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends Activity {

	EditText editUsername;
	EditText editPassword;
	Button submitButton;
	ProgressDialog pd;
	Activity activity;
	SharedPreferences prefs;
	boolean isLogedIn = false;
	String sessid;
	String token;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		pd = new ProgressDialog(this);
		
		activity = this;
		prefs = this.getSharedPreferences(
			      "com.blueserial", Context.MODE_PRIVATE);
		submitButton =(Button)findViewById(R.id.buttonLogin);
		editUsername = (EditText)findViewById(R.id.editUsername);
		editPassword = (EditText)findViewById(R.id.editPassword);
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new loginTask().execute();
			}
		});
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_settings:
			Intent i=new Intent(getApplicationContext(),PreferencesEditor.class);
			startActivity(i);
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	private class loginTask extends AsyncTask<Void, Void, Void>{
		JSONObject rr;
		String strMessage;
		@Override
		protected void onPreExecute() {
			if(pd==null){
				pd = new ProgressDialog(activity);
			}
			//Toast.makeText(activity, prefs.getString(PreferencesEditor.SERVER_URL, "")+SelectIbuActivity.LOGIN_IBU, Toast.LENGTH_SHORT).show();
			pd.setTitle("Coba Login");
			pd.setCancelable(true);
			
			pd.setCanceledOnTouchOutside(false);
			pd.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Map<String,String> login = new HashMap<String, String>();
			login.put("username", editUsername.getText().toString());
			login.put("password", editPassword.getText().toString());
			JSONObject o = new JSONObject(login);
			HttpClient.cookieStore = ((MyApplication)getApplication()).getCookieStore();
			rr = (JSONObject)HttpClient.SendHttpPost(prefs.getString(PreferencesEditor.SERVER_URL, "")+SelectIbuActivity.LOGIN_IBU, o);
			
			try {
				Log.i("JSON", rr.toString());
				JSONObject message;
				message = ((JSONObject)rr.get("message"));
				//activity.setTitle(message.getString("pesan"));
				strMessage = message.getString("pesan");
				if(message.getString("tipe").equals("success")||(message.getString("tipe").equals("error")&&message.getString("pesan").equalsIgnoreCase("Username sudah login"))){
					isLogedIn = true;
					JSONObject form = (JSONObject) HttpClient.SendHttpGet(prefs.getString(PreferencesEditor.SERVER_URL, "")+ActivityAnak.FORM_KUNJUNGAN_TOKEN_URL);
					Log.i("JSON",form.toString());
					sessid = form.getString("sessid");
					token = form.getString("token");
					Log.d("TOKEN", token);						
					
				}
				activity.runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						Toast.makeText(activity, sessid, Toast.LENGTH_LONG).show();					
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				activity.runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show();					
					}
				});
			}
			catch (NullPointerException e){
				activity.runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show();					
					}
				});
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			if(isLogedIn){
				Intent i =new Intent(getApplicationContext(),SelectModeActivity.class);
				startActivity(i);
			}
			super.onPostExecute(result);
			
		}
	}

}
