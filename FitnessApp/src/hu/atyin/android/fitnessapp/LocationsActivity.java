package hu.atyin.android.fitnessapp;

import hu.atyin.android.fitnessapp.adapter.LocationsListAdapter;
import hu.atyin.android.fitnessapp.model.FitnessLocation;
import hu.atyin.android.fitnessapp.model.MyLocation;
import hu.atyin.android.fitnessapp.model.MyLocation.LocationResult;
import hu.atyin.android.fitnessapp.session.SessionManager;
import hu.atyin.android.fitnessapp.volley.AlarmNotificationReceiver;
import hu.atyin.android.fitnessapp.volley.AppController;
import hu.atyin.android.fitnessapp.volley.CustomJsonRequest;
import hu.atyin.android.fitnessapp.volley.UrlCollection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.preference.PreferenceManager;
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
	
    private static final int RESULT_SETTINGS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locations_activity_layout);
		
		pDialog = new ProgressDialog(LocationsActivity.this);
		pDialog.setCancelable(false);
		
		session = new SessionManager(LocationsActivity.this);
		
		userDetails = session.getUserDetails();
		
		Log.d("FITNESS", userDetails.toString());
		
		// Referenciák lekérése
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
							Toast.makeText(LocationsActivity.this, "Pozíció lekérése sikertelen!", Toast.LENGTH_LONG).show();
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
		
		// Teszt helyszínek
		fitnessLocations = new ArrayList<FitnessLocation>();
		/*fitnessLocations.add(new FitnessLocation("Allee", "Budapest Práter u. 61", 47.486775, 19.081819, 0.0));
		fitnessLocations.add(new FitnessLocation("Óbuda Gate", "Budapest Sass u. 8", 47.500460, 19.052079, 0.0));
		fitnessLocations.add(new FitnessLocation("River Estates", "Budapest Gábor Áron u. 54", 47.520781, 19.011556, 0.0));
		fitnessLocations.add(new FitnessLocation("Hermina Towers", "Budapest Öntöde u. 7", 47.428056, 19.061826, 0.0));
		fitnessLocations.add(new FitnessLocation("Flórián", "Budapest Bezerédi Pál u. 69", 47.553833, 19.121764, 0.0));
		fitnessLocations.add(new FitnessLocation("Savoya Park", "Budapest Toborzó u. 20", 47.557072, 19.080241, 0.0));
		*/
		
		// Actionbar referencia megszerzése
		actionbar = getSupportActionBar();
		
		// Actionbar cím beállítása
		actionbar.setTitle("Gilda Max Fitness");
		
		// Actionbar háttér beállítás
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ce3334")));
		
		// Actionbar ikon beállítás
		actionbar.setIcon(R.drawable.gilda_icon_v2);
		
		
		try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		if(googleMap != null) {
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
		}
		
		// Helyszínek letöltése
		pDialog.setMessage("Helyszínek letöltése...");
		pDialog.show();
		
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> params = new HashMap<String, String>();
		headers.put("Authorization", userDetails.get("api_key"));
		
		locationsJsonObjReq = new CustomJsonRequest(Method.GET, UrlCollection.GET_ALL_LOCATION_URL, params, headers, 
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
								
								if(!getLocationDatas()) {
									pDialog.dismiss();
									Toast.makeText(LocationsActivity.this, "Pozíció lekérése sikertelen!", Toast.LENGTH_LONG).show();
									setListElements();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showErrorAlert();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("FITNESS", "Error: " + error.getMessage());
						pDialog.dismiss();
						showErrorAlert();
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
	
	public void showErrorAlert() {
		new AlertDialog.Builder(LocationsActivity.this).setTitle("Hiba").setMessage("Sajnos nem sikerült letölteni a helyszíneket!")
			.setNegativeButton("Kilépés", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					session.logoutUser();
				}
			})
			.setPositiveButton("Újra", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					pDialog.setMessage("Helyszínek letöltése...");
					pDialog.show();
					
					AppController.getInstance().addToRequestQueue(locationsJsonObjReq, tag_locations_json_obj);
				}
			}).show();
	}
	
	private void changePassword() {
		Intent cpIntent = new Intent(LocationsActivity.this, ChangePasswordActivity.class);
		startActivity(cpIntent);
	}

	
	private void logoutUser() {
		new AlertDialog.Builder(LocationsActivity.this).setTitle("Kijelentkezés").setMessage("Biztos ki akarsz jelentkezni?")
		.setNegativeButton("Nem", null)
		.setPositiveButton("Igen", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				session.logoutUser();
			}
		}).show();
	}
	
	private void setting(){
		Intent settingIntetn = new Intent(getApplicationContext(), SettingActivity.class);		
		//startActivity(settingIntetn);
		startActivityForResult(settingIntetn, RESULT_SETTINGS);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SETTINGS:
            showSettingResult();
            break;
 
        }
 
    }
	
	private void showSettingResult() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
 
        StringBuilder builder = new StringBuilder();
 
        builder.append("\n Timer: "
                + sharedPrefs.getString("pref_key", "NULL"));
        
        //Toast.makeText(LocationsActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
        Log.d("FITNESS", builder.toString());
        
        Calendar calendar = Calendar.getInstance();
        
        //calendar.set(Calendar.MONTH, 10);
        //calendar.set(Calendar.YEAR, 2014);
        //calendar.set(Calendar.DAY_OF_MONTH, 25);
   
        //calendar.set(Calendar.HOUR_OF_DAY, 13);
        //calendar.set(Calendar.MINUTE, 29);
        
        calendar.add(Calendar.MINUTE, 1);
        
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmNotificationReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000), pendingIntent);
	}
	
	private void initializeMap() {
		if(googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
			
			if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sajnos nem sikerült betölteni a térképet!", Toast.LENGTH_LONG)
                        .show();
            }
		}
	}
	
	public boolean getLocationDatas() {
		pDialog.setMessage("Saját pozíció meghatározása...");
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
		
		@SuppressLint("InflateParams") public MyInfoWindowAdapter() {
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
