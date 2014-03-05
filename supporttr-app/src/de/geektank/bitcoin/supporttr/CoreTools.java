package de.geektank.bitcoin.supporttr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.geektank.bitcoin.supporttr.data.PayoutTransaction;
import de.geektank.bitcoin.supporttr.data.SupportItem;
import de.geektank.bitcoin.supporttr.tools.BitcoinTools;
import de.geektank.bitcoin.supporttr.tools.SupporttrTools;
import de.geektank.bitcoin.supporttr.wallets.WalletManager.WalletManagerPersitence;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

public class CoreTools implements WalletManagerPersitence {

	// SWITCH ON TRUE WHEN DEVELOPING
	public static final boolean RUNNING_ON_TESTNET = true;
	
	static final String TAG = "Coretools";
		
	private static SharedPreferences sharedPref;
	static CoreTools singleton;
	
	private ArrayList<SupportItem> supportItems;
	private Integer budget;
	private Long lastPayout;
	private Alarm alarm;
	
	public static CoreTools getInstance() {
		if (singleton==null) singleton = new CoreTools();
		return singleton;
	}
		
	public static void init(Context context) {
		sharedPref = context.getSharedPreferences("storageV1", Context.MODE_PRIVATE);
	}
	
	private CoreTools() {
		
		// set default values
		supportItems = new ArrayList<SupportItem>();
		budget = 0;
		lastPayout = new Date().getTime();
		
		// load persitet values
		loadState();
	}
	
	public Integer getBudget() {
		return budget;
	}

	public void setBudget(Integer budget) {
		this.budget = budget;
		persistState();
	}
		
	public void addBtcAddress(String value) {
		if (containsBtcAddress(value)) return;
		SupportItem item = new SupportItem();
		item.address = value;
		this.supportItems.add(item);
		persistState();
	}
	
	public boolean containsBtcAddress(String value) {
		if (value==null) return false;
		boolean result = false;
		for (SupportItem item : this.supportItems) {
			if (value.equals(item.address)) result = true;
		}
		return result;
	}

	public void removeBtcAddress(String address) {
		if (!containsBtcAddress(address)) return;
		SupportItem itemToRemove = null;
		for (SupportItem item : this.supportItems) {
			if (address.equals(item.address)) itemToRemove = item;
		}
		this.supportItems.remove(itemToRemove);
		persistState();
	}
	
	public List<SupportItem> getSupportItems() {
		return this.supportItems;
	}

	public void addSupportItem(SupportItem item) {
		removeBtcAddress(item.address);
		this.supportItems.add(item);
		persistState();
	}

	public void updateSupportItem(SupportItem item) {
		removeBtcAddress(item.address);
		this.supportItems.add(item);
		persistState();
	}
	
   @SuppressWarnings("unchecked")
   private void loadState() {
	   try {
		   String sItems = sharedPref.getString("items", null);
		   if (sItems!=null) this.supportItems = (ArrayList<SupportItem>) fromString(sItems);
		   String sBudget = sharedPref.getString("budget", null);
		   if (sBudget!=null) this.budget = (Integer) fromString(sBudget);
		   String sPayout = sharedPref.getString("lastpayout", null);
		   if (sPayout!=null) this.lastPayout = (Long) fromString(sPayout);
		   Log.i(TAG, "loadState DONE");
	   } catch (Exception e) {
		   Log.e(TAG, "Was not able to load state.", e);  
	   }
   }	
	
   private void persistState() {
	   try {
		   sharedPref.edit().putString("items", toString(this.supportItems)).apply();
		   sharedPref.edit().putString("budget", toString(this.budget)).apply();
		   sharedPref.edit().putString("lastpayout", toString(this.lastPayout)).apply();
		   Log.i(TAG, "persistState DONE");
	   } catch (Exception e) {
		   Log.e(TAG, "Was not able to persit state.", e);
	   }
   } 
	
    /** Read the object from Base64 string. */
   private static Object fromString( String s ) throws IOException ,ClassNotFoundException {
        byte [] data = Base64.decode( s, Base64.DEFAULT );
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
   }

    /** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64.encode( baos.toByteArray() , Base64.DEFAULT) );
    }

	public SupportItem getSupportItem(String addr) {
		for (SupportItem item : this.supportItems) {
			if (item.address.equals(addr)) return item;
		}
		return null;
	}
    
	public float calcPayoutForSupportItem(SupportItem item) {
		
		// sum all weight
		float sum = 0;
		for (SupportItem sItem : this.supportItems) {
			sum += sItem.weight;
		}

		return (item.weight / sum) * this.budget;
	}

	public Long getLastPayout() {
		return lastPayout;
	}
	
	public Long getNextPayout() {
		return SupporttrTools.getTimestampOneMonthAhead(lastPayout);
	}

	public void setLastPayout(Long lastPayout) {
		this.lastPayout = lastPayout;
		persistState();
	}

	public void setNextAlarm(Context context) {
		setLastPayout(new Date().getTime());
		setAlarm(context, getNextPayout());
	}
	
	public void setAlarm(Context context, Long time) {
		if (this.alarm!=null) alarm.CancelAlarm(context);
		this.alarm = new Alarm();
		this.alarm.SetAlarm(context, time);
	}
	
	public ArrayList<PayoutTransaction> getPayoutTransactions() {
		ArrayList<PayoutTransaction> result = new ArrayList<PayoutTransaction>();
		for (SupportItem item : this.supportItems) {
			PayoutTransaction ta = new PayoutTransaction();
			ta.addr = item.address;
			ta.note = item.comment;
			ta.amount = (double) (calcPayoutForSupportItem(item) * 1000);
			result.add(ta);
		}
		return result;
	}
	
	public String payoutTransactionsToString(Context context, ArrayList<PayoutTransaction> list) {
		StringBuffer buff = new StringBuffer();
		int count = 0;
		for (PayoutTransaction payoutTransaction : list) {
			count++;
			buff.append(count + ". " + context.getString(R.string.payout_supporting) + " " + payoutTransaction.note+"\n");
			buff.append(context.getString(R.string.payout_sending) + " " + BitcoinTools.double2mBTC(payoutTransaction.amount) +"mBTC "+ context.getString(R.string.payout_to)  +" "+payoutTransaction.addr+"\n\n");
		}
		return buff.toString();
	}
	
	public void triggerNotification(Context context) {
		
		 /*
		  * make intent for notification
		  */

	     Intent paymentIntent = new Intent(context, PayoutActivity.class);
	     paymentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     PendingIntent pIntent = PendingIntent.getActivity(context, 0, paymentIntent, 0);

	     NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	     Notification.Builder builder = new Notification.Builder(context);

	     builder
	     .setSmallIcon(R.drawable.icon)
	     .setContentTitle(context.getString(R.string.noti_title))
	     .setContentText(context.getString(R.string.noti_text))
	     .setContentInfo(context.getString(R.string.noti_content))
	     .setTicker(context.getString(R.string.noti_ticker))
	     .setLights(0xFFFF0000, 500, 500) //setLights (int argb, int onMs, int offMs)
	     .setContentIntent(pIntent)
	     .setAutoCancel(true);

	     Notification notification = builder.getNotification();
	     notificationManager.notify(R.drawable.icon, notification);

	     /*
	      * schedule next alarm 
	      */

	     setNextAlarm(context);
	     
	}
		
}
