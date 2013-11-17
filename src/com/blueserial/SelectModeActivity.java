package com.blueserial;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectModeActivity extends Activity {
	Button selectIbu;
	Button selectAnak;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_mode);
		selectAnak = (Button)findViewById(R.id.buttonSelectAnak);
		selectIbu = (Button)findViewById(R.id.buttonSelectIbu);
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
