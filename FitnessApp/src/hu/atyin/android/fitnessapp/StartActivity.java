package hu.atyin.android.fitnessapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start_activity_layout);
		
		// Referenciák lekérése
		btnLanguagePicker = (Button) findViewById(R.id.btnLanguagePicker);
		btnSignUp = (Button) findViewById(R.id.btnSignUp);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		
		// Nyelvválasztó dialógus feldobása
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
						langDialog.dismiss();
					}
				});
				
				btnLangChooseEng.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						langDialog.dismiss();
					}
				});
				
				btnLangChooseDe.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						langDialog.dismiss();
					}
				});
				
				langDialog.show();
			}
		});
		
		// Regisztráció gomb
		btnSignUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Regisztráció ablak megnyitása
				Intent signUpIntent = new Intent(StartActivity.this, SignUpActivity.class);
				startActivity(signUpIntent);
			}
		});
		
		// Login gomb
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Bejelentkezés ablak megnyitása
				Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
				startActivity(loginIntent);
			}
		});
	}
	
}
