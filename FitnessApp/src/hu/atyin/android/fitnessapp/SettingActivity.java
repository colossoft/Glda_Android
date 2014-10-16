package hu.atyin.android.fitnessapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingActivity extends PreferenceActivity {
	
	OnSharedPreferenceChangeListener listener;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference); 
		
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
		Preference timerPreference = findPreference("pref_key");
		String settedValue = sharedPrefs.getString("pref_key", "");
		if(settedValue != null) {
			timerPreference.setSummary(getString(R.string.pref_summary) + " (" + settedValue + " óra)");
		} else {
			timerPreference.setSummary(getString(R.string.pref_summary) + " ( 2 óra)");
		}
        

		
		listener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if(key.equals("pref_key")) {
					Preference timerPreference = findPreference(key);
					String settedValue = sharedPreferences.getString(key, "");
					timerPreference.setSummary(getString(R.string.pref_summary) + " (" + settedValue + " óra)");
				}
			}
		};
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
