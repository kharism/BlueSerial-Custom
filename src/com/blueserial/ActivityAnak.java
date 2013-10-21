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


import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityAnak extends Activity {
	private Button buttonManualBerat;
	private Button buttonManualTinggi;
	private Button buttonManualLila;
	private Button buttonManualLika;
	private Button buttonManualTricep;
	private Button buttonManualSubskapular;
	private Button buttonAnakSet;
	
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anak);
		readThreads = new ArrayList<Runnable>();
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		buttonManualBerat = (Button) findViewById(R.id.buttonManualBerat);
		buttonManualTinggi = (Button) findViewById(R.id.buttonManualTinggi);
		buttonManualLila = (Button) findViewById(R.id.buttonManualLila);
		buttonManualLika = (Button) findViewById(R.id.buttonManualLika);
		buttonManualTricep = (Button) findViewById(R.id.buttonManualTricep);
		buttonManualSubskapular = (Button) findViewById(R.id.buttonManualSubskapular);
		buttonAnakSet = (Button) findViewById(R.id.buttonAnakSet);
		
		editTextBerat = (EditText) findViewById(R.id.editTextBerat);
		editTextTinggi = (EditText) findViewById(R.id.editTextTinggi);
		editTextLila = (EditText) findViewById(R.id.editTextLila);
		editTextLika = (EditText) findViewById(R.id.editTextLika);
		editTextTricep = (EditText) findViewById(R.id.editTextTricep);
		editTextSubskapular = (EditText) findViewById(R.id.editTextSubskapular);
		
		mDeviceUUID = UUID.fromString(b.getString(Homescreen.DEVICE_UUID));
		devices = b.getParcelableArrayList(Homescreen.DEVICES_LISTS);
		
		
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
		for(int i=0;i<devices.size();i++){
			new ConnectBT(devices.get(i)).execute();
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.anak, menu);
		return true;
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
		BluetoothSocket mBTSocket;
		Map<String,EditText> maps;
		StringHandler sh;
		public ReadInput(BluetoothSocket sock) {
			mBTSocket = sock;
			sh = new StringHandler();
			maps = new HashMap<String, EditText>();
			maps.put("T", editTextTinggi);
			maps.put("S", editTextBerat);
			maps.put("LE", editTextTricep);
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
						try{
							if(lines[g].isEmpty()|| !maps.containsKey(String.valueOf(lines[g].charAt(0)))){
							g++;
						}
						String pp = new String(lines[g].split(" ")[0]);
						final EditText curr = maps.get(pp);
						curr.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								curr.setText(sh.Handle(strInput));
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
