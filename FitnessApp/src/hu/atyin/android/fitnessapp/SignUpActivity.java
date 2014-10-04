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

public class SignUpActivity extends Activity {

	private EditText edFirstName;
	private EditText edLastName;
	private EditText edEmail;
	private EditText edPassword;
	private EditText edPasswordAgain;
	private Button btnCancel;
	private Button btnSignUp;
	
	SessionManager session;
	
	String tag_reg_json_obj = "reg_json_obj_req";
	
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signup_activity_layout);
		
		session = new SessionManager(SignUpActivity.this);
		
		// Referenciák elkérése
		edFirstName = (EditText) findViewById(R.id.edFirstName);
		edLastName = (EditText) findViewById(R.id.edLastName);
		edEmail = (EditText) findViewById(R.id.edEmail);
		edPassword = (EditText) findViewById(R.id.edPassword);
		edPasswordAgain = (EditText) findViewById(R.id.edPasswordAgain);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnSignUp = (Button) findViewById(R.id.btnSignUp);
		
		// Mégse gomb
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// Regisztráció gomb
		btnSignUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final String firstName = edFirstName.getText().toString();
				final String lastName = edLastName.getText().toString();
				final String email = edEmail.getText().toString();
				final String password = edPassword.getText().toString();
				final String passwordAgain = edPasswordAgain.getText().toString();
				
				if(firstName.trim().length() == 0) {
					new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage("Add meg a keresztneved!").setNeutralButton("OK", null).show();
				}
				else if(lastName.trim().length() == 0) {
					new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage("Add meg a vezetékneved!").setNeutralButton("OK", null).show();
				}
				else if(email.trim().length() == 0) {
					new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage("Add meg az e-mail címed!").setNeutralButton("OK", null).show();
				}
				else if(password.trim().length() == 0) {
					new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage("Add meg a jelszót!").setNeutralButton("OK", null).show();
				}
				else if(!password.trim().equals(passwordAgain.trim())) {
					new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage("A megadott jelszavak nem egyeznek!").setNeutralButton("OK", null).show();
				}
				else {
					pDialog = new ProgressDialog(SignUpActivity.this);
					pDialog.setCancelable(false);
					pDialog.setMessage("Regisztráció...");
					pDialog.show();
					
					Map<String, String> headers = new HashMap<String, String>();
					Map<String, String> params = new HashMap<String, String>();
					params.put("first_name", firstName);
					params.put("last_name", lastName);
					params.put("email", email);
					params.put("password", password);
					
					CustomJsonRequest regJsonObjReq = new CustomJsonRequest(Method.POST, UrlCollection.REGISTER_URL, params, headers, 
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									Log.d("FITNESS", response.toString());
									pDialog.dismiss();
									
									try {
										if(response.getBoolean("error")) {
											new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage(response.getString("message")).setNeutralButton("OK", null).show();
										}
										else {
											session.createLoginSession( firstName, 
																		lastName, 
																		email, 
																		response.getString("api_key") );
										}
									} catch (JSONException e) {
										e.printStackTrace();
										new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage("A regisztráció sikertelen! Kérjük próbáld újra!").setNeutralButton("OK", null).show();
									}
								}
							}, 
							new ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Log.d("FITNESS", "Error: " + error.getMessage());
									pDialog.dismiss();
									new AlertDialog.Builder(SignUpActivity.this).setTitle("Regisztráció").setMessage("A regisztráció sikertelen! Kérjük próbáld újra!").setNeutralButton("OK", null).show();
								}
							});
					
					AppController.getInstance().addToRequestQueue(regJsonObjReq, tag_reg_json_obj);
				}
			}
		});
	}
}
