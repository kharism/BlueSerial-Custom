package com.blueserial;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectModeActivity extends Activity {
	Button selectIbu;
	Button selectAnak;
	Button selectTambahIbu;
	Button selectTambahAnak;
	SharedPreferences prefs;
	public static String BUMIL="/gia/bumil/lihat";
	public static String ANAK="/gia/anak/lihat";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_mode);
		prefs = this.getSharedPreferences("com.blueserial", Context.MODE_PRIVATE);
		selectAnak = (Button)findViewById(R.id.buttonSelectAnak);
		selectIbu = (Button)findViewById(R.id.buttonSelectIbu);
		selectTambahAnak = (Button)findViewById(R.id.buttonTambahAnak);
		selectTambahIbu = (Button)findViewById(R.id.buttonTambahIbu);
		selectIbu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), SelectIbuActivity.class);
				startActivity(intent);
			}
		});
		selectAnak.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),SelectAnakActivity.class);
				startActivity(intent);
			}
		});
		selectTambahAnak.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i =new Intent(getApplicationContext(), WebActivity.class);
				Log.i("BROWSING",prefs.getString(PreferencesEditor.SERVER_URL, "")+ANAK);
				i.putExtra(WebActivity.WEB_URL, prefs.getString(PreferencesEditor.SERVER_URL, "")+ANAK);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_mode, menu);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.action_settings:
			Intent i=new Intent(getApplicationContext(),PreferencesEditor.class);
			startActivity(i);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
