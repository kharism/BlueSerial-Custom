package com.blueserial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PilihKehamilanActivity extends Activity {
	ListView listKehamilan;
	Button buttonPilihKehamilan;
	String idIbu;
	String namaIbu;
	JSONArray jsonListKehamilan;
	Activity activity;
	JSONAdapter mja;
	public static final String LIST_KEHAMILAN="http://gia.karyateknologiinformasi.com/ws/bumil/hamil/";
	public static final String KEHAMILAN_DIPILIH="KEHAMILAN_DIPILIH";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		setContentView(R.layout.activity_pilih_kehamilan);
		activity = this;
		listKehamilan = (ListView)findViewById(R.id.listKehamilan);
		buttonPilihKehamilan = (Button)findViewById(R.id.buttonPilihKehamilan);
		idIbu = intent.getExtras().getString(SelectIbuActivity.ID_IBU);
		namaIbu = intent.getExtras().getString(SelectIbuActivity.NAMA_IBU);
		buttonPilihKehamilan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONArray kehamilan = mja.getItem(mja.getSelectedId());
				Intent i = new Intent(getApplicationContext(),Homescreen.class);
				i.putExtra(PilihKehamilanActivity.KEHAMILAN_DIPILIH, kehamilan.toString());
				i.putExtra(SelectIbuActivity.NAMA_IBU, namaIbu);
				startActivity(i);
			}
		});
		new GetKehamilanTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pilih_kehamilan, menu);
		return true;
	}
	private class GetKehamilanTask extends AsyncTask<Void, Void, Void>{
		JSONObject rr;
		ProgressDialog pd;
		protected void onPreExecute() {
			if(pd==null){
				pd = new ProgressDialog(activity);
				pd.setTitle("Coba Ambil Data");
			}
			pd.show();
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... params) {
			rr = (JSONObject)HttpClient.SendHttpGet(PilihKehamilanActivity.LIST_KEHAMILAN+idIbu);
			try {
				jsonListKehamilan = (JSONArray) rr.getJSONArray("aaData");
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						List<JSONArray> hh = new ArrayList<JSONArray>();
						for(int i=0;i<jsonListKehamilan.length();i++){
							try {
								hh.add((JSONArray)jsonListKehamilan.get(i));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}					
						}
						mja = new JSONAdapter(activity, R.id.listKehamilan, hh);
						listKehamilan.setAdapter(mja);
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	private class JSONAdapter extends ArrayAdapter<JSONArray>{
		HashMap<JSONArray, Integer> mIdMap = new HashMap<JSONArray, Integer>();
		List<JSONArray> jlist;
		private Context context;
		private int selectedId;
		public JSONAdapter(Context context, int textViewResourceId,
		        List<JSONArray> objects) {
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
	      JSONArray item = getItem(position);
	      return mIdMap.get(item);
	    }
		@Override
		public JSONArray getItem(int position) {
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
			JSONArray jj = jlist.get(position);
			try {
				tv.setText(jj.getString(0));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(position == selectedId){
				vi.setBackgroundColor(Color.GRAY);
			}
			return vi;
		}
		
		
	}
}
