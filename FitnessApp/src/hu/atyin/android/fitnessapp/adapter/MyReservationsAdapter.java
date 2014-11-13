package hu.atyin.android.fitnessapp.adapter;

import hu.atyin.android.fitnessapp.MyReservationsActivity;
import hu.atyin.android.fitnessapp.R;
import hu.atyin.android.fitnessapp.model.Reservation;
import hu.atyin.android.fitnessapp.session.SessionManager;
import hu.atyin.android.fitnessapp.volley.AlarmNotificationReceiver;
import hu.atyin.android.fitnessapp.volley.AppController;
import hu.atyin.android.fitnessapp.volley.CustomJsonRequest;
import hu.atyin.android.fitnessapp.volley.UrlCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class MyReservationsAdapter extends ArrayAdapter<Reservation> {
	
	private Context context;
	private ArrayList<Reservation> myReservations;
	
	SessionManager session;
	
	private HashMap<String, String> userDetails;
	
	private ProgressDialog pDialog;
	
	private CustomJsonRequest deleteReservationJsonObjReq;
	String tag_delete_reservation_json_obj = "delete_reservation_json_obj_req";
	
	IRefreshMyReservations rmsListener;

	public MyReservationsAdapter(Context context, int resource, ArrayList<Reservation> myReservations) {
		super(context, resource, myReservations);
		
		this.context = context;
		this.myReservations = myReservations;
		
		session = new SessionManager(context);
		
		userDetails = session.getUserDetails();
		
		rmsListener = (MyReservationsActivity) context;
	}

	static class ViewHolder {
		public TextView tvEventDate;
		public TextView tvEventTime;
		public TextView tvReservationTrainerName;
		public TextView tvReservationTrainingName;
		public TextView tvReservationTime;
		public Button btnDeleteReservation;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder = null;
		
		if(rowView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.my_reservations_row_layout, parent, false);
			
			holder = new ViewHolder();
			
			holder.tvEventDate = (TextView) rowView.findViewById(R.id.tvEventDate);
			holder.tvEventTime = (TextView) rowView.findViewById(R.id.tvEventTime);
			holder.tvReservationTrainerName = (TextView) rowView.findViewById(R.id.tvReservationTrainerName);
			holder.tvReservationTrainingName = (TextView) rowView.findViewById(R.id.tvReservationTrainingName);
			holder.tvReservationTime = (TextView) rowView.findViewById(R.id.tvReservationTime);
			holder.btnDeleteReservation = (Button) rowView.findViewById(R.id.btnDeleteReservation);
			
			rowView.setTag(holder);
		}
		else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		final Reservation reservation = myReservations.get(position);
		
		holder.tvEventDate.setText(reservation.getDate());
		holder.tvEventTime.setText(String.format("%s - %s", reservation.getStartTime(), reservation.getEndTime()));
		holder.tvReservationTrainerName.setText(reservation.getTrainerName());
		holder.tvReservationTrainingName.setText(reservation.getTrainingName());
		holder.tvReservationTime.setText(reservation.getReservationTime());
		
		holder.btnDeleteReservation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context).setTitle(R.string.app_my_reservations_title).setMessage(R.string.app_my_reservations_deleteQuestion)
					.setNegativeButton(R.string.app_btnNo_Title, null)
					.setPositiveButton(R.string.app_btnYesTitle, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Foglalás törlése
							pDialog = new ProgressDialog(context);
							pDialog.setCancelable(false);
							pDialog.setMessage(context.getString(R.string.app_my_reservations_deletingReservation));
							pDialog.show();
							
							Map<String, String> headers = new HashMap<String, String>();
							headers.put("Authorization", userDetails.get("api_key"));
							SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
							String settedValue = prefs.getString("Language", null);
							if(settedValue != null) {
								headers.put("Accept-Language", settedValue);
							}
							
							Map<String, String> params = new HashMap<String, String>();
							
							deleteReservationJsonObjReq = new CustomJsonRequest(context, Method.DELETE, UrlCollection.DELETE_RESERVATION_URL + reservation.getId(), params, headers, 
									new Listener<JSONObject>() {
										@Override
										public void onResponse(JSONObject response) {
											pDialog.dismiss();
											
											try {
												new AlertDialog.Builder(context).setTitle(R.string.app_my_reservations_title).setMessage(response.getString("message"))
												.setNeutralButton("OK", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														dialog.dismiss();
														DeleteNotification(context, reservation.getId());
														rmsListener.refreshMyReservations();
													}
												}).show();
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}, 
									new ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
											pDialog.dismiss();
											
											new AlertDialog.Builder(context).setTitle(R.string.app_error_title).setMessage(error.getMessage())
											.setNegativeButton(R.string.app_btnBack_Title, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											})
											.setPositiveButton(R.string.app_btnAgain_Title, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													pDialog.setMessage(context.getString(R.string.app_my_reservations_deletingReservation));
													pDialog.show();
													
													AppController.getInstance().addToRequestQueue(deleteReservationJsonObjReq, tag_delete_reservation_json_obj);
												}
											}).show();
										}
									});
							
							AppController.getInstance().addToRequestQueue(deleteReservationJsonObjReq, tag_delete_reservation_json_obj);
						}
					}).show();
			}
		});
		
		return rowView;
	}
	
	private void DeleteNotification(Context ctx, int event_id) {
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, AlarmNotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, event_id, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
	}
	
	public interface IRefreshMyReservations {
		public void refreshMyReservations();
	}
}
