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

public class LoginActivity extends Activity {

	private EditText edEmail;
	private EditText edPassword;
	private Button btnCancel;
	private Button btnLogin;
	
	SessionManager session;
	
	String tag_login_json_obj = "login_json_obj_req";
	
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity_layout);
		
		session = new SessionManager(LoginActivity.this);
		
		// Referenciák elkérése
		edEmail = (EditText) findViewById(R.id.edEmail);
		edPassword = (EditText) findViewById(R.id.edPassword);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final String email = edEmail.getText().toString();
				final String password = edPassword.getText().toString();
				
				Log.d("FITNESS", email);
				Log.d("FITNESS", password);
				
				if(email.trim().length() == 0) {
					new AlertDialog.Builder(LoginActivity.this).setTitle("Bejelentkezés").setMessage("Add meg az e-mail címet!").setNeutralButton("OK", null).show();
				}
				else if(password.trim().length() == 0) {
					new AlertDialog.Builder(LoginActivity.this).setTitle("Bejelentkezés").setMessage("Add meg a jelszót!").setNeutralButton("OK", null).show();
				}
				else {
					pDialog = new ProgressDialog(LoginActivity.this);
					pDialog.setCancelable(false);
					pDialog.setMessage("Bejelentkezés...");
					pDialog.show();
					
					Map<String, String> headers = new HashMap<String, String>();
					Map<String, String> params = new HashMap<String, String>();
					params.put("email", email);
					params.put("password", password);
					
					CustomJsonRequest loginJsonObjReq = new CustomJsonRequest(Method.POST, UrlCollection.LOGIN_URL, params, headers, 
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									Log.d("FITNESS", response.toString());
									pDialog.dismiss();
									
									try {
										if(response.getBoolean("error")) {
											new AlertDialog.Builder(LoginActivity.this).setTitle("Bejelentkezés").setMessage("Hibás email vagy jelszó!").setNeutralButton("OK", null).show();
										}
										else {
											session.createLoginSession( response.getString("first_name"), 
																		response.getString("last_name"), 
																		response.getString("email"), 
																		response.getString("api_key") );
										}
									} catch (JSONException e) {
										e.printStackTrace();
										new AlertDialog.Builder(LoginActivity.this).setTitle("Bejelentkezés").setMessage("A bejelentkezés sikertelen! Kérjük próbáld újra!").setNeutralButton("OK", null).show();
									}
								}
							}, 
							new ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Log.d("FITNESS", "Error: " + error.getMessage());
									pDialog.dismiss();
									new AlertDialog.Builder(LoginActivity.this).setTitle("Bejelentkezés").setMessage("A bejelentkezés sikertelen! Kérjük próbáld újra!").setNeutralButton("OK", null).show();
								}
							}
					);
					
					AppController.getInstance().addToRequestQueue(loginJsonObjReq, tag_login_json_obj);					
				}
			}
		});
		
	}
}
