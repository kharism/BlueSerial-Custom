package com.blueserial;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class IbuActivity extends Activity {
	public final static String FORM_KUNJUNGAN_TOKEN_URL = "http://gizi.inovasihusada.com/ws/ui/form/form-bumil-kunjungan?aksi=p&format=json";
	private Button mBtnHbManual;
	private Button buttonManualBerat;
	private Button buttonManualTinggi;
	private Button buttonManualLila;
	private Button buttonIbuSet;
	private EditText editHb;
	private EditText editTextLila;
	private EditText editTextTinggi;
	private EditText editTextBerat;
	private ProgressDialog progressDialog;
	private UUID mDeviceUUID;
	private Activity activity;
	private List<Runnable> readThreads;
	private ArrayList<BluetoothDevice> devices;
	private boolean exitOnDisconect = true;
	private boolean isLogedIn = false;
	private String sessid;
	private String token;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		for(int i=readThreads.size()-1;i>=0;i--){
			ReadInput u = (ReadInput)readThreads.get(i);
			u.stop();
			readThreads.remove(u);
		}
		super.onBackPressed();
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ibu);
		readThreads = new ArrayList<Runnable>();
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		activity = this;
		editHb = (EditText) findViewById(R.id.editHb);
		mBtnHbManual = (Button) findViewById(R.id.buttonManualHb);
		buttonManualBerat = (Button) findViewById(R.id.buttonManualBerat);
		buttonManualTinggi = (Button) findViewById(R.id.buttonManualTinggi);
		buttonManualLila = (Button) findViewById(R.id.buttonManualLila);
		buttonIbuSet = (Button) findViewById(R.id.buttonIbuSet);
		editHb = (EditText) findViewById(R.id.editHb);
		editTextLila = (EditText) findViewById(R.id.editTextLila);
		editTextTinggi = (EditText) findViewById(R.id.editTextTinggi);
		editTextBerat = (EditText) findViewById(R.id.editTextBerat);
		mDeviceUUID = UUID.fromString(b.getString(Homescreen.DEVICE_UUID));
		devices = b.getParcelableArrayList(Homescreen.DEVICES_LISTS);
		mBtnHbManual.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				editHb.setInputType(InputType.TYPE_CLASS_TEXT);
				editHb.setEnabled(true);
			}
		});
		buttonManualBerat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextBerat.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextBerat.setEnabled(true);
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
		buttonManualLila.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextLila.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextLila.setEnabled(true);
			}
		});
		buttonIbuSet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				for(int i=readThreads.size()-1;i>=0;i--){
					ReadInput u = (ReadInput)readThreads.get(i);
					u.stop();
					readThreads.remove(u);
				}
			}
		});
		new loginTask().execute();
		for(int i=0;i<devices.size();i++){
			new ConnectBT(devices.get(i)).execute();
		
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ibu, menu);
		return true;
	}
	@SuppressLint("NewApi")
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
		BluetoothSocket mBTSocket;
		Map<String,EditText> maps;
		StringHandler sh;
		public ReadInput(BluetoothSocket sock) {
			mBTSocket = sock;
			sh = new StringHandler();
			maps = new HashMap<String, EditText>();
			maps.put("T", editTextTinggi);
			maps.put("S", editTextBerat);
			maps.put("BB", editTextBerat);
			maps.put("BI", editTextBerat);
			t = new Thread(this, "Input Thread");
			t.start();
		}

		public boolean isRunning() {
			return t.isAlive();
		}

		@Override
		public void run() {
			InputStream inputStream;

			try {
				inputStream = mBTSocket.getInputStream();
				while (!bStop) {
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
						try{if(lines[g].isEmpty()|| !maps.containsKey(lines[g].split(" ")[0])){
							g++;
						}
						String pp = new String(lines[g].split(" ")[0]);
						final EditText curr = maps.get(pp);
						curr.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								String j = sh.Handle(strInput);
								if(!j.isEmpty())
								curr.setText(j);
							}
						});}
						catch(Exception ex){
							
						}

					}
					Thread.sleep(500);
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
	private class loginTask extends AsyncTask<Void, Void, Void>{
		JSONObject rr;
		String strMessage;
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Map<String,String> login = new HashMap<String, String>();
			login.put("username", "admin");
			login.put("password", "admin");
			JSONObject o = new JSONObject(login);
			rr = (JSONObject)HttpClient.SendHttpPost("http://gizi.inovasihusada.com/ws/usr/login", o);
			
			try {
				Log.i("JSON", rr.toString());
				JSONObject message;
				message = ((JSONObject)rr.get("message"));
				//activity.setTitle(message.getString("pesan"));
				strMessage = message.getString("pesan");
				if(message.getString("tipe").equals("success")){
					isLogedIn = true;
					JSONObject form = (JSONObject) HttpClient.SendHttpGet(IbuActivity.FORM_KUNJUNGAN_TOKEN_URL);
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
