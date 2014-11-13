package hu.atyin.android.fitnessapp;

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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class ForgotPasswordActivity extends Activity {

	private EditText edEmail;
	private Button btnCancel;
	private Button btnSend;
	
	String tag_forgot_password_json_obj = "forgot_password_json_obj_req";
	
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forgot_password_activity_layout);
		
		// View referenciák elkérése
		edEmail = (EditText) findViewById(R.id.edEmail);
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
				final String email = edEmail.getText().toString();
				
				if(email.trim().length() == 0) {
					new AlertDialog.Builder(ForgotPasswordActivity.this).setTitle(getString(R.string.app_forgot_password_title)).setMessage(R.string.app_registration_emailInvalid).setNeutralButton("OK", null).show();
				}
				else {
					pDialog = new ProgressDialog(ForgotPasswordActivity.this);
					pDialog.setCancelable(false);
					pDialog.setMessage(getString(R.string.app_forgot_password_sendigTitle));
					pDialog.show();
					
					Map<String, String> headers = new HashMap<String, String>();
					SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
					String settedValue = prefs.getString("Language", null);
					if(settedValue != null) {
						headers.put("Accept-Language", settedValue);
					}
					
					Map<String, String> params = new HashMap<String, String>();
					params.put("email", email);
					
					CustomJsonRequest forgotPasswordJsonObjReq = new CustomJsonRequest(ForgotPasswordActivity.this, Method.POST, UrlCollection.FORGOT_PASSWORD_URL, params, headers, 
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									Log.d("FITNESS", response.toString());
									pDialog.dismiss();
									
									try {
										new AlertDialog.Builder(ForgotPasswordActivity.this).setTitle(getString(R.string.app_forgot_password_title)).setMessage(response.getString("message")).setNeutralButton("OK", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												finish();
											}
										}).show();
									} catch (JSONException e) {
										e.printStackTrace();
										new AlertDialog.Builder(ForgotPasswordActivity.this).setTitle(getString(R.string.app_forgot_password_title)).setMessage(R.string.app_forgot_password_oparationFailed).setNeutralButton("OK", null).show();
									}
								}
							}, 
							new ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									pDialog.dismiss();
									new AlertDialog.Builder(ForgotPasswordActivity.this).setTitle(getString(R.string.app_forgot_password_title)).setMessage(error.getMessage()).setNeutralButton("OK", null).show();
								}
							}
					);
					
					AppController.getInstance().addToRequestQueue(forgotPasswordJsonObjReq, tag_forgot_password_json_obj);
				}
			}
		});
	}
	
}
