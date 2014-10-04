package hu.atyin.android.fitnessapp.session;

import hu.atyin.android.fitnessapp.LocationsActivity;
import hu.atyin.android.fitnessapp.StartActivity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

	SharedPreferences pref;
	
	Editor editor;
	
	Context _context;
	
	int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "GILDA_USER_SESSION";
	private static final String IS_LOGIN = "IsLoggedIn";
	
	public static final String KEY_FIRST_NAME = "first_name";
	public static final String KEY_LAST_NAME = "last_name";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_API_KEY = "api_key";
	
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/*
	 * Create login session
	 */
	public void createLoginSession(String firstName, String lastName, String email, String apiKey) {
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_FIRST_NAME, firstName);
		editor.putString(KEY_LAST_NAME, lastName);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_API_KEY, apiKey);
		
		editor.commit();
		
		Intent i = new Intent(_context, LocationsActivity.class);
		
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		_context.startActivity(i);
		((Activity) _context).finish();
	}
	
	/*
	 * Check login status
	 */
	public boolean checkLogin() {
		if(!this.isLoggedIn()) {
			/*Intent i = new Intent(_context, StartActivity.class);
			
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			_context.startActivity(i);
			((LocationsActivity) _context).finish();*/
			
			return false;
		}
		
		return true;
	}
	
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}
	
	/*
	 * Get stored session datas
	 */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		
		user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));
		user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, null));
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		user.put(KEY_API_KEY, pref.getString(KEY_API_KEY, null));
		
		return user;
	}
	
	/*
	 * Clear session details
	 */
	public void logoutUser() {
		editor.clear();
		editor.commit();
		
		Intent i = new Intent(_context, StartActivity.class);
		
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		_context.startActivity(i);
		((LocationsActivity) _context).finish();
	}
}
