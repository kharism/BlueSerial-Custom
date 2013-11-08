package com.blueserial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class SelectIbuActivity extends Activity {
	public static String LIST_IBU = "http://gia.karyateknologiinformasi.com/andro/antro/ibu";
	public static String ID_IBU="com.bullshitdiarrha";
	public static String NAMA_IBU="com.bullshitdiarrha.name";
	public static String LOGIN_IBU = "http://gia.karyateknologiinformasi.com/ws/usr/login";
	Activity activity;
	boolean isLogedIn = false;
	JSONAdapter mja;
	ListView lv;
	ProgressDialog pd;
	String sessid;
	JSONArray listIbu;
	String token;
	Button selectButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_select_ibu);
		lv = (ListView)findViewById(R.id.listIbu);
		selectButton = (Button)findViewById(R.id.selectButton);
		selectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), PilihKehamilanActivity.class);
				try {
					intent.putExtra(SelectIbuActivity.ID_IBU, mja.getItem(mja.getSelectedId()).getString("id"));
					intent.putExtra(SelectIbuActivity.NAMA_IBU, mja.getItem(mja.getSelectedId()).getString("nama"));
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.i("pass error", "gagal dapat id ibu");
					e.printStackTrace();
				}
			}
		});
		new loginTask().execute();
		//pd = new ProgressDialog(getApplicationContext());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_ibu, menu);
		return true;
	}
	private class loginTask extends AsyncTask<Void, Void, Void>{
		JSONObject rr;
		String strMessage;
		@Override
		protected void onPreExecute() {
			if(pd==null){
				pd = new ProgressDialog(activity);
				pd.setTitle("Coba Login");
			}
			pd.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Map<String,String> login = new HashMap<String, String>();
			login.put("username", "admin");
			login.put("password", "admin");
			JSONObject o = new JSONObject(login);
			rr = (JSONObject)HttpClient.SendHttpPost(SelectIbuActivity.LOGIN_IBU, o);
			
			try {
				Log.i("JSON", rr.toString());
				JSONObject message;
				message = ((JSONObject)rr.get("message"));
				//activity.setTitle(message.getString("pesan"));
				strMessage = message.getString("pesan");
				if(message.getString("tipe").equals("success")||(message.getString("tipe").equals("error")&&message.getString("pesan").equalsIgnoreCase("Username sudah login"))){
					isLogedIn = true;
					JSONObject form = (JSONObject) HttpClient.SendHttpGet(IbuActivity.FORM_KUNJUNGAN_TOKEN_URL);
					Log.i("JSON",form.toString());
					sessid = form.getString("sessid");
					token = form.getString("token");
					Log.d("TOKEN", token);
					listIbu = (JSONArray)HttpClient.SendHttpGet(SelectIbuActivity.LIST_IBU);
					if(listIbu!=null){
					    activity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								ArrayList<JSONObject> list = new ArrayList<JSONObject>();
							    for(int i=0;i<listIbu.length();i++){
							    	try {
										list.add((JSONObject)listIbu.get(i));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							    }
							    mja = new JSONAdapter(activity, R.layout.list_item, list);
							    lv.setAdapter(mja);
							    lv.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
											mja.setSelectedId(position);
											mja.notifyDataSetChanged();
									}
							    	
								});
							}
						});
						
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				activity.runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show();					
					}
				});
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
			pd.dismiss();
			super.onPostExecute(result);
		}
	}
	private class JSONAdapter extends ArrayAdapter<JSONObject>{
		HashMap<JSONObject, Integer> mIdMap = new HashMap<JSONObject, Integer>();
		List<JSONObject> jlist;
		private Context context;
		private int selectedId;
		public JSONAdapter(Context context, int textViewResourceId,
		        List<JSONObject> objects) {
			super(context,textViewResourceId);
			this.context = context;
			jlist = objects;
			for (int i = 0; i < objects.size(); ++i) {
		        mIdMap.put(objects.get(i), i);
		  } 
		}
		public void setSelectedId(int selectedId) {
			this.selectedId = selectedId;
		}
		public int getSelectedId() {
			return selectedId;
		}
		@Override
	    public long getItemId(int position) {
	      JSONObject item = getItem(position);
	      return mIdMap.get(item);
	    }
		@Override
		public JSONObject getItem(int position) {
			// TODO Auto-generated method stub
			return jlist.get(position);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jlist.size();
		}

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }
		private class ViewHolder {
			TextView tv;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View vi = convertView;
			ViewHolder holder;
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item, null);
			TextView tv = (TextView)vi.findViewById(R.id.lstContent);
			JSONObject jj = jlist.get(position);
			try {
				tv.setText(jj.getString("nama"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(position == selectedId){
				vi.setBackgroundColor(Color.GRAY);
			}
			return vi;
		}
		
		
	}

}
