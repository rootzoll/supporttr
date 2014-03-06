package de.geektank.bitcoin.supporttr;

import java.util.Locale;

import de.geektank.bitcoin.supporttr.data.SupportItem;
import de.geektank.bitcoin.supporttr.tools.BitcoinTools;
import de.geektank.bitcoin.supporttr.wallets.WalletManager;
import de.geektank.bitcoin.supporttr.web.BtcPriceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class GuiTools {
	
	static final String TAG = "GuiTools";

	public static void showInfo(Activity activity, String info) {
		showInfo(activity, info, "Info", null, null);
	}	
	
	public static void showInfo(Activity activity, String info, String title, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
		AlertDialog.Builder  builder = new AlertDialog.Builder(activity).setTitle(title).setMessage(info);
	    if (ok!=null) {
	    	builder.setPositiveButton(android.R.string.yes, ok);
	    } else {
	    	builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
	    }
	    if (cancel!=null) builder.setNegativeButton(android.R.string.no, cancel);
	    builder.show();
	}
		
	public static LinearLayout buildSupportItem(final Activity context, final SupportItem item, final SupportsActivityListener listener) {
		
		LinearLayout result = new LinearLayout(context);
		result.setOrientation(LinearLayout.VERTICAL);
		result.setBackgroundColor(Color.LTGRAY);
		result.setWeightSum(1);
		
		LinearLayout top = new LinearLayout(context);
		top.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout bottom = new LinearLayout(context);
		bottom.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 0, 5, 0);
        layoutParams.gravity = Gravity.LEFT;
		
		Button removeBtn = new Button(context);
		removeBtn.setText("X");
		removeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				deleteRequest(context, item, listener);
			}
		});
		top.addView(removeBtn, layoutParams);
		
		final RatingBar ratingBar = new RatingBar(context);
		ratingBar.setNumStars(3);
		ratingBar.setRating(item.weight*3);;
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (fromUser) {
					item.weight = rating/3;
					CoreTools.getInstance().updateSupportItem(item);
					listener.updateSupportItems();
				}
			}
		});
		top.addView(ratingBar, layoutParams);
		
		LinearLayout textInfos = new LinearLayout(context);
		textInfos.setOrientation(LinearLayout.VERTICAL);
		//textInfos.setBackgroundColor(Color.BLUE);
		textInfos.setVerticalGravity(Gravity.CENTER);
		
		TextView addr = new TextView(context);
		SpannableString spanString = new SpannableString(item.address);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		addr.setText(spanString);
		textInfos.addView(addr);
		
		String comment = "-";
		if ((item.comment!=null) && (item.comment.trim().length()>0)) comment = item.comment.trim();
		TextView com = new TextView(context);
		com.setText(comment);
		textInfos.addView(com);
		
		final TextView amount = new TextView(context);
		amount.setText("-");
		textInfos.addView(amount);
		listener.addItemUpdateListener(item.address, new ItemUpdateListener() {
			public void onItemUpdate(String addr) {
				SupportItem item = CoreTools.getInstance().getSupportItem(addr);
				if (item==null) {
					Log.w(TAG, "No item with addr("+addr+")");
					return;
				}
				double share = CoreTools.getInstance().calcPayoutForSupportItem(item);
				String amountT = "";
				if (item.currencyCode.equals(WalletManager.CURRENCYCODE_BITCOIN)) {
					String fiatValue = "";
					if (BtcPriceInfo.gotInfo()) fiatValue = "\u2248 " + BtcPriceInfo.convertToLocaleFiat(share);
					amountT = context.getString(R.string.items_share) + " " + BitcoinTools.double2mBTC(Locale.getDefault(), share) + " " + context.getString(R.string.items_mbtc) + " " +fiatValue;
				}
				amount.setText(amountT);
			}
		});
		
		bottom.addView(textInfos, layoutParams);
		
		result.addView(top, layoutParams);
		result.addView(bottom, layoutParams);
		
		return result;
	}
	
	public static LinearLayout buildPendingDonation(int counter, final Activity context, final SupportItem item, final SupportsActivityListener listener) {
		
		LinearLayout result = new LinearLayout(context);
		result.setOrientation(LinearLayout.VERTICAL);
		result.setBackgroundColor(Color.argb(128, 215, 215, 215));
		result.setWeightSum(1);
		
		LinearLayout top = new LinearLayout(context);
		top.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout bottom = new LinearLayout(context);
		bottom.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 3, 5, 3);
        layoutParams.gravity = Gravity.LEFT;
				
		LinearLayout textInfos = new LinearLayout(context);
		textInfos.setOrientation(LinearLayout.VERTICAL);
		textInfos.setVerticalGravity(Gravity.CENTER);
		
		TextView addr = new TextView(context);
		SpannableString spanString = new SpannableString(counter+". "+context.getString(R.string.payout_supporting));
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		addr.setText(spanString);
		textInfos.addView(addr);
		
		LinearLayout iconAndText = new LinearLayout(context);
		iconAndText.setOrientation(LinearLayout.HORIZONTAL);
		iconAndText.setVerticalGravity(Gravity.CENTER);
		
		ImageView i = new ImageView(context);
		i.setImageResource(R.drawable.coins);
		i.setPadding(0, 0, 10, 0);
		iconAndText.addView(i);
		
		final TextView amount = new TextView(context);
		amount.setText("-");
		iconAndText.addView(amount);
		listener.addItemUpdateListener(item.address, new ItemUpdateListener() {
			public void onItemUpdate(String addr) {
				SupportItem item = CoreTools.getInstance().getSupportItem(addr);
				if (item==null) {
					Log.w(TAG, "No item with addr("+addr+")");
					return;
				}
				double share = CoreTools.getInstance().calcPayoutForSupportItem(item);
				String amountT = "";
				if (item.currencyCode.equals(WalletManager.CURRENCYCODE_BITCOIN)) {
					String fiatValue = "";
					if (BtcPriceInfo.gotInfo()) fiatValue = "\u2248 " + BtcPriceInfo.convertToLocaleFiat(share) + " ";
					amountT = BitcoinTools.double2mBTC(Locale.getDefault(), share) + " " + context.getString(R.string.items_mbtc) + " " +fiatValue + context.getString(R.string.payout_to)+"\n";
					if ((item.comment!=null) && (item.comment.trim().length()>2)) {
						amountT += item.comment.trim();
					} else {
						amountT += item.address;
					}
					
				}
				amount.setText(amountT);
			}
		});
		
		textInfos.addView(iconAndText);
		bottom.addView(textInfos, layoutParams);
		
		result.addView(top, layoutParams);
		result.addView(bottom, layoutParams);
		
		return result;
	}
	
	public static void deleteRequest(Activity activity, final SupportItem item, final SupportsActivityListener listener) {
		
		String to = item.address;
		if ((item.comment!=null) && (item.comment.length()>1)) to = item.comment;
		
		new AlertDialog.Builder(activity)
	    .setTitle(activity.getString(R.string.items_deleteTitle))
	    .setMessage( activity.getString(R.string.items_delete) + " " + to )
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	 CoreTools.getInstance().removeBtcAddress(item.address);
	        	 listener.refreshSupportItemList();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .show();
	}
	
}
