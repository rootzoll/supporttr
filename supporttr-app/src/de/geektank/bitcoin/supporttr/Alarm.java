package de.geektank.bitcoin.supporttr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class Alarm extends BroadcastReceiver 
{    
	private static final String TAG = "Alarm";
	
     @Override
     public void onReceive(Context context, Intent intent) 
     {   
    	  
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();

         Log.i(TAG, "Alarm");
         
         CoreTools.init(context);
         CoreTools.getInstance().triggerNotification(context);

         wl.release();
     }

 public void SetAlarm(Context context, long timeToSchedule)
 {
     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
     Intent i = new Intent(context, Alarm.class);
     PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
     am.set(AlarmManager.RTC_WAKEUP, timeToSchedule, pi);
 }

 public void CancelAlarm(Context context)
 {
     Intent intent = new Intent(context, Alarm.class);
     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     alarmManager.cancel(sender);
 }
}