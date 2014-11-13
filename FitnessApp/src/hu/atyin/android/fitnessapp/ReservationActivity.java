package hu.atyin.android.fitnessapp;

import hu.atyin.android.fitnessapp.adapter.TimetableListAdapter;
import hu.atyin.android.fitnessapp.adapter.WeekdaySpinnerAdapter;
import hu.atyin.android.fitnessapp.model.TimetableEvent;
import hu.atyin.android.fitnessapp.model.WeekdaySpinnerItem;
import hu.atyin.android.fitnessapp.session.SessionManager;
import hu.atyin.android.fitnessapp.volley.AlarmNotificationReceiver;
import hu.atyin.android.fitnessapp.volley.AppController;
import hu.atyin.android.fitnessapp.volley.CustomJsonRequest;
import hu.atyin.android.fitnessapp.volley.UrlCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class ReservationActivity extends ActionBarActivity implements OnNavigationListener {
	
	private ListView timetableList;
	private TimetableListAdapter timetableAdapter;
	
	private TextView tvGymRoomTitle;
	
	private HashMap<Integer, ArrayList<TimetableEvent>> timeTable;
	
	private ActionBar actionbar;
	
	private ArrayList<WeekdaySpinnerItem> weekdaySpinnerItems;
	
	private WeekdaySpinnerAdapter weekdaySpinnerAdapter;

	private int selectedWeekday = 0;
	
	private String gymTitle;
	private String roomTitle;
	private int roomId;
	
	private ProgressDialog pDialog;
	
	SessionManager session;
	
	String tag_events_json_obj = "events_json_obj_req";
	String tag_make_reservation_json_obj = "make_reservation_json_obj_req";
	String tag_delete_reservation_json_obj = "delete_reservation_json_obj_req";
	
	private CustomJsonRequest eventsJsonObjReq;
	private CustomJsonRequest makeReservationJsonObjReq;
	private CustomJsonRequest deleteReservationJsonObjReq;
	//private CustomJsonRequest event;
	
	private HashMap<String, String> userDetails;
	
	@SuppressLint("UseSparseArrays") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reservation_activity_layout);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			gymTitle = extras.getString("GYM_TITLE");
			roomTitle = extras.getString("ROOM_TITLE");
			roomId = extras.getInt("ROOM_ID");
		}
		
		session = new SessionManager(ReservationActivity.this);
		
		userDetails = session.getUserDetails();
		
		// referenciák lekérése
		timetableList = (ListView) findViewById(R.id.timetableList);
		tvGymRoomTitle = (TextView) findViewById(R.id.tvGymRoomTitle);
		
		tvGymRoomTitle.setText(gymTitle + " - " + roomTitle);
		
		// Actionbar referencia megszerzése
		actionbar = getSupportActionBar();
		
		// Actionbar háttér beállítás
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ce3334")));
		
		// Actionbar cím beállítása
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setIcon(R.drawable.gilda_icon_v2);
		
		// Actionbar vissza gomb bekapcs
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		// Spinner beállítása
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		// Init
		weekdaySpinnerItems = new ArrayList<WeekdaySpinnerItem>();
		timeTable = new HashMap<Integer, ArrayList<TimetableEvent>>();
		
		// Edzések letöltése
		pDialog = new ProgressDialog(ReservationActivity.this);
		pDialog.setCancelable(false);
		pDialog.setMessage(getString(R.string.app_reservation_loadingTrainingsTitle));
		pDialog.show();
		
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> params = new HashMap<String, String>();
		headers.put("Authorization", userDetails.get("api_key"));
		SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		String settedValue = prefs.getString("Language", null);
		if(settedValue != null) {
			headers.put("Accept-Language", settedValue);
		}
		
		eventsJsonObjReq = new CustomJsonRequest(ReservationActivity.this, Method.GET, UrlCollection.GET_EVENTS_URL + roomId, params, headers, 
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("FITNESS", response.toString());
						pDialog.dismiss();
						
						try {
							JSONArray eventsJsonArray = response.getJSONArray("events");
							
							if(eventsJsonArray.length() == 0) {
								new AlertDialog.Builder(ReservationActivity.this).setTitle(R.string.app_reservation_titleText).setMessage(R.string.app_reservation_noTrainingInThisRoom)
									.setNeutralButton("OK", new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									}).show();
							}
							else {
								String actWdi = "";
								ArrayList<TimetableEvent> tte = null;
								int c = 0;
								
								for (int i = 0; i < eventsJsonArray.length(); i++) {
									JSONObject act = eventsJsonArray.getJSONObject(i);
									
									String actDate = act.getString("date");
									if(!actWdi.equals(actDate)) {
										actWdi = actDate;
										weekdaySpinnerItems.add(new WeekdaySpinnerItem(getWeekdayFromDate(actDate), actDate));
										
										if(i != 0) {
											timeTable.put(c, tte);
											c++;
										}
										
										tte = new ArrayList<TimetableEvent>();
									}
									
									tte.add(new TimetableEvent(act.getInt("id"), 
															   act.getString("start_time"), 
															   act.getString("end_time"), 
															   act.getString("trainer"), 
															   act.getString("training"), 
															   act.getInt("free_spots"), 
															   act.getInt("is_reserved") == 0 ? false : true));
									
									if(i == eventsJsonArray.length() - 1) {
										timeTable.put(c, tte);
									}
								}
								
								weekdaySpinnerAdapter = new WeekdaySpinnerAdapter(getApplicationContext(), weekdaySpinnerItems);
								actionbar.setListNavigationCallbacks(weekdaySpinnerAdapter, ReservationActivity.this);
								
								timetableAdapter = new TimetableListAdapter(ReservationActivity.this, R.layout.timetable_row_layout, timeTable.get(0));
								timetableList.setAdapter(timetableAdapter);
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
						showErrorAlert(error.getMessage());
					}
				});
		
		AppController.getInstance().addToRequestQueue(eventsJsonObjReq, tag_events_json_obj);
		
		
		timetableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				
				final int event_id = timeTable.get(selectedWeekday).get(position).getId();
				final String selected_start_time = timeTable.get(selectedWeekday).get(position).getStartTime();
				final String selected_training_name = timeTable.get(selectedWeekday).get(position).getTrainingName();
				Log.d("FITNESS", "Event ID: " + String.valueOf(event_id));
				
				if(timeTable.get(selectedWeekday).get(position).isReserved()) {
					new AlertDialog.Builder(ReservationActivity.this).setTitle(R.string.app_reservation_titleText).setMessage(R.string.app_my_reservations_deleteQuestion)
						.setNegativeButton(R.string.app_btnNo_Title, null)
						.setPositiveButton(R.string.app_btnYesTitle, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								pDialog.setMessage(getString(R.string.app_my_reservations_deletingReservation));
								pDialog.show();
								
								Map<String, String> headers = new HashMap<String, String>();
								Map<String, String> params = new HashMap<String, String>();
								headers.put("Authorization", userDetails.get("api_key"));
								SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
								String settedValue = prefs.getString("Language", null);
								if(settedValue != null) {
									headers.put("Accept-Language", settedValue);
								}
								
								deleteReservationJsonObjReq = new CustomJsonRequest(ReservationActivity.this, Method.DELETE, UrlCollection.DELETE_RESERVATION_URL + event_id, params, headers, 
										new Listener<JSONObject>() {
											@Override
											public void onResponse(JSONObject response) {
												Log.d("FITNESS", response.toString());
												pDialog.dismiss();
												
												try {
													new AlertDialog.Builder(ReservationActivity.this).setTitle(R.string.app_reservation_titleText).setMessage(response.getString("message")).setNeutralButton("OK", null).show();
													timeTable.get(selectedWeekday).get(position).setFreeSpots(response.getInt("free_spots"));
													timeTable.get(selectedWeekday).get(position).setReserved(false);
													
													timetableAdapter.notifyDataSetChanged();
													
													DeleteNotification(position);
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										}, 
										new ErrorListener() {
											@Override
											public void onErrorResponse(VolleyError error) {
												pDialog.dismiss();
												showReservationErrorAlert(false, error.getMessage());
											}
										});
								
								AppController.getInstance().addToRequestQueue(deleteReservationJsonObjReq, tag_delete_reservation_json_obj);
							}
						})
						.show();
				}
				else {
					new AlertDialog.Builder(ReservationActivity.this).setTitle(R.string.app_reservation_titleText).setMessage(R.string.app_reservation_sureToReserveQuestion)
						.setNegativeButton(R.string.app_btnNo_Title, null)
						.setPositiveButton(R.string.app_btnYesTitle, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								pDialog.setMessage(getString(R.string.app_reservation_reservationInProgressTitle));
								pDialog.show();
								
								Map<String, String> headers = new HashMap<String, String>();
								SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
								String settedValue = prefs.getString("Language", null);
								if(settedValue != null) {
									headers.put("Accept-Language", settedValue);
								}
								headers.put("Authorization", userDetails.get("api_key"));
								
								Map<String, String> params = new HashMap<String, String>();
								params.put("event_id", String.valueOf(event_id));
								
								makeReservationJsonObjReq = new CustomJsonRequest(ReservationActivity.this, Method.POST, UrlCollection.MAKE_RESERVATION_URL, params, headers, 
										new Listener<JSONObject>() {
											@Override
											public void onResponse(JSONObject response) {
												pDialog.dismiss();
												
												try {
													new AlertDialog.Builder(ReservationActivity.this).setTitle(R.string.app_reservation_titleText).setMessage(response.getString("message")).setNeutralButton("OK", null).show();
													timeTable.get(selectedWeekday).get(position).setFreeSpots(response.getInt("free_spots"));
													timeTable.get(selectedWeekday).get(position).setReserved(true);
													
													timetableAdapter.notifyDataSetChanged();
													
													SetNotification(weekdaySpinnerItems.get(selectedWeekday).getDate(), 
																	selected_start_time, 
																	selected_training_name, position);
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										}, 
										new ErrorListener() {
											@Override
											public void onErrorResponse(VolleyError error) {
												pDialog.dismiss();
												showReservationErrorAlert(true, error.getMessage());
											}
										});
								
								AppController.getInstance().addToRequestQueue(makeReservationJsonObjReq, tag_make_reservation_json_obj);
							}
						})
						.show();
				}
			}
		});
	}
	
	@SuppressLint("SimpleDateFormat") 
	private void SetNotification(String date, String startTime, String trainingName, int position) {
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
		final int event_id = timeTable.get(selectedWeekday).get(position).getId();
		String timerValue = sharedPrefs.getString("pref_key", "NULL");
		timerValue = timerValue.equals("NULL") ? "2" : timerValue;
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		Date reservationDate = null;
		try {
			reservationDate = formatter.parse(date + " " + startTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		reservationDate.setTime(reservationDate.getTime() - Integer.parseInt(timerValue)*3600000);
		
		Log.d("FITNESS", String.valueOf(reservationDate.getTime()));
        
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmNotificationReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("NOTI_MESSAGE", getString(R.string.app_notification_training) + trainingName + ", " + getString(R.string.app_notification_startText) + startTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, event_id, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reservationDate.getTime(), pendingIntent);
		
	}
	
	private void DeleteNotification(int position) {
		final int event_id = timeTable.get(selectedWeekday).get(position).getId();
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmNotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, event_id, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
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
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Log.d("FITNESS ItemPosition", String.valueOf(itemPosition));
		Log.d("FITNESS ItemId", String.valueOf(itemId));
		
		timetableAdapter = new TimetableListAdapter(ReservationActivity.this, R.layout.timetable_row_layout, timeTable.get(itemPosition));
		timetableList.setAdapter(timetableAdapter);
		
		selectedWeekday = itemPosition;
		
		return false;
	}
	
	public void showErrorAlert(String messageText) {
		new AlertDialog.Builder(ReservationActivity.this).setTitle(R.string.app_error_title).setMessage(messageText)
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
					pDialog.setMessage(getString(R.string.app_reservation_loadingTrainingsTitle));
					pDialog.show();
					
					AppController.getInstance().addToRequestQueue(eventsJsonObjReq, tag_events_json_obj);
				}
			}).show();
	}
	
	public void showReservationErrorAlert(final boolean isMakeReservation, String messageText) {
		new AlertDialog.Builder(ReservationActivity.this).setTitle(R.string.app_error_title).setMessage(messageText)
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
					pDialog.setMessage(isMakeReservation ? getString(R.string.app_reservation_reservationInProgressTitle): getString(R.string.app_my_reservations_deletingReservation));
					pDialog.show();
					
					AppController.getInstance().addToRequestQueue(isMakeReservation ? makeReservationJsonObjReq : deleteReservationJsonObjReq, isMakeReservation ? tag_make_reservation_json_obj : tag_delete_reservation_json_obj);
				}
			}).show();
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getWeekdayFromDate(String actDate) {
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		
		Date date = null;
		try {
			date = dFormat.parse(actDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		c.setTime(date);
		
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		
		switch (dayOfWeek) {
		
			case Calendar.MONDAY:
				return getString(R.string.app_reservation_spinner_monday);
			case Calendar.TUESDAY:
				return getString(R.string.app_reservation_spinner_tuesday);
			case Calendar.WEDNESDAY:
				return getString(R.string.app_reservation_spinner_wednesday);
			case Calendar.THURSDAY:
				return getString(R.string.app_reservation_spinner_thursday);
			case Calendar.FRIDAY:
				return getString(R.string.app_reservation_spinner_friday);
			case Calendar.SATURDAY:
				return getString(R.string.app_reservation_spinner_saturday);
			case Calendar.SUNDAY:
				return getString(R.string.app_reservation_spinner_sunday);

			default:
				return getString(R.string.app_reservation_spinner_monday);
		}
	}
}
