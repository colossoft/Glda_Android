package hu.atyin.android.fitnessapp;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

public class StartActivity extends Activity {

	private Button btnLanguagePicker;
	private Button btnSignUp;
	private Button btnLogin;
	
	//private static String [] languages = {"Magyar", "English", "Deutsch"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        
		//Lek�rj�k a nyelvet �s ha m�r be van �ll�tva, akkor azt l�kj�k vissza, ha nincs, akkor a magyart
		SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		String settedValue = prefs.getString("Language", "NULL");
	    
		if(prefs == null || settedValue == "hu")
	    	ChangeLang("hu");
	    else
	    	ChangeLang(settedValue);
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_activity_layout);
		
		// Referenci�k lek�r�se
		btnLanguagePicker = (Button) findViewById(R.id.btnLanguagePicker);
		btnSignUp = (Button) findViewById(R.id.btnSignUp);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		
		// Nyelvv�laszt� dial�gus feldob�sa
		btnLanguagePicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog langDialog = new Dialog(StartActivity.this);
				langDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				langDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				langDialog.setContentView(R.layout.lang_chooser_layout);
				
				RelativeLayout btnLangChooseHun = (RelativeLayout) langDialog.findViewById(R.id.btnLangChooseHun);
				RelativeLayout btnLangChooseEng = (RelativeLayout) langDialog.findViewById(R.id.btnLangChooseEng);
				RelativeLayout btnLangChooseDe = (RelativeLayout) langDialog.findViewById(R.id.btnLangChooseDe);
				
				btnLangChooseHun.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ChangeLang("hu");						
		                langDialog.dismiss();
		        	    RefreshStartActivity();
					}
				});
				
				btnLangChooseEng.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ChangeLang("en");						
						langDialog.dismiss();
					    RefreshStartActivity();
					}
				});
				
				btnLangChooseDe.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ChangeLang("de");						
						langDialog.dismiss();
					    RefreshStartActivity();
					}
				});
				
				langDialog.show();
			}
		});
		
		// Regisztr�ci� gomb
		btnSignUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Regisztr�ci� ablak megnyit�sa
				Intent signUpIntent = new Intent(StartActivity.this, SignUpActivity.class);
				startActivity(signUpIntent);
			}
		});
		
		// Login gomb
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Bejelentkez�s ablak megnyit�sa
				Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
				startActivity(loginIntent);
			}
		});
	}
	
	//Nyelvv�laszt�s
	private void ChangeLang(String lang)
	{
	    if (lang.equalsIgnoreCase(""))
	     return;
	    Locale myLocale = new Locale(lang);
	    SaveLocale(lang);
	    Locale.setDefault(myLocale);
	    android.content.res.Configuration config = new android.content.res.Configuration();
	    config.locale = myLocale;
	    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
	}
	
	//Kiv�lasztott nyelv t�rol�sa
	private void SaveLocale(String lang)
	{
	    String langPref = "Language";
	    SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(langPref, lang);
	    editor.commit();
	}
	
	//�jraind�tjuk a Start Activity-t, hogy annak a nyelvezete is befriss�lj�n
	public void RefreshStartActivity() {
		this.finish();
		this.startActivity(getIntent());
	}
}
