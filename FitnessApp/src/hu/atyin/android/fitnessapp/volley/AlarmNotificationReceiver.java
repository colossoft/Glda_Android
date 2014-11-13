package hu.atyin.android.fitnessapp.volley;

import hu.atyin.android.fitnessapp.R;
import hu.atyin.android.fitnessapp.StartScreenPickerActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

public class AlarmNotificationReceiver extends BroadcastReceiver {
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context ctx, Intent intent) {
		String message = intent.getExtras().getString("NOTI_MESSAGE");
		
		NotificationManager notificationManager = 
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		CharSequence text = ctx.getString(R.string.app_notification_notiTitle);
		long when = System.currentTimeMillis();
		
	    PendingIntent pendingIntent =PendingIntent.getActivity(ctx, 0, new Intent(ctx, StartScreenPickerActivity.class), 0);

		Notification notification = new Notification(R.drawable.ic_launcher,text, when);
		notification.setLatestEventInfo(ctx, "GildaMax", message, pendingIntent);

	    long[] vibrate = {0,2000};
	    notification.vibrate = vibrate;
	    notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	    
	    notificationManager.notify(0, notification);		
	}

}
