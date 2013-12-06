package com.blueserial;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrasiIbu extends IbuActivity {
	public static String REGISTRASI_IBU="com.blueserial.registrasiibu";
	private static String REGISTRASI_IBU_URL="/ws/bumil/daftar";
	private static String REGISTRASI_IBU_TOKEN="/ws/ui/form/form-bumil-reg?aksi=p&format=json";
	private final static String[] PENDIDIKAN={"TS","SD","SMP","SMA_SMK","S1","S2","S3","TT"};
	final static String[] AGAMA={"islam","hindu","budha","kristen katholik","kristen protestan","S2","S3","TT"};
	protected TextView labelKtp;
	protected TextView labelUmur;
	protected TextView labelTanggal;
	protected TextView labelTempat;
	protected TextView labelPekerjaan;
	protected TextView labelPendidikan;
	protected TextView labelAgama;
	protected TextView labelDarah;
	protected EditText editTextKtp;
	protected EditText editTextUmur;
	protected EditText editTextTanggal;
	protected EditText editTextTempat;
	protected EditText editTextPekerjaan;
	protected Spinner spinnerPendidikan;
	protected Spinner spinnerAgama;
	protected Spinner spinnerDarah;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		labelTinggi.setVisibility(View.VISIBLE);
		editTextTinggi.setVisibility(View.VISIBLE);
		LabelNama.setVisibility(View.VISIBLE);
		editTextNama.setVisibility(View.VISIBLE);
		//tedious chore is shit
		findViewById(R.id.buttonManualTinggi).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonSetTinggi).setVisibility(View.VISIBLE);
		buttonSimpan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new GetTokenRegistrasi().execute();
			}
		});		
		
		labelAgama = (TextView)findViewById(R.id.textAgama);
		labelDarah = (TextView)findViewById(R.id.textdarah);
		labelKtp = (TextView)findViewById(R.id.textKtp);
		labelPekerjaan = (TextView)findViewById(R.id.textPekerjaan);
		labelPendidikan = (TextView)findViewById(R.id.textPendidikan);
		labelTanggal = (TextView)findViewById(R.id.textTanggalLahir);
		labelTempat = (TextView)findViewById(R.id.textTempat);
		labelUmur = (TextView)findViewById(R.id.textUmur);
		
		labelAgama.setVisibility(View.VISIBLE);
		labelDarah.setVisibility(View.VISIBLE);
		labelKtp.setVisibility(View.VISIBLE);
		labelPekerjaan.setVisibility(View.VISIBLE);
		labelPendidikan.setVisibility(View.VISIBLE);
		labelTanggal.setVisibility(View.VISIBLE);
		labelTempat.setVisibility(View.VISIBLE);
		labelUmur.setVisibility(View.VISIBLE);
		
		editTextKtp =(EditText)findViewById(R.id.editTextKtp);
		editTextPekerjaan =(EditText)findViewById(R.id.editTextPekerjaan);
		editTextTempat =(EditText)findViewById(R.id.editTextTempat);
		editTextTanggal =(EditText)findViewById(R.id.editTextTanggal);
		editTextUmur =(EditText)findViewById(R.id.editTextUmur);
		spinnerAgama = (Spinner)findViewById(R.id.spinnerAgama);
		spinnerDarah= (Spinner)findViewById(R.id.spinnerDarah);
		spinnerPendidikan = (Spinner)findViewById(R.id.spinnerPendidikan);
		editTextKtp.setVisibility(View.VISIBLE);
		editTextPekerjaan.setVisibility(View.VISIBLE);
		editTextTempat.setVisibility(View.VISIBLE);
		editTextTanggal.setVisibility(View.VISIBLE);
		editTextUmur.setVisibility(View.VISIBLE);
		spinnerPendidikan.setVisibility(View.VISIBLE);
		spinnerAgama.setVisibility(View.VISIBLE);
		spinnerDarah.setVisibility(View.VISIBLE);
	}
	protected class GetTokenRegistrasi extends GetToken{
		public GetTokenRegistrasi() {
			tokenUrl = REGISTRASI_IBU_TOKEN;
		}
		@Override
		protected void onPostExecute(Void result) {
			new SendRegistrasiTask().execute();
		}
	}
	protected class SendRegistrasiTask extends AsyncTask<Void, Void, Void>{
		JSONObject o = new JSONObject();
		JSONObject l = new JSONObject();
		@Override
		protected Void doInBackground(Void... params) {
			try {
				o.put("token", token);
				o.put("nama", editTextNama.getText().toString());
				o.put("tinggi", editTextTinggi.getText().toString());
				o.put("tempat_lahir", editTextTempat.getText().toString());
				o.put("tanggal_lahir", editTextTanggal.getText().toString());
				o.put("umur", Integer.parseInt(editTextUmur.getText().toString()));
				o.put("pekerjaan", editTextPekerjaan.getText().toString());
				o.put("darah", spinnerDarah.getSelectedItem());
				o.put("pendidikan",PENDIDIKAN[spinnerPendidikan.getSelectedItemPosition()]);
				o.put("agama",AGAMA[spinnerPendidikan.getSelectedItemPosition()]);
				o.put("KTP", editTextKtp.getText().toString());
				o.put("kaki_bengkak", kakiBengkak?"1":"2");
				o.put("berat", editTextBerat.getText());
				l = (JSONObject)HttpClient.SendHttpPost(prefs.getString(PreferencesEditor.SERVER_URL, "")+REGISTRASI_IBU_URL, o);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						Toast.makeText(activity, l.getString("pesan"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
				}
			});
			
			super.onPostExecute(result);
		}
	}
}
