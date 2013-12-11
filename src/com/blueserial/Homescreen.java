/*
 * Released under MIT License http://opensource.org/licenses/MIT
 * Copyright (c) 2013 Plasty Grove
 * Refer to file LICENSE or URL above for full text 
 */

package com.blueserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blueserial.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Homescreen extends Activity {

	private Button mBtnSearch;
	private Button mBtnConnect;
	private ListView mLstDevices;
	private TextView heading;
	private BluetoothAdapter mBTAdapter;
	private Button mButtonIbu;
	private Button mButtonAnak;
	private String namaIbu;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayList<Short> RSID;
    private JSONArray kehamilan;
    SharedPreferences prefs;
    private ArrayList<BluetoothDevice> devList;
    static ArrayList<BluetoothDevice> staticDevList;
    private ArrayList<Short> staticRSID;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        
    	@Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress()+String.valueOf(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)));
                if(!devList.contains(device)){
	                devList.add(device);
	                RSID.add(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
	            }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("Search Device");
                heading.setText("Finished");
    			mBtnSearch.setEnabled(true);
                
                MyAdapter adapter = (MyAdapter) mLstDevices.getAdapter();
				adapter.replaceItems(devList);
				//devList=new ArrayList<BluetoothDevice>();
            }
        }
    };

	private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
	private static final int SETTINGS = 20;

	private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SPP UUID
	// (http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createInsecureRfcommSocketToServiceRecord%28java.util.UUID%29)

	private int mBufferSize = 50000; //Default
	private JSONObject anak;
	public static final String DEVICE_EXTRA = "com.blueserial.SOCKET";
	public static final String DEVICE_UUID = "com.blueserial.uuid";
	private static final String DEVICE_LIST = "com.blueserial.devicelist";
	private static final String DEVICE_LIST_SELECTED = "com.blueserial.devicelistselected";
	public static final String BUFFER_SIZE = "com.blueserial.buffersize";
	private static final String TAG = "BlueTest5-Homescreen";
	public static final String DEVICES_LISTS = "com.blueserial.devices";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RSID = new ArrayList<Short>();
		prefs = this.getSharedPreferences(
			      "com.blueserial", Context.MODE_PRIVATE);
		setContentView(R.layout.activity_homescreen);
		ActivityHelper.initialize(this); //This is to ensure that the rotation persists across activities and not just this one
		Log.d(TAG, "Created");
		Intent intent = getIntent();
		devList = new ArrayList<BluetoothDevice>();
		mBtnSearch = (Button) findViewById(R.id.btnSearch);
		//mBtnConnect = (Button) findViewById(R.id.btnConnect);
		mButtonAnak = (Button) findViewById(R.id.buttonAnak);
		mButtonIbu = (Button) findViewById(R.id.buttonIbu);
		heading = (TextView) findViewById(R.id.txtListHeading);
		mLstDevices = (ListView) findViewById(R.id.lstDevices);
		
		mButtonIbu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<BluetoothDevice> devices;
				if(((MyAdapter) (mLstDevices.getAdapter())).selectedIndex>=0){
					devices = new ArrayList<BluetoothDevice>();
					devices.add(((MyAdapter) (mLstDevices.getAdapter())).getSelectedItem());
					Intent intent = new Intent(getApplicationContext(), IbuActivity.class);
					intent.putExtra(DEVICES_LISTS, devices);
					try{
						intent.putExtra(PilihKehamilanActivity.KEHAMILAN_DIPILIH, kehamilan.toString());
						intent.putExtra(SelectIbuActivity.NAMA_IBU, namaIbu);
					}catch(NullPointerException ex){}
					intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
					intent.putExtra(BUFFER_SIZE, mBufferSize);
					startActivity(intent);
				}
				else if(mLstDevices!=null){
				devices = (ArrayList<BluetoothDevice>) ((MyAdapter) (mLstDevices.getAdapter())).getEntireList();
				Intent intent = new Intent(getApplicationContext(), IbuActivity.class);
				intent.putExtra(DEVICES_LISTS, devices);
				try{
					intent.putExtra(PilihKehamilanActivity.KEHAMILAN_DIPILIH, kehamilan.toString());
					intent.putExtra(SelectIbuActivity.NAMA_IBU, namaIbu);
				}catch(NullPointerException ex){}
				
				intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
				intent.putExtra(BUFFER_SIZE, mBufferSize);
				startActivity(intent);
				}
			}
		});
		
		if(intent.getExtras().containsKey(PilihKehamilanActivity.KEHAMILAN_DIPILIH))
		try {
			kehamilan = new JSONArray(intent.getExtras().getString(PilihKehamilanActivity.KEHAMILAN_DIPILIH));
			mButtonAnak.setVisibility(View.INVISIBLE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		else if(intent.getExtras().containsKey(SelectAnakActivity.ID_ANAK)){
			try {
				anak = new JSONObject(intent.getExtras().getString(SelectAnakActivity.ID_ANAK));
				mButtonIbu.setVisibility(View.INVISIBLE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(intent.getExtras().containsKey(RegistrasiIbu.REGISTRASI_IBU)){
			mButtonAnak.setVisibility(View.INVISIBLE);
			mButtonIbu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					ArrayList<BluetoothDevice> devices;
					devices = new ArrayList<BluetoothDevice>();
					devices = (ArrayList<BluetoothDevice>) ((MyAdapter) (mLstDevices.getAdapter())).getEntireList();
					
					Intent i = new Intent(getApplicationContext(),RegistrasiIbu.class);
					i.putExtra(DEVICES_LISTS, devices);
					i.putExtra(DEVICE_UUID, mDeviceUUID.toString());
					i.putExtra(BUFFER_SIZE, mBufferSize);
					startActivity(i);
				}
			});
		}
		
		if(intent.getExtras().containsKey(SelectIbuActivity.NAMA_IBU)){
			namaIbu = intent.getExtras().getString(SelectIbuActivity.NAMA_IBU);
		}
		/*
		 *Check if there is a savedInstanceState. If yes, that means the onCreate was probably triggered by a configuration change
		 *like screen rotate etc. If that's the case then populate all the views that are necessary here 
		 */
		if (savedInstanceState != null) {
			ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
			if(list!=null){
				initList(list);
				MyAdapter adapter = (MyAdapter)mLstDevices.getAdapter();
				int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
				if(selectedIndex != -1){
					adapter.setSelectedIndex(selectedIndex);
					//mBtnConnect.setEnabled(true);
				}
			} else {
				initList(new ArrayList<BluetoothDevice>());
			}
			
		} else {
			initList(new ArrayList<BluetoothDevice>());
		}
		//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        //filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //this.registerReceiver(mReceiver, filter);

		mBtnSearch.setOnClickListener(new OnClickListener() {
			List<BluetoothDevice> listDevices;
			@Override
			public void onClick(View arg0) {
				mBTAdapter = BluetoothAdapter.getDefaultAdapter();
				devList = new ArrayList<BluetoothDevice>();
				RSID = new ArrayList<Short>();
				heading.setText("Searching");
				mBtnSearch.setEnabled(false);
				if (mBTAdapter == null) {
					Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
				}else if (!mBTAdapter.isEnabled()) {
					Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBT, BT_ENABLE_REQUEST);
				} else {
					//new SearchDevices().execute();
					mBTAdapter.startDiscovery();
				}
				
			}
		});
		mButtonAnak.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<BluetoothDevice> devices;
				/*if(((MyAdapter) (mLstDevices.getAdapter())).selectedIndex>=0){
					devices = new ArrayList<BluetoothDevice>();
					devices.add(((MyAdapter) (mLstDevices.getAdapter())).getSelectedItem());
					Intent intent = new Intent(getApplicationContext(), ActivityAnak.class);
					intent.putExtra(DEVICES_LISTS, devices);
					try{
					intent.putExtra(SelectAnakActivity.ID_ANAK, anak.toString());
					}catch(NullPointerException ex){}
					intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
					intent.putExtra(BUFFER_SIZE, mBufferSize);
					startActivity(intent);
				}
				else*/ 
				if(mLstDevices!=null){
					devices = (ArrayList<BluetoothDevice>) ((MyAdapter) (mLstDevices.getAdapter())).getEntireList();					
					Intent intent = new Intent(getApplicationContext(), ActivityAnak.class);
					intent.putExtra(DEVICES_LISTS, devices);
					try{
					intent.putExtra(SelectAnakActivity.ID_ANAK, anak.toString());
					}catch(NullPointerException ex){}
					intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
					intent.putExtra(BUFFER_SIZE, mBufferSize);
					startActivity(intent);
				}				
			}
		});
		
		
		/*mBtnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				List<BluetoothDevice> devices = ((MyAdapter) (mLstDevices.getAdapter())).getEntireList();
				for(int i=0;i<devices.size();i++){
					BluetoothDevice device = devices.get(i);
					msg(device.getName());
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.putExtra(DEVICE_EXTRA, device);
					intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
					intent.putExtra(BUFFER_SIZE, mBufferSize);
					startActivity(intent);
				}
				
			}
		});*/
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
	}
	
	/**
	 * Called when the screen rotates. If this isn't handled, data already generated is no longer available
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		MyAdapter adapter = (MyAdapter) (mLstDevices.getAdapter());
		ArrayList<BluetoothDevice> list = (ArrayList<BluetoothDevice>) adapter.getEntireList();
		
		if (list != null) {
			outState.putParcelableArrayList(DEVICE_LIST, list);
			int selectedIndex = adapter.selectedIndex;
			outState.putInt(DEVICE_LIST_SELECTED, selectedIndex);
		}
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mReceiver);
		
		Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
		((MyApplication)getApplication()).setStaticDevList(devList);
		((MyApplication)getApplication()).setStaticRSID(RSID);
		super.onPause();
	}
	@Override
	protected void onResume() {
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		Toast.makeText(getApplicationContext(), "resume", Toast.LENGTH_SHORT).show();
		if(((MyApplication)getApplication()).getStaticDevList()!=null){
			devList = ((MyApplication)getApplication()).getStaticDevList();
			RSID = ((MyApplication)getApplication()).getStaticRSID();
			((MyAdapter)mLstDevices.getAdapter()).replaceItems(((MyApplication)getApplication()).getStaticDevList());			
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		
		super.onStop();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BT_ENABLE_REQUEST:
			if (resultCode == RESULT_OK) {
				msg("Bluetooth Enabled successfully");
				new SearchDevices().execute();
			} else {
				msg("Bluetooth couldn't be enabled");
			}

			break;
		case SETTINGS: //If the settings have been updated
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String uuid = prefs.getString("prefUuid", "Null");
			mDeviceUUID = UUID.fromString(uuid);
			Log.d(TAG, "UUID: " + uuid);
			String bufSize = prefs.getString("prefTextBuffer", "Null");
			mBufferSize = Integer.parseInt(bufSize);

			String orientation = prefs.getString("prefOrientation", "Null");
			Log.d(TAG, "Orientation: " + orientation);
			if (orientation.equals("Landscape")) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else if (orientation.equals("Portrait")) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if (orientation.equals("Auto")) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Quick way to call the Toast
	 * @param str
	 */
	private void msg(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Initialize the List adapter
	 * @param objects
	 */
	private void initList(List<BluetoothDevice> objects) {
		final MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.list_item, R.id.lstContent, objects);
		mLstDevices.setAdapter(adapter);
		mLstDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setSelectedIndex(position);
				//mBtnConnect.setEnabled(true);
			}
		});
	}
	private class CalibrateCalipherTask extends AsyncTask<Void, Void, Void>{
		BluetoothDevice bd;
		public CalibrateCalipherTask(BluetoothDevice bd){
			this.bd = bd;
		}
		String mes = "sukses";
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			BluetoothSocket mBTSocket;
			Method m;
			try {
				m = bd.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
				mBTSocket = (BluetoothSocket) m.invoke(bd, 1);
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
				mBTSocket.connect();
				OutputStream os = mBTSocket.getOutputStream();
				byte[] buff ="SA\r\n".getBytes("ASCII"); 
				os.write(buff);				
				os.close();
				mBTSocket.close();
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mes = e.getMessage();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			msg(mes);
			super.onPostExecute(result);
		}
		
	}

	/**
	 * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
	 * will show up with this. I didn't see any need to re-build the wheel over here
	 * @author ryder
	 *
	 */
	private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {
		List<BluetoothDevice> listDevices;
		@SuppressLint("NewApi")
		@Override
		protected List<BluetoothDevice> doInBackground(Void... params) {
			Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
			listDevices = new ArrayList<BluetoothDevice>();
			for (BluetoothDevice device : pairedDevices) {
				BluetoothSocket mBTSocket;
				try {
					
					Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});					
					mBTSocket = (BluetoothSocket) m.invoke(device, 1);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					mBTSocket.connect();
					listDevices.add(device);
					mBTSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//msg(device.getName()+" tidak bisa dihubungkan");
					e.printStackTrace();
					continue;
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
				}
				
			}
			
		
			/*mBTAdapter.startDiscovery();
			BroadcastReceiver mReceiver = new BroadcastReceiver() {
				public void onReceive(Context context, Intent intent) {
				    String action = intent.getAction();
				    if (BluetoothDevice.ACTION_FOUND.equals(action)) 
				    {
				        // Get the BluetoothDevice object from the Intent
				        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				        // Add the name and address to an array adapter to show in a ListView
				        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				        listDevices.add(device);
				    }
				  }
			};
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 
			registerReceiver(mReceiver, filter);*/
			return listDevices;
		}

		@Override
		protected void onPostExecute(List<BluetoothDevice> listDevices) {
			super.onPostExecute(listDevices);
			heading.setText("Finished");
			mBtnSearch.setEnabled(true);
			if (listDevices.size() > 0) {
				MyAdapter adapter = (MyAdapter) mLstDevices.getAdapter();
				adapter.replaceItems(listDevices);
			} else {
				msg("No paired devices found, please pair your serial BT device and try again");
			}
		}

	}
	
	private class GetBateryLevelTask extends AsyncTask<Void, Void, Void>{
		BluetoothDevice bd;
		String mes;
		public GetBateryLevelTask(BluetoothDevice d) {
			bd = d;
		}
		@Override
		protected Void doInBackground(Void... params) {
			BluetoothSocket mBTSocket;
			Method m;
			try {
				m = bd.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
				mBTSocket = (BluetoothSocket) m.invoke(bd, 1);
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
				mBTSocket.connect();
				OutputStream os = mBTSocket.getOutputStream();
				InputStream is = mBTSocket.getInputStream();
				byte[] buff ="BL\r\n".getBytes("ASCII"); 
				os.write(buff);				
				buff = new byte[100];
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int h=0;
				while(h<100){
					if(is.available()>0){
						int gg = is.read();
						buff[h] = (byte)gg;
						h++;
						if(gg=='\n')break;
					}
					else break;
				}
				String res = new String(buff);
				mes = res.substring(0, h);
				is.close();
				os.close();
				mBTSocket.close();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
					}
				});
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mes = e.getMessage();
			}
			return null;			
		}
		
	}

	/**
	 * Custom adapter to show the current devices in the list. This is a bit of an overkill for this 
	 * project, but I figured it would be good learning
	 * Most of the code is lifted from somewhere but I can't find the link anymore
	 * @author ryder
	 *
	 */
	private class MyAdapter extends ArrayAdapter<BluetoothDevice> {
		private int selectedIndex;
		private Context context;
		private int selectedColor = Color.parseColor("#abcdef");
		private List<BluetoothDevice> myList;

		public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
			super(ctx, resource, textViewResourceId, objects);
			context = ctx;
			myList = objects;
			selectedIndex = -1;
		}

		public void setSelectedIndex(int position) {
			selectedIndex = position;
			notifyDataSetChanged();
		}

		public BluetoothDevice getSelectedItem() {
			return myList.get(selectedIndex);
		}

		@Override
		public int getCount() {
			return myList.size();
		}

		@Override
		public BluetoothDevice getItem(int position) {
			return myList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			TextView tv;
		}

		public void replaceItems(List<BluetoothDevice> list) {
			myList = list;
			notifyDataSetChanged();
		}
		@Override
		public void remove(BluetoothDevice object) {
			// TODO Auto-generated method stub
			myList.remove(object);
			super.remove(object);
		}

		public List<BluetoothDevice> getEntireList() {
			return myList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			ViewHolder holder;
			if (convertView == null) {
				vi = LayoutInflater.from(context).inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.tv = (TextView) vi.findViewById(R.id.lstContent);
				vi.setTag(holder);
			} else {
				holder = (ViewHolder) vi.getTag();
			}

			if (selectedIndex != -1 && position == selectedIndex) {
				holder.tv.setBackgroundColor(selectedColor);
			} else {
				holder.tv.setBackgroundColor(Color.WHITE);
			}
			BluetoothDevice device = myList.get(position);
			try{
				String h = String.valueOf(RSID.get(position));
				holder.tv.setText(device.getName() + "\n   " +h);
			}catch(IndexOutOfBoundsException ex){
				ex.printStackTrace();
			}
			return vi;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.homescreen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_batery_level:
			BluetoothDevice selected2 = ((MyAdapter)mLstDevices.getAdapter()).getSelectedItem();
			GetBateryLevelTask t2 = new GetBateryLevelTask(selected2);
			t2.execute();
			break;
		case R.id.action_settings:
			Intent intent = new Intent(Homescreen.this, PreferencesActivity.class);
			startActivityForResult(intent, SETTINGS);
			break;
		case R.id.action_calibrate_tinggi:
			BluetoothDevice selected = ((MyAdapter)mLstDevices.getAdapter()).getSelectedItem();
			CalibrateCalipherTask t = new CalibrateCalipherTask(selected);
			t.execute();
			break;
		case R.id.action_remove_item:
			BluetoothDevice selected3 = ((MyAdapter)mLstDevices.getAdapter()).getSelectedItem();
			MyAdapter adapter = (MyAdapter) mLstDevices.getAdapter();
			adapter.remove(selected3);
			adapter.notifyDataSetChanged();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
