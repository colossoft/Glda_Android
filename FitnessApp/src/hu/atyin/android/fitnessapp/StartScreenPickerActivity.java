package hu.atyin.android.fitnessapp;

import hu.atyin.android.fitnessapp.session.SessionManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartScreenPickerActivity extends Activity {

	SessionManager session;
	
	Class<? extends Activity> activityClass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		session = new SessionManager(StartScreenPickerActivity.this);
		
		if(session.checkLogin()) {
			activityClass = LocationsActivity.class;
		}
		else {
			activityClass = StartActivity.class;
		}
		
		Intent startScreenIntent = new Intent(StartScreenPickerActivity.this, activityClass);
		startActivity(startScreenIntent);
		finish();
	}
	
}
