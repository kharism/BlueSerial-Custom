package com.blueserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
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
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ShowToast" })
public class IbuActivity extends Activity {
	public final static String FORM_KUNJUNGAN_TOKEN_URL = "/ws/ui/form/form-bumil-kunjungan?aksi=p&format=json";
	public final static String FORM_ACTION = "/ws/bumil/kunjungan/";
	private ProgressDialog pd;
	protected Button mBtnHbManual;
	protected Button buttonManualBerat;
	protected Button buttonManualTinggi;
	protected Button buttonManualLila;
	protected Button buttonIbuSet;
	protected Button buttonSimpan;
	protected TextView labelIbu;
	protected TextView labelTinggi;
	protected TextView LabelNama;
	protected EditText editHb;
	protected EditText editTextLila;
	protected EditText editTextTinggi;
	protected EditText editTextBerat;
	protected EditText editTextNama;
	protected ProgressDialog progressDialog;
	protected UUID mDeviceUUID;
	protected Activity activity;
	protected int selectedDevice;
	protected HashMap<String, EditText> maps;
	protected List<Runnable> readThreads;
	protected ArrayList<BluetoothDevice> devices;
	protected ArrayList<BluetoothSocket> sockets;
	protected boolean exitOnDisconect = true;
	protected boolean isLogedIn = false;
	protected String sessid;
	protected String token;
	protected boolean kakiBengkak;
	protected JSONArray kehamilan;
	SharedPreferences prefs;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		for(int i=readThreads.size()-1;i>=0;i--){
			ReadInput2 u = (ReadInput2)readThreads.get(i);
			u.setRunning(false);
			readThreads.remove(u);
		}
		super.onBackPressed();
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ibu);
		readThreads = new ArrayList<Runnable>();
		prefs = this.getSharedPreferences("com.blueserial", Context.MODE_PRIVATE);;
		Intent intent = getIntent();
		sockets = new ArrayList<BluetoothSocket>();
		Bundle b = intent.getExtras();
		labelIbu = (TextView)findViewById(R.id.textView2);
		LabelNama = (TextView)findViewById(R.id.textNama);
		labelTinggi = (TextView)findViewById(R.id.textTinggi);
		activity = this;
		
		editHb = (EditText) findViewById(R.id.editHb);
		mBtnHbManual = (Button) findViewById(R.id.buttonManualHb);
		buttonManualBerat = (Button) findViewById(R.id.buttonManualBerat);
		buttonManualLila = (Button) findViewById(R.id.buttonManualLila);
		buttonIbuSet = (Button) findViewById(R.id.buttonIbuSet);
		buttonSimpan = (Button) findViewById(R.id.buttonSimpan);
		editHb = (EditText) findViewById(R.id.editHb);
		editTextNama = (EditText)findViewById(R.id.editTextNama);
		editTextLila = (EditText) findViewById(R.id.editTextLila);
		editTextBerat = (EditText) findViewById(R.id.editTextBerat);
		editTextTinggi = (EditText)findViewById(R.id.editTextTinggi);
		mDeviceUUID = UUID.fromString(b.getString(Homescreen.DEVICE_UUID));
		devices = b.getParcelableArrayList(Homescreen.DEVICES_LISTS);
		
		LabelNama.setVisibility(View.GONE);
		editTextNama.setVisibility(View.GONE);
		maps = new HashMap<String, EditText>();
		maps.put("S", editTextBerat);
		maps.put("BB", editTextBerat);
		maps.put("BI", editTextBerat);
		maps.put("LL", editTextLila);
		try {
			kehamilan = new JSONArray(b.getString(PilihKehamilanActivity.KEHAMILAN_DIPILIH));
			labelIbu.setText(b.getString(SelectIbuActivity.NAMA_IBU));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e){
			e.printStackTrace();
		}
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
		buttonSimpan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new GetToken().execute();
				
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
					ReadInput2 u = (ReadInput2)readThreads.get(i);
					u.setRunning(false);
					readThreads.remove(u);
				}
			}
		});
		//new loginTask().execute();
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //this.registerReceiver(mReceiver, filter3);
        if(devices.size()>0)
        new ConnectBt2Task().execute();
		/*for(int i=0;i<devices.size();i++){
			new ConnectBT(devices.get(i)).execute();
		}*/
	}
	private class ConnectBt2Task extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			int loopFail=0;
			for(int i=0;i<devices.size();i++){
				Method m;
				try {
					m = devices.get(i).getClass().getMethod("createRfcommSocket", new Class[] {int.class});
					BluetoothSocket mBTSocket = (BluetoothSocket) m.invoke(devices.get(i), 1);
					mBTSocket.connect();
					Log.i("Ambil Socket",devices.get(i).getName());
					sockets.add(mBTSocket);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					loopFail++;
					if(loopFail<3){
						i--;
					}
					else
					{
						loopFail=0;
						Log.i("CONNECTION ERROR", "kebacut suwi device "+devices.get(i).getName()+" gagal koneksi, lanjut gan");
						Toast.makeText(getApplicationContext(), "device "+devices.get(i).getName()+" gagal koneksi", Toast.LENGTH_SHORT);
					}
					Log.i("CONNECTION ERROR", "device "+devices.get(i).getName()+" gagal koneksi");
					Toast.makeText(activity, "device "+devices.get(i).getName()+" gagal koneksi", Toast.LENGTH_SHORT);
				}
			}
			int len = sockets.size();
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			ReadInput2 rr = new ReadInput2();
			readThreads.add(rr);
	        Thread t = new Thread(rr);
	        t.run();
	        return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
		}
		
	}
	public void onRadioButtonClicked(View view) {
	    boolean checked = ((RadioButton)view).isChecked();
		switch (view.getId()) {
		case R.id.radioNo:
			if(checked)
			kakiBengkak = false;
			break;
		case R.id.radioYes:
			if(checked)
			kakiBengkak = true;
			break;
		default:
			break;
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
	private class ReadInput2 implements Runnable{
		private boolean bStop = false;
		private Thread t;
		boolean threadStop=false;
		BluetoothSocket mBTSocket;
		Map<String,EditText> pp;
		StringHandler sh;
		boolean running;
		public void setRunning(boolean run){
			running = run;
			if(!run){
				for(int i=0;i<sockets.size();i++){
					try {
						sockets.get(i).close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		public ReadInput2() {
			
			running=true;
			pp=maps;
			sh = new StringHandler();
			
			//t = new Thread(this, "Input Thread");
			//t.start();
		}
		@Override
		public void run() {
			
			while(running){
				try{
				BluetoothDevice devX = devices.get(selectedDevice);
				}catch(ArrayIndexOutOfBoundsException ex){
					ex.printStackTrace();
					break;
				}
				int errorCount = 0;
				OutputStream os=null;
				InputStream is=null;
				while(true)
				try {
					mBTSocket = sockets.get(selectedDevice);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					if(!mBTSocket.isConnected())
						mBTSocket.connect();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					os = mBTSocket.getOutputStream();
					is = mBTSocket.getInputStream();
					/*os.write("SET AWAL\r\n".getBytes("ASCII"));
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					Log.i("Senfing Message","GN\r\n");
					os.write("GN\r\n".getBytes("ASCII"));
					byte[] buff = new byte[256];

					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int buffIdx=0;
					while(buffIdx<256){
						if(is.available()>0){
						int c = is.read();
						buff[buffIdx] = (byte)c;
						buffIdx++;
						}
					}
					
					try {
						//Thread.sleep(500);
						os.write("ST\r\n".getBytes("ASCII"));
						//is.reset();
						//is.close();
						//os.close();
						//mBTSocket.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					} /*catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
					selectedDevice = (selectedDevice+1)%sockets.size();
					int i = 0;
					for (i = 0; i < buff.length && buff[i] != 0; i++) {
					}
					final String strInput = new String(buff, 0, i);
					int g=0;
					String[] lines = strInput.split("\r\n");
					try{							
						String gt = lines[g].split("\\s")[0];
						if(lines[g].isEmpty()|| !maps.containsKey(gt))
						{
							g++;
						}
						
						String ll = new String(lines[g].split("\\s")[0]);
						final EditText curr = maps.get(ll);
						curr.post(new Runnable() {
						
						@Override
						public void run() {
							Log.i("SENSOR", strInput);
							String j = sh.Handle(strInput);
							if(!j.isEmpty())
							curr.setText(j);
						}
					});
					break;	
				}
					catch(Exception ex){
						ex.printStackTrace();
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					errorCount++;
					e.printStackTrace();
					if(errorCount>9){
						break;
					}else{
						continue;
					}					
				}
			
			}		
			
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
	protected class GetToken extends AsyncTask<Void, Void, Void>{
		JSONObject rr;
		String strMessage;
		protected String tokenUrl;
		public GetToken() {
			tokenUrl = IbuActivity.FORM_KUNJUNGAN_TOKEN_URL;
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				JSONObject form = (JSONObject) HttpClient.SendHttpGet(prefs.getString(PreferencesEditor.SERVER_URL, "")+tokenUrl);
				Log.i("JSON",form.toString());
				sessid = form.getString("sessid");
				token = form.getString("token");
				Log.d("TOKEN", token);
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
			new SendData().execute();
			super.onPostExecute(result);
		}
	}
	protected class SendData extends AsyncTask<Void, Void, Void>{
		JSONObject o = new JSONObject();
		protected String submitUrl;
		public SendData() {
			submitUrl = IbuActivity.FORM_ACTION;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if(pd==null){
				pd = new ProgressDialog(activity);
			}
			pd.setCanceledOnTouchOutside(false);
			pd.setTitle("Sedang mengirim");
			pd.show();
			super.onPreExecute();
		}
		JSONObject l;
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			try {
				o.put("token", token);
				o.put("kaki_bengkak", kakiBengkak?"1":"2");
				o.put("berat", editTextBerat.getText());
				l = (JSONObject)HttpClient.SendHttpPost(prefs.getString(PreferencesEditor.SERVER_URL, "")+submitUrl+kehamilan.getString(0), o);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Toast.makeText(activity, l.getString("pesan"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
