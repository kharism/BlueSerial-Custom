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
	public static String LILA_MAC = "LILA_MAC";
	public static String LIKA_MAC = "LIKA_MAC";
	public static String TINGGI_BERDIRI_MAC = "TINGGI_BERDIRI_MAC";
	public static String TINGGI_TERLENTANG_MAC = "TINGGI_TERLENTANG_MAC";
	public static String BERAT_ANAK_MAC = "BERAT_ANAK_MAC";
	public static String BERAT_DEWASA_MAC = "BERAT_DEWASA_MAC";
	public static String CALIPER_MAC = "CALIPER_MAC";
	SharedPreferences prefs;
	EditText editUrl;
	EditText editLila;
	EditText editLika;
	EditText editTinggiTerlentang;
	EditText editTinggiBerdiri;
	EditText editBeratAnak;
	EditText editBeratDewasa;
	EditText editCaliper;
	Activity activity;
	Button buttonSave;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences_editor);
		prefs = this.getSharedPreferences(
			      "com.blueserial", Context.MODE_PRIVATE);
		editUrl = (EditText)findViewById(R.id.editUrl);
		editBeratAnak = (EditText)findViewById(R.id.editTimbanganAnak);
		editBeratDewasa = (EditText)findViewById(R.id.editTimbanganDewasa);
		editTinggiBerdiri = (EditText)findViewById(R.id.editTinggiBerdiri);
		editTinggiTerlentang = (EditText)findViewById(R.id.editTinggiTerlentang);
		editCaliper = (EditText)findViewById(R.id.editCaliper);
		editLika = (EditText)findViewById(R.id.editLika);
		editLila = (EditText)findViewById(R.id.editLila);
		activity = this;
		buttonSave = (Button) findViewById(R.id.buttonSave);
		editUrl.setText(prefs.getString(SERVER_URL, "http://gia.karyateknologiinformasi.com/"));
		editBeratAnak.setText(prefs.getString(BERAT_ANAK_MAC, ""));
		editBeratDewasa.setText(prefs.getString(BERAT_DEWASA_MAC, ""));;
		editTinggiBerdiri.setText(prefs.getString(TINGGI_BERDIRI_MAC, ""));;
		editTinggiTerlentang.setText(prefs.getString(TINGGI_TERLENTANG_MAC, ""));;
		editCaliper.setText(prefs.getString(CALIPER_MAC, ""));;
		editLika.setText(prefs.getString(LIKA_MAC, ""));
		editLila.setText(prefs.getString(LILA_MAC, ""));;
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prefs.edit().putString(SERVER_URL, editUrl.getText().toString()).commit();
				prefs.edit().putString(CALIPER_MAC, editCaliper.getText().toString()).commit();
				prefs.edit().putString(LIKA_MAC, editLika.getText().toString()).commit();
				prefs.edit().putString(LILA_MAC, editLila.getText().toString()).commit();
				prefs.edit().putString(TINGGI_BERDIRI_MAC, editTinggiBerdiri.getText().toString()).commit();
				prefs.edit().putString(TINGGI_TERLENTANG_MAC, editTinggiTerlentang.getText().toString()).commit();
				prefs.edit().putString(BERAT_ANAK_MAC, editBeratAnak.getText().toString()).commit();
				prefs.edit().putString(BERAT_DEWASA_MAC, editBeratDewasa.getText().toString()).commit();
				
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
