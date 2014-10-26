package hu.atyin.android.fitnessapp.volley;

import hu.atyin.android.fitnessapp.LocationsActivity;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

public class AlarmNotificationReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		NotificationManager notificationManager = 
				(NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//int icon = R.drawable.alert_light_frame;
		//CharSequence text = "Ez a text vazzeg";
		//long when = System.currentTimeMillis();
		
		//Intent in=new Intent(arg0, LocationsActivity.class);
	    //in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent pendingIntent=PendingIntent.getActivity(arg0, 0, new Intent(), 0);
		
		//@SuppressWarnings("deprecation")
		//Notification notification = new Notification(icon,text, when);

	    //long[] vibrate = {0,100,200,300};
	    //notification.vibrate = vibrate;

	    //notification.ledARGB = Color.RED;
	    //notification.ledOffMS = 300;
	    //notification.ledOnMS = 300;

	    //notification.defaults |= Notification.DEFAULT_LIGHTS;
	    
	    //Notification mBuilder=new NotificationCompat.Builder(arg0)
       // .setContentTitle("Your Title")
       // .setContentText("Your Text")
       // .setContentIntent(pendingIntent)
       // .build();
	    
	    //mBuilder.setLatestEventInfo(arg0, "Alárm", "Ez a szöveg", pendingIntent);
	    
	    Notification notif = new Notification(0,
	    	    "Crazy About Android...", System.currentTimeMillis());
	    	  notif.setLatestEventInfo(arg0, "GildaMax", "Mûködj már baszdmeg", pendingIntent);
	    
	    notificationManager.notify(1, notif);		
	}

}
