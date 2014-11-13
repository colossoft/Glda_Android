package hu.atyin.android.fitnessapp;

import hu.atyin.android.fitnessapp.adapter.MyReservationsAdapter;
import hu.atyin.android.fitnessapp.adapter.MyReservationsAdapter.IRefreshMyReservations;
import hu.atyin.android.fitnessapp.model.Reservation;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class MyReservationsActivity extends ActionBarActivity implements IRefreshMyReservations {
	
	private ListView myReservationsList;
	private MyReservationsAdapter myReservationsListAdapter;
	
	private ArrayList<Reservation> myReservations;
	
	private ActionBar actionbar;
	
	private ProgressDialog pDialog;
	
	SessionManager session;
	
	private HashMap<String, String> userDetails;
	
	String tag_get_reservations_json_obj = "get_reservations_json_obj_req";
	
	private CustomJsonRequest getReservationsJsonObjReq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_reservations_activity_layout);
		
		session = new SessionManager(MyReservationsActivity.this);
		
		userDetails = session.getUserDetails();
		
		// View referenciák elkérése
		myReservationsList = (ListView) findViewById(R.id.myReservationsList);
		
		// Actionbar referencia megszerzése
		actionbar = getSupportActionBar();
		
		// Actionbar vissza gomb bekapcs
		actionbar.setDisplayHomeAsUpEnabled(true);		
		
		// Actionbar cím beállítása
		actionbar.setTitle(R.string.app_my_reservations_title);
		
		// Actionbar háttér beállítás
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ce3334")));
		
		// Actionbar ikon beállítás
		actionbar.setIcon(R.drawable.gilda_icon_v2);
		
		myReservations = new ArrayList<Reservation>();
		
		// Foglalások letöltése
		pDialog = new ProgressDialog(MyReservationsActivity.this);
		pDialog.setCancelable(false);
		pDialog.setMessage(getString(R.string.app_my_reservations_downloadingReservations));
		pDialog.show();
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", userDetails.get("api_key"));
		SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		String settedValue = prefs.getString("Language", null);
		if(settedValue != null) {
			headers.put("Accept-Language", settedValue);
		}
		
		Map<String, String> params = new HashMap<String, String>();
		
		getReservationsJsonObjReq = new CustomJsonRequest(MyReservationsActivity.this, Method.GET, UrlCollection.GET_RESERVATIONS_OF_USER_URL, params, headers, 
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						pDialog.dismiss();
						
						try {
							myReservations.clear();
							
							JSONArray myReservationsJsonArray = response.getJSONArray("reservations");
							
							if(myReservationsJsonArray.length() == 0) {
								new AlertDialog.Builder(MyReservationsActivity.this).setTitle(R.string.app_my_reservations_title).setMessage(R.string.app_my_reservations_noReservations)
									.setNeutralButton("OK", new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									}).show();
							}
							else {
								for (int i = 0; i < myReservationsJsonArray.length(); i++) {
									JSONObject act = myReservationsJsonArray.getJSONObject(i);
									
									myReservations.add(new Reservation(act.getInt("id"), 
																	   act.getString("reservationTime"), 
																	   act.getString("date"), 
																	   act.getString("startTime"), 
																	   act.getString("endTime"), 
																	   act.getString("trainerName"), 
																	   act.getString("trainingName"), 
																	   act.getBoolean("past")));
								}
								
								myReservationsListAdapter = new MyReservationsAdapter(MyReservationsActivity.this, R.layout.my_reservations_row_layout, myReservations);
								myReservationsList.setAdapter(myReservationsListAdapter);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						pDialog.dismiss();
						
						new AlertDialog.Builder(MyReservationsActivity.this).setTitle(R.string.app_error_title).setMessage(error.getMessage())
							.setNegativeButton(R.string.app_btnBack_Title, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									finish();
								}
							})
							.setPositiveButton(R.string.app_btnAgain_Title, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									pDialog.setMessage(getString(R.string.app_my_reservations_downloadingReservations));
									pDialog.show();
									
									AppController.getInstance().addToRequestQueue(getReservationsJsonObjReq, tag_get_reservations_json_obj);
								}
							}).show();
					}
				});
		
		AppController.getInstance().addToRequestQueue(getReservationsJsonObjReq, tag_get_reservations_json_obj);
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

	@Override
	public void refreshMyReservations() {
		pDialog = new ProgressDialog(MyReservationsActivity.this);
		pDialog.setCancelable(false);
		pDialog.setMessage(getString(R.string.app_my_reservations_downloadingReservations));
		pDialog.show();
		
		AppController.getInstance().addToRequestQueue(getReservationsJsonObjReq, tag_get_reservations_json_obj);
	}
}
