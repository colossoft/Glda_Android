package hu.atyin.android.fitnessapp.volley;

import hu.atyin.android.fitnessapp.LocationsActivity;
import hu.atyin.android.fitnessapp.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class AlarmNotificationReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		NotificationManager notificationManager = 
				(NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);

		CharSequence text = "�nnek edz�se leszt!";
		long when = System.currentTimeMillis();
		
	    PendingIntent pendingIntent =PendingIntent.getActivity(arg0, 0, new Intent(arg0, LocationsActivity.class), 0);

		
		Notification notification = new Notification(R.drawable.ic_launcher,text, when);
		notification.setLatestEventInfo(arg0, "GildaMax", "Gy�runk vazzeg?", pendingIntent);

	    long[] vibrate = {0,2000};
	    notification.vibrate = vibrate;

	    notification.ledARGB = Color.RED;
	    notification.ledOffMS = 300;
	    notification.ledOnMS = 300;

	    notification.defaults |= Notification.DEFAULT_LIGHTS;
	    
	    notificationManager.notify(0, notification);		
	}

}
