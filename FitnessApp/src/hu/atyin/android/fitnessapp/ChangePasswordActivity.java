package hu.atyin.android.fitnessapp;

import hu.atyin.android.fitnessapp.session.SessionManager;
import hu.atyin.android.fitnessapp.volley.AppController;
import hu.atyin.android.fitnessapp.volley.CustomJsonRequest;
import hu.atyin.android.fitnessapp.volley.UrlCollection;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class ChangePasswordActivity extends ActionBarActivity {
	
	private ActionBar actionbar;
	
	private ProgressDialog pDialog;
	
	SessionManager session;
	
	private HashMap<String, String> userDetails;
	
	String tag_change_password_json_obj = "change_password_json_obj_req";
	private CustomJsonRequest changePasswordJsonObjReq;
	
	private EditText edOldPassword;
	private EditText edNewPassword;
	private EditText edNewPasswordAgain;
	private Button btnCancel;
	private Button btnSend;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity_layout);
		
		session = new SessionManager(ChangePasswordActivity.this);
		
		userDetails = session.getUserDetails();
		
		// Actionbar referencia megszerzése
		actionbar = getSupportActionBar();
		
		// Actionbar vissza gomb bekapcs
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		// Actionbar cím beállítása
		actionbar.setTitle(R.string.app_change_password_title);
		
		// Actionbar háttér beállítás
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ce3334")));
		
		// Actionbar ikon beállítás
		actionbar.setIcon(R.drawable.gilda_icon_v2);
		
		// View referenciák elkérése
		edOldPassword = (EditText) findViewById(R.id.edOldPassword);
		edNewPassword = (EditText) findViewById(R.id.edNewPassword);
		edNewPasswordAgain = (EditText) findViewById(R.id.edNewPasswordAgain);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnSend = (Button) findViewById(R.id.btnSend);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String oldPass = edOldPassword.getText().toString();
				final String newPass = edNewPassword.getText().toString();
				final String newPassAgain = edNewPasswordAgain.getText().toString();
				
				if(oldPass.trim().length() == 0 || newPass.trim().length() == 0 || newPassAgain.trim().length() == 0) {
					new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(getString(R.string.app_change_password_title)).setMessage(R.string.app_change_password_allFieldRequired).setNeutralButton("OK", null).show();
				}
				else if(!newPass.equals(newPassAgain)) {
					new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(getString(R.string.app_change_password_title)).setMessage(R.string.app_change_password_newPasswordsNotEqual).setNeutralButton("OK", null).show();
				}
				else {
					pDialog = new ProgressDialog(ChangePasswordActivity.this);
					pDialog.setCancelable(false);
					pDialog.setMessage(getString(R.string.app_forgot_password_sendigTitle));
					pDialog.show();
					
					Map<String, String> headers = new HashMap<String, String>();
					headers.put("Authorization", userDetails.get("api_key"));
					
					SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
					String settedValue = prefs.getString("Language", null);
					if(settedValue != null) {
						headers.put("Accept-Language", settedValue);
					}
					
					Map<String, String> params = new HashMap<String, String>();
					params.put("oldPassword", oldPass);
					params.put("newPassword", newPass);
					params.put("confirmNewPassword", newPassAgain);
					
					changePasswordJsonObjReq = new CustomJsonRequest(Method.PUT, UrlCollection.CHANGE_PASSWORD_URL, params, headers, 
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									Log.d("FITNESS", response.toString());
									pDialog.dismiss();
									
									try {
										new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(getString(R.string.app_change_password_title)).setMessage(response.getString("message")).setNeutralButton("OK", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												finish();
											}
										}).show();
									} catch (JSONException e) {
										e.printStackTrace();
										new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(getString(R.string.app_change_password_title)).setMessage(R.string.app_change_password_operationFailed).setNeutralButton("OK", null).show();
									}
								}
							}, 
							new ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									pDialog.dismiss();
									new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(getString(R.string.app_change_password_title)).setMessage(error.getMessage()).setNeutralButton("OK", null).show();
								}
							}
					);
					
					AppController.getInstance().addToRequestQueue(changePasswordJsonObjReq, tag_change_password_json_obj);
				}
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
	
}
