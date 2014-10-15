package hu.atyin.android.fitnessapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingActivity extends PreferenceActivity {
	
	OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			//if(key.equals("pref_timed")) {
				Preference timerPreference = findPreference(key);
				Log.d("FITNESS", "OK!!!!!!!!!");
				String settedValue = sharedPreferences.getString(key, "");
				Log.d("FITNESS", settedValue);
				timerPreference.setSummary(getString(R.string.pref_summary) + " (" + settedValue + " óra)");
			//}
		}
	};
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference); 
	}	
	
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(listener);
	}
}
