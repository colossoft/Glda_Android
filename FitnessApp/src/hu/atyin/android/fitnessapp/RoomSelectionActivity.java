package hu.atyin.android.fitnessapp;

import hu.atyin.android.fitnessapp.adapter.RoomListAdapter;
import hu.atyin.android.fitnessapp.model.FitnessRoom;
import hu.atyin.android.fitnessapp.session.SessionManager;
import hu.atyin.android.fitnessapp.volley.AppController;
import hu.atyin.android.fitnessapp.volley.CustomJsonRequest;
import hu.atyin.android.fitnessapp.volley.UrlCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class RoomSelectionActivity extends ActionBarActivity {

	private ActionBar actionbar;
	
	private ArrayList<FitnessRoom> rooms;
	
	private int gymId;
	private String gymTitle;
	
	private ListView roomsList;
	private RoomListAdapter roomsAdapter;
	
	private ProgressDialog pDialog;
	
	SessionManager session;
	
	String tag_rooms_json_obj = "rooms_json_obj_req";
	
	private HashMap<String, String> userDetails;
	
	private CustomJsonRequest roomsJsonObjReq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_selection_activity_layout);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			gymId = extras.getInt("GYM_ID");
			gymTitle = extras.getString("GYM_TITLE");
		}
		
		session = new SessionManager(RoomSelectionActivity.this);
		
		userDetails = session.getUserDetails();
		
		// Actionbar referencia megszerzése
		actionbar = getSupportActionBar();
		
		// Actionbar vissza gomb bekapcs
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		// Actionbar cím beállítása
		actionbar.setTitle(gymTitle);
		
		// Actionbar háttér beállítás
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ce3334")));
		
		// Actionbar ikon beállítás
		actionbar.setIcon(R.drawable.gilda_icon_v2);
		
		roomsList = (ListView) findViewById(R.id.trainingsList);
		
		rooms = new ArrayList<FitnessRoom>();
		
		// Termek letöltése
		pDialog = new ProgressDialog(RoomSelectionActivity.this);
		pDialog.setCancelable(false);
		pDialog.setMessage("Termek letöltése...");
		pDialog.show();
		
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> params = new HashMap<String, String>();
		headers.put("Authorization", userDetails.get("api_key"));
		
		roomsJsonObjReq = new CustomJsonRequest(Method.GET, UrlCollection.GET_ROOMS_URL + gymId, params, headers, 
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("FITNESS", response.toString());
						pDialog.dismiss();
						
						try {
							if(response.getBoolean("error")) {
								showErrorAlert();
							}
							else {
								JSONArray roomsJsonArray = response.getJSONArray("rooms");
								
								for (int i = 0; i < roomsJsonArray.length(); i++) {
									JSONObject act = roomsJsonArray.getJSONObject(i);
									
									rooms.add(new FitnessRoom(act.getInt("id"), act.getString("name")));
								}
								
								roomsAdapter = new RoomListAdapter(RoomSelectionActivity.this, R.layout.trainings_row_layout, rooms);
								roomsList.setAdapter(roomsAdapter);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showErrorAlert();
						}
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("FITNESS", "Error: " + error.getMessage());
						pDialog.dismiss();
						showErrorAlert();
					}
				});
		
		AppController.getInstance().addToRequestQueue(roomsJsonObjReq, tag_rooms_json_obj);
		
		roomsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent reservationIntent = new Intent(RoomSelectionActivity.this, ReservationActivity.class);
				
				reservationIntent.putExtra("GYM_TITLE", gymTitle);
				reservationIntent.putExtra("ROOM_ID", rooms.get(position).getId());
				reservationIntent.putExtra("ROOM_TITLE", rooms.get(position).getName());
				
				startActivity(reservationIntent);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
	
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void showErrorAlert() {
		new AlertDialog.Builder(RoomSelectionActivity.this).setTitle("Hiba").setMessage("Sajnos nem sikerült letölteni a termeket!")
			.setNegativeButton("Vissza", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			})
			.setPositiveButton("Újra", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					pDialog.setMessage("Termek letöltése...");
					pDialog.show();
					
					AppController.getInstance().addToRequestQueue(roomsJsonObjReq, tag_rooms_json_obj);
				}
			}).show();
	}
}
