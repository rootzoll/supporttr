package de.geektank.bitcoin.supporttr;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver
{   
	private static final String TAG = "AutoStart";
	
    @Override
    public void onReceive(Context context, Intent intent)
    {   
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
        	
        	Log.i(TAG, "BOOT COMPLETE RECEIVED");
        	
        	CoreTools.init(context);
        	Long nextPayout = CoreTools.getInstance().getNextPayout();
        	if (nextPayout<=(new Date().getTime())) {
        		Log.i(TAG, "payout time alreday in the past ... starting in 10 secs");
        		nextPayout = new Date().getTime() + (10*1000);
        	}
        	Log.i(TAG, "Setting Payoutalarm for "+nextPayout);
        	CoreTools.getInstance().setAlarm(context, nextPayout);

        }
    }
}