package de.geektank.bitcoin.supporttr;

import de.geektank.bitcoin.supporttr.data.SupportItem;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class GuiTools {
	
	static final String TAG = "GuiTools";

	public static void showInfo(Activity activity, String info) {
		new AlertDialog.Builder(activity)
	    .setTitle("Info")
	    .setMessage(info)
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	 // do nothing
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .show();
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
		if (item.comment!=null) comment = item.comment.trim();
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
				float share = CoreTools.getInstance().calcPayoutForSupportItem(item);
				String amountT = context.getString(R.string.items_share) + " " + share + " " + context.getString(R.string.items_mbtc);
				amount.setText(amountT);
			}
		});
		
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
