package hu.atyin.android.fitnessapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingActivity extends PreferenceActivity {
	
	OnSharedPreferenceChangeListener listener;
	
	private ActionBar actionbar;
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference); 
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// Actionbar referencia megszerzése
			actionbar = getActionBar();
			
			// Actionbar vissza gomb bekapcs
			actionbar.setDisplayHomeAsUpEnabled(true);
			
			// Actionbar cím beállítása
			actionbar.setTitle(R.string.app_menu_settings);
			
			// Actionbar háttér beállítás
			actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ce3334")));
			
			// Actionbar ikon beállítás
			actionbar.setIcon(R.drawable.gilda_icon_v2);
		}
		
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
		Preference timerPreference = findPreference("pref_key");
		String settedValue = sharedPrefs.getString("pref_key", "");
		if(settedValue != null) {
			timerPreference.setSummary(getString(R.string.pref_summary) + " (" + settedValue + " " + getString(R.string.app_settings_hourText) + ")");
		} else {
			timerPreference.setSummary(getString(R.string.pref_summary) + " ( 2 " + getString(R.string.app_settings_hourText) + ")");
		}
        

		
		listener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if(key.equals("pref_key")) {
					Preference timerPreference = findPreference(key);
					String settedValue = sharedPreferences.getString(key, "");
					timerPreference.setSummary(getString(R.string.pref_summary) + " (" + settedValue + " " + getString(R.string.app_settings_hourText) + ")");
				}
			}
		};
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(listener);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(listener);
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
