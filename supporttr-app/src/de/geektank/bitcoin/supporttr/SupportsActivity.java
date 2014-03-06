package de.geektank.bitcoin.supporttr;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.geektank.bitcoin.supporttr.R;
import de.geektank.bitcoin.supporttr.data.SupportItem;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SupportsActivity extends Activity implements SupportsActivityListener {

	private static final String TAG = "SupportsActivity";
	
	LinearLayout itemsLayout;
	TextView itemsInfo;
	TextView payoutInfo;
	HashMap<String, ItemUpdateListener> itemInfoUpdateList;
	Button forcePayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_supports);

		itemInfoUpdateList = new HashMap<String, ItemUpdateListener>();
		itemsInfo = (TextView) findViewById(R.id.itemsinfo);
		payoutInfo = (TextView) findViewById(R.id.payoutInfo);
		
		itemsLayout = (LinearLayout) findViewById(R.id.itemsLayout);
		
		forcePayout = (Button) findViewById(R.id.forcePayout);
		forcePayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SupportsActivity.this, getString(R.string.notice_triggerNotification), Toast.LENGTH_LONG).show();
					}
				}, 2000);
				
				CoreTools.getInstance().triggerNotification(SupportsActivity.this);
			}
		});
		
		rebuildSupportItemsList();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.supports, menu);
		return true;
	}
	
	private void rebuildSupportItemsList() {
		
		Log.i(TAG, "rebuildSupportItemsList");
		
		String dateStr = null;
		Date date = new Date(CoreTools.getInstance().getNextPayout());
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
		dateStr = dateFormat.format(date);
		dateStr = dateStr + " " + android.text.format.DateFormat.format("kk:mm:ss", date).toString();
		String text = " " + getString(R.string.items_payoutino) + " " + dateStr;
		payoutInfo.setText(text);
		
		itemsLayout.removeAllViews();
		itemInfoUpdateList.clear();
		
		List<SupportItem> items = CoreTools.getInstance().getSupportItems();
		
		if (items.size()==0) {
			itemsInfo.setText(R.string.items_noitems);
			itemsInfo.setTextColor(Color.RED);
			forcePayout.setEnabled(false);
		} else 
		if (CoreTools.getInstance().getBudget()==0) {
			itemsInfo.setText(R.string.items_nobudget);
			itemsInfo.setTextColor(Color.RED);
			forcePayout.setEnabled(false);
		}
		else {
			itemsInfo.setText(R.string.items_info);	
			itemsInfo.setTextColor(Color.BLACK);
			forcePayout.setEnabled(true);
		}
		
		Collections.sort(items, new Comparator<SupportItem>() {
			public int compare(SupportItem lhs, SupportItem rhs) {
				return (int) ((int) rhs.created - lhs.created);
			}
		});
		for (SupportItem item : items) {
			LinearLayout itemLayout = GuiTools.buildSupportItem(this, item, this);
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 0, 5);
	
			itemsLayout.addView(itemLayout, layoutParams);
		}
		this.updateSupportItems();
		
	}

	public void refreshSupportItemList() {
			rebuildSupportItemsList();
	}

	public void addItemUpdateListener(String addr, ItemUpdateListener listener) {
		this.itemInfoUpdateList.put(addr, listener);
	}

	public void updateSupportItems() {
		for (String addr : this.itemInfoUpdateList.keySet()) {
			this.itemInfoUpdateList.get(addr).onItemUpdate(addr);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {    
		Toast.makeText(this, "ActicityResult", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		rebuildSupportItemsList();
	}

	
}
