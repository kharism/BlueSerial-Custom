package com.blueserial;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class ActivityAnak extends Activity {
	public final static String BALITA_LIST = "/antro/balita/";
	public final static String LOGIN = "/ws/usr/login/";
	public final static String FORM_KUNJUNGAN_TOKEN_URL = "/ws/ui/form/form-anak-kunjungan?aksi=p&format=json";
	public final static String FORM_ACTION="/ws/anak/kunjungan/";
	Map<String,EditText> maps;
	private Button buttonManualBerat;
	private Button buttonManualTinggi;
	private Button buttonManualLila;
	private Button buttonManualLika;
	private Button buttonManualTricep;
	private Button buttonManualSubskapular;
	private Button buttonAnakSet;
	private Button buttonSimpan;
	private Button toggleCaliper;
	private Button buttonStopBerat;
	private Button buttonStopTinggi;
	
	private EditText editTextBerat;
	private EditText editTextTinggi;
	private EditText editTextLila;
	private EditText editTextLika;
	private EditText editTextTricep;
	private EditText editTextSubskapular;
	
	private List<Runnable> readThreads;
	private ArrayList<BluetoothDevice> devices;
	private boolean exitOnDisconect = true;
	private UUID mDeviceUUID;
	private Handler loginHandler;
	private Activity activity;
	private boolean isLogedIn;
	private String sessid;
	private String editToken;
	private JSONObject anak;
	SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anak);
		readThreads = new ArrayList<Runnable>();
		Intent intent = getIntent();
		loginHandler = new Handler();
		prefs = this.getSharedPreferences("com.blueserial", Context.MODE_PRIVATE);
		activity = this;
		Bundle b = intent.getExtras();
		buttonManualBerat = (Button) findViewById(R.id.buttonManualBerat);
		buttonManualTinggi = (Button) findViewById(R.id.buttonManualTinggi);
		buttonManualLila = (Button) findViewById(R.id.buttonManualLila);
		buttonManualLika = (Button) findViewById(R.id.buttonManualLika);
		buttonManualTricep = (Button) findViewById(R.id.buttonManualTricep);
		buttonManualSubskapular = (Button) findViewById(R.id.buttonManualSubskapular);
		buttonAnakSet = (Button) findViewById(R.id.buttonAnakSet);
		buttonSimpan = (Button) findViewById(R.id.buttonSimpan);
		toggleCaliper = (Button) findViewById(R.id.toggleCaliper);
		buttonStopBerat = (Button) findViewById(R.id.buttonStopBerat);
		buttonStopTinggi = (Button) findViewById(R.id.buttonStopTinggi);
		
		editTextBerat = (EditText) findViewById(R.id.editTextBerat);
		editTextTinggi = (EditText) findViewById(R.id.editTextTinggi);
		editTextLila = (EditText) findViewById(R.id.editTextLila);
		editTextLika = (EditText) findViewById(R.id.editTextLika);
		editTextTricep = (EditText) findViewById(R.id.editTextTricep);
		editTextSubskapular = (EditText) findViewById(R.id.editTextSubskapular);
		
		if(prefs.contains("tinggi")){
			String h = prefs.getString("tinggi", "");
			editTextTinggi.setText(h);
			editTextTinggi.refreshDrawableState();
		}
		if(prefs.contains("berat")){
			editTextBerat.setText(prefs.getString("berat", ""));
		}
		if(prefs.contains("tricep")){
			editTextTricep.setText(prefs.getString("tricep", ""));
		}
		if(prefs.contains("subscapular")){
			editTextSubskapular.setText(prefs.getString("subscapular", ""));
		}
		
		maps = new HashMap<String, EditText>();
		maps.put("T", editTextTinggi);
		maps.put("S", editTextBerat);
		maps.put("LE", editTextTricep);
		maps.put("BB", editTextBerat);
		
		mDeviceUUID = UUID.fromString(b.getString(Homescreen.DEVICE_UUID));
		devices = b.getParcelableArrayList(Homescreen.DEVICES_LISTS);
		try {
			anak = new JSONObject(b.getString(SelectAnakActivity.ID_ANAK));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buttonManualBerat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextBerat.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextBerat.setEnabled(true);
			}
		});
		buttonSimpan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new GetTokenTask().execute();
			}
		});
		buttonStopBerat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		buttonStopTinggi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(maps.containsKey("T"))
					maps.remove("T");
				else
					maps.put("T", editTextTinggi);					
			}
		});
		buttonManualTinggi.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						editTextTinggi.setInputType(InputType.TYPE_CLASS_TEXT);
						editTextTinggi.setEnabled(true);
					}
				});
		buttonManualLika.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextLika.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextLika.setEnabled(true);
			}
		});
		buttonManualLila.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextLila.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextLila.setEnabled(true);
			}
		});
		buttonManualTricep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextTricep.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextTricep.setEnabled(true);
			}
		});
		toggleCaliper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(maps.get("LE")==editTextTricep)
				maps.put("LE", editTextSubskapular);
				else
					maps.put("LE", editTextTricep);	
			}
		});
		buttonManualSubskapular.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextSubskapular.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextSubskapular.setEnabled(true);
			}
		});
		buttonAnakSet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(int i=readThreads.size()-1;i>=0;i--){
					ReadInput u = (ReadInput)readThreads.get(i);
					u.stop();
					readThreads.remove(u);
				}
			}
		});
		
		//new loginTask().execute();
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter3);
		for(int i=0;i<devices.size();i++){
			new ConnectBT(devices.get(i)).execute();			
		}
	}
	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
	        String action = arg1.getAction();
			final BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
	        	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	        	builder.setMessage("device "+device.getName()+" disconected");
	        	AlertDialog dialog = builder.create();
	        	dialog.show();
	        }
		}
		
	};
	private boolean kakiBengkak;
	private boolean berdiri;
	public void onRadioButtonClicked(View view) {
	    boolean checked = ((RadioButton)view).isChecked();
		switch (view.getId()) {
		case R.id.radioTidak:
			if(checked)
			kakiBengkak = false;
			break;
		case R.id.radioYa:
			if(checked)
			kakiBengkak = true;
			break;
		case R.id.radioBerdiri:
			berdiri = true;
			break;
		case R.id.radioTerlentang:
			berdiri = false;
		default:
			break;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.action_settings:
			new GetBalitaTask().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.anak, menu);
		return true;
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		prefs.edit().putString("tinggi", editTextTinggi.getText().toString()).commit();
		prefs.edit().putString("berat", editTextBerat.getText().toString()).commit();
		prefs.edit().putString("tricep", editTextTricep.getText().toString()).commit();
		prefs.edit().putString("subscapular", editTextSubskapular.getText().toString()).commit();
		
		for(int i=readThreads.size()-1;i>=0;i--){
			ReadInput u = (ReadInput)readThreads.get(i);
			u.stop();
			readThreads.remove(u);
		}try{
		this.unregisterReceiver(mReceiver);
		}catch(IllegalArgumentException ex){
			ex.printStackTrace();
		}
		super.onBackPressed();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		try{
		this.unregisterReceiver(mReceiver);
		}catch(IllegalArgumentException ex){
			
		}
		super.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter3);
        if(prefs.contains("tinggi")){
			String h = prefs.getString("tinggi", "");
			editTextTinggi.setText(h);
			editTextTinggi.refreshDrawableState();
		}
		if(prefs.contains("berat")){
			editTextBerat.setText(prefs.getString("berat", ""));
		}
		if(prefs.contains("tricep")){
			editTextTricep.setText(prefs.getString("tricep", ""));
		}
		if(prefs.contains("subscapular")){
			editTextSubskapular.setText(prefs.getString("subscapular", ""));
		}
        super.onResume();
	}
	private class SendDataTask extends AsyncTask<Void, Void, Void>{
		JSONObject response;
		ProgressDialog pd;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pd = new ProgressDialog(activity);
			pd.setCanceledOnTouchOutside(false);
			pd.setTitle("Coba mengirim data");
			Toast.makeText(activity, prefs.getString(PreferencesEditor.SERVER_URL, "")+ActivityAnak.FORM_ACTION, Toast.LENGTH_SHORT).show();
			pd.show();
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject o = new JSONObject();
			try {
				o.put("token", editToken);
				Date date = new Date();
				o.put("tanggal", date.getDate()+"/"+date.getMonth()+"/"+date.getYear());
				o.put("bb", editTextBerat.getText());
				o.put("tb", editTextTinggi.getText());
				o.put("pengukuran", berdiri?"BERDIRI":"TERLENTANG");
				o.put("edema",kakiBengkak?1:0);
				o.put("lika", editTextLika.getText());
				o.put("lila", editTextLila.getText());
				o.put("tlt", editTextTricep.getText());
				o.put("tls", editTextSubskapular.getText());
				response = (JSONObject) HttpClient.SendHttpPost(prefs.getString(PreferencesEditor.SERVER_URL, "")+ActivityAnak.FORM_ACTION+anak.getString("id"), o);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NullPointerException ex){
				ex.printStackTrace();
			}
			prefs.edit().remove("tinggi").commit();
			prefs.edit().remove("berat").commit();
			prefs.edit().remove("subscapular").commit();
			prefs.edit().remove("tricep").commit();
			
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					try {
						JSONObject kk = response;
						String f = kk.getString("pesan");
						Toast.makeText(activity, response.getString("pesan"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
				}
			});
			editTextTinggi.setText("");
			editTextBerat.setText("");
			editTextSubskapular.setText("");
			editTextTricep.setText("");
			
			super.onPostExecute(result);
		}
		
		
	}
	private class ConnectBT extends AsyncTask<Void, Void, Void> {
		private boolean mConnectSuccessful = true;
		BluetoothSocket mBTSocket;
		BluetoothDevice mDevice;
		String mes;
		private boolean mIsBluetoothConnected = false;
		Runnable mReadThread;
		public ConnectBT(BluetoothDevice dev){
			super();
			mDevice = dev;
		}
		@Override
		protected void onPreExecute() {
			//progressDialog = ProgressDialog.show(IbuActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
		}

		@Override
		protected Void doInBackground(Void... devices) {

			try {
				if (mBTSocket == null) {
					Method m = mDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
					mBTSocket = (BluetoothSocket) m.invoke(mDevice, 1);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					mBTSocket.connect();
				}
			} catch (IOException e) {
				// Unable to connect to device
				e.printStackTrace();
				mes = e.getMessage();
				mConnectSuccessful = false;
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mes = e.getMessage();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mes = e.getMessage();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mes = e.getMessage();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mes = e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (!mConnectSuccessful) {
				Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
				finish();
			} else {
				mIsBluetoothConnected = true;
				mReadThread = new ReadInput(mBTSocket); // Kick off input reader
				readThreads.add(mReadThread);
			}

			//progressDialog.dismiss();
		}

	}
	private class ReadInput implements Runnable {

		private boolean bStop = false;
		private Thread t;
		boolean threadStop=false;
		BluetoothSocket mBTSocket;
		Map<String,EditText> pp;
		StringHandler sh;
		public ReadInput(BluetoothSocket sock) {
			mBTSocket = sock;
			pp=maps;
			sh = new StringHandler();
			
			t = new Thread(this, "Input Thread");
			t.start();
		}

		public boolean isRunning() {
			return t.isAlive();
		}

		@SuppressLint("NewApi")
		@Override
		public void run() {
			InputStream inputStream;

			try {
				inputStream = mBTSocket.getInputStream();
				while (!bStop && !threadStop) {
					byte[] buffer = new byte[256];
					if (inputStream.available() > 0) {
						inputStream.read(buffer);
						int i = 0;
						/*
						 * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
						 */
						for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
						}
						final String strInput = new String(buffer, 0, i);

						/*
						 * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
						 */
						//TODO:Olah bacaan
						String[] lines=strInput.split("\r\n");
						int g=0;
						try{
							if(lines[g].isEmpty()|| !pp.containsKey(String.valueOf(lines[g].charAt(0)))){
							g++;
						}
						String ll = new String(lines[g].split(" ")[0]);
						final EditText curr = maps.get(ll);
						curr.post(new Runnable() {
							
							@Override
							public void run() {
								Log.i("SENSOR", strInput);
								String j = sh.Handle(strInput);
								if(!j.isEmpty())
								curr.setText(j);
							}
						});}
						catch(Exception ex){
							ex.printStackTrace();
						}

					}
					
					Thread.sleep(250);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void stop() {
			bStop = true;
			try {
				mBTSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	private class GetTokenTask extends AsyncTask<Void, Void, Void>{
		JSONObject rr;
		String strMessage;
		@Override
		protected Void doInBackground(Void... params) {
			rr = (JSONObject)HttpClient.SendHttpGet(prefs.getString(PreferencesEditor.SERVER_URL, "")+ActivityAnak.FORM_KUNJUNGAN_TOKEN_URL);
			try {
				sessid = rr.getString("sessid");
				editToken = rr.getString("token");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(NullPointerException ex){
				Log.e("RR", rr.toString());
				ex.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			activity.runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					String h = editToken;
					Toast.makeText(activity, editToken, Toast.LENGTH_SHORT).show();
				}
			});

			new SendDataTask().execute();
			super.onPostExecute(result);
		}
	}
	private class GetBalitaTask extends AsyncTask<Void, Void, Void>{
		JSONArray rr;
		@Override
		protected Void doInBackground(Void... arg0) {
			rr = (JSONArray)HttpClient.SendHttpGet(prefs.getString(PreferencesEditor.SERVER_URL, "")+ActivityAnak.BALITA_LIST);
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(activity, rr.toString(), Toast.LENGTH_LONG).show();
				}
			});
			super.onPostExecute(result);
		}
		
	}
	private class DisConnectBT extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			for(int i=readThreads.size();i>=0;i--){
				ReadInput r = (ReadInput) readThreads.get(i);
				r.stop();
				while (r.isRunning())
					;
				readThreads.remove(r);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//mIsBluetoothConnected = false;
			if (exitOnDisconect) {
				finish();
			}
		}

	}
}
