package com.blueserial;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PreferencesEditor extends Activity {
	public static String SERVER_URL = "serverUrl";
	SharedPreferences prefs;
	EditText editUrl;
	Activity activity;
	Button buttonSave;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences_editor);
		prefs = this.getSharedPreferences(
			      "com.blueserial", Context.MODE_PRIVATE);
		editUrl = (EditText)findViewById(R.id.editUrl);
		activity = this;
		buttonSave = (Button) findViewById(R.id.buttonSave);
		editUrl.setText(prefs.getString(SERVER_URL, "http://gia.karyateknologiinformasi.com/"));
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prefs.edit().putString(SERVER_URL, editUrl.getText().toString()).commit();
				activity.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preferences_editor, menu);
		return true;
	}

}
