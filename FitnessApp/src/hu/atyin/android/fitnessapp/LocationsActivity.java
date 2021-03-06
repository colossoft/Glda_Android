package hu.atyin.android.fitnessapp;

import hu.atyin.android.fitnessapp.adapter.LocationsListAdapter;
import hu.atyin.android.fitnessapp.model.FitnessLocation;
import hu.atyin.android.fitnessapp.model.MyLocation;
import hu.atyin.android.fitnessapp.model.MyLocation.LocationResult;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationsActivity extends ActionBarActivity {
	
	private GoogleMap googleMap;
	
	private ActionBar actionbar;
	
	private ArrayList<FitnessLocation> fitnessLocations;
	
	private ListView locationsList;
	private LocationsListAdapter adapter;
	
	private LocationResult locationResult;
	private LatLngBounds.Builder bounds;
	
	private ProgressDialog pDialog;
	
	SessionManager session;
	
	String tag_locations_json_obj = "locations_json_obj_req";
	
	private HashMap<String, String> userDetails;
	
	private CustomJsonRequest locationsJsonObjReq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locations_activity_layout);
		
		pDialog = new ProgressDialog(LocationsActivity.this);
		pDialog.setCancelable(false);
		
		session = new SessionManager(LocationsActivity.this);
		
		userDetails = session.getUserDetails();
		
		Log.d("FITNESS", userDetails.toString());
		
		// Referenci�k lek�r�se
		locationsList = (ListView) findViewById(R.id.locationsList);
		
		this.locationResult = new LocationResult() {
			@Override
			public void gotLocation(final Location location, Context ctx) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(location != null) {
							for (int i = 0; i < fitnessLocations.size(); i++) {
								float[] distResults = new float[5];
								Location.distanceBetween(location.getLatitude(), 
														location.getLongitude(), 
														fitnessLocations.get(i).getLatitude(), 
														fitnessLocations.get(i).getLongitude(), 
														distResults);
								
								fitnessLocations.get(i).setDistance(distResults[0]/1000);
							}
						}
						else {
							Toast.makeText(LocationsActivity.this, getString(R.string.app_location_positionGetFailed), Toast.LENGTH_LONG).show();
						}
						
						pDialog.dismiss();
						setListElements();
					}
				});
			}
		};
		
		locationsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LatLng pos = new LatLng(fitnessLocations.get(position).getLatitude(), fitnessLocations.get(position).getLongitude());
				if(googleMap != null) {
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
				}
			}
		});
		
		bounds = new LatLngBounds.Builder();
		
		// Teszt helysz�nek
		fitnessLocations = new ArrayList<FitnessLocation>();
		/*fitnessLocations.add(new FitnessLocation("Allee", "Budapest Pr�ter u. 61", 47.486775, 19.081819, 0.0));
		fitnessLocations.add(new FitnessLocation("�buda Gate", "Budapest Sass u. 8", 47.500460, 19.052079, 0.0));
		fitnessLocations.add(new FitnessLocation("River Estates", "Budapest G�bor �ron u. 54", 47.520781, 19.011556, 0.0));
		fitnessLocations.add(new FitnessLocation("Hermina Towers", "Budapest �nt�de u. 7", 47.428056, 19.061826, 0.0));
		fitnessLocations.add(new FitnessLocation("Fl�ri�n", "Budapest Bezer�di P�l u. 69", 47.553833, 19.121764, 0.0));
		fitnessLocations.add(new FitnessLocation("Savoya Park", "Budapest Toborz� u. 20", 47.557072, 19.080241, 0.0));
		*/
		
		// Actionbar referencia megszerz�se
		actionbar = getSupportActionBar();
		
		// Actionbar c�m be�ll�t�sa
		actionbar.setTitle(R.string.app_location_actionbarTitle);
		
		// Actionbar h�tt�r be�ll�t�s
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ce3334")));
		
		// Actionbar ikon be�ll�t�s
		actionbar.setIcon(R.drawable.gilda_icon_v2);
		
		
		try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		if(googleMap != null) {
			//googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
		}
		
		// Helysz�nek let�lt�se
		pDialog.setMessage(getString(R.string.app_location_loadingLocationsTitle));
		pDialog.show();
		
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> params = new HashMap<String, String>();
		headers.put("Authorization", userDetails.get("api_key"));
		SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		String settedValue = prefs.getString("Language", null);
		if(settedValue != null) {
			headers.put("Accept-Language", settedValue);
		}
		
		locationsJsonObjReq = new CustomJsonRequest(LocationsActivity.this, Method.GET, UrlCollection.GET_ALL_LOCATION_URL, params, headers, 
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("FITNESS", response.toString());
						pDialog.dismiss();
						
						try {
							JSONArray locationsJsonArray = response.getJSONArray("locations");
							
							for (int i = 0; i < locationsJsonArray.length(); i++) {
								JSONObject act = locationsJsonArray.getJSONObject(i);
								
								fitnessLocations.add(new FitnessLocation(act.getInt("id"), 
																		 act.getString("name"), 
																		 act.getString("address"), 
																		 act.getDouble("latitude"), 
																		 act.getDouble("longitude"), 
																		 0.0));
							}
							
							//if(!getLocationDatas()) {
							//	pDialog.dismiss();
							//	Toast.makeText(LocationsActivity.this, R.string.app_location_positionGetFailed, Toast.LENGTH_LONG).show();
								setListElements();
							//}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						pDialog.dismiss();
						showErrorAlert(error.getMessage());
					}
				});
		
		AppController.getInstance().addToRequestQueue(locationsJsonObjReq, tag_locations_json_obj);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.locations_activity_actions, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_my_reservations:
				myReservations();
				return true;
			case R.id.action_change_password:
				changePassword();
				return true;
			case R.id.action_setting:
				setting();
				return true;
			case R.id.action_logout:
				logoutUser();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		logoutUser();
	}
	
	public void showErrorAlert(String messageText) {
		new AlertDialog.Builder(LocationsActivity.this).setTitle(R.string.app_error_title).setMessage(messageText)
			.setNegativeButton(R.string.app_btnExit_Title, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					session.logoutUser();
				}
			})
			.setPositiveButton(R.string.app_btnAgain_Title, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					pDialog.setMessage(getString(R.string.app_location_loadingLocationsTitle));
					pDialog.show();
					
					AppController.getInstance().addToRequestQueue(locationsJsonObjReq, tag_locations_json_obj);
				}
			}).show();
	}
	
	private void myReservations() {
		Intent mrIntent = new Intent(LocationsActivity.this, MyReservationsActivity.class);
		startActivity(mrIntent);
	}
	
	private void changePassword() {
		Intent cpIntent = new Intent(LocationsActivity.this, ChangePasswordActivity.class);
		startActivity(cpIntent);
	}

	
	private void logoutUser() {
		new AlertDialog.Builder(LocationsActivity.this).setTitle(R.string.app_logout_title)
		.setMessage(R.string.app_location_quitQuestion)
		.setNegativeButton(R.string.app_btnNo_Title, null)
		.setPositiveButton(R.string.app_btnYesTitle, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				session.logoutUser();
			}
		}).show();
	}
	
	private void setting(){
		Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
		startActivity(settingIntent);
	}
	
	private void initializeMap() {
		if(googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
			
			if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.app_location_mapLoadingFailed), Toast.LENGTH_LONG)
                        .show();
            }
		}
	}
	
	public boolean getLocationDatas() {
		pDialog.setMessage(getString(R.string.app_location_findingPosition));
		pDialog.setCancelable(false);
		pDialog.show();
		
		MyLocation myLocation = new MyLocation(LocationsActivity.this);
		if(myLocation.getLocation(locationResult)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void setListElements() {
		adapter = new LocationsListAdapter(LocationsActivity.this, R.layout.location_row_layout, fitnessLocations);
		locationsList.setAdapter(adapter);
		
		for (FitnessLocation loc : fitnessLocations) {
			LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
			if(googleMap != null) {
				googleMap.addMarker(new MarkerOptions()
										.position(pos)
										.title(loc.getName())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
			}
			
			bounds.include(pos);
		}
		
		//googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0));
		if(googleMap != null) {
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().getCenter(), 8));
		}
	}
	
	class MyInfoWindowAdapter implements InfoWindowAdapter {

		private final View myInfoWindowView;
		
		@SuppressLint("InflateParams")
		public MyInfoWindowAdapter() {
			myInfoWindowView = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
		}
		
		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			TextView tvMarkerTitle = (TextView) myInfoWindowView.findViewById(R.id.tvMarkerTitle);
			tvMarkerTitle.setText(marker.getTitle());
			
			return myInfoWindowView;
		}

	}
}
