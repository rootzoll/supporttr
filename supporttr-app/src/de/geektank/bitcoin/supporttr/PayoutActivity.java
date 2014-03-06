package de.geektank.bitcoin.supporttr;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.geektank.bitcoin.supporttr.data.PayoutTransaction;
import de.geektank.bitcoin.supporttr.data.SupportItem;
import de.geektank.bitcoin.supporttr.tools.AndroidTools;
import de.geektank.bitcoin.supporttr.tools.AndroidToolsDependencies;
import de.geektank.bitcoin.supporttr.wallets.PaymentActivity;
import de.geektank.bitcoin.supporttr.wallets.PaymentActivityResultListener;
import de.geektank.bitcoin.supporttr.wallets.WalletManager;
import de.geektank.bitcoin.supporttr.web.BtcPriceInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PayoutActivity extends Activity implements PaymentActivity, SupportsActivityListener {
	
	TextView textAtTheTop;
	LinearLayout itemsLayout;
	Button payoutButton;
	Button skipButton;
	ArrayList<PayoutTransaction> transactionsBTC;
	PaymentActivityResultListener listener;
	HashMap<String, ItemUpdateListener> itemInfoUpdateList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payout);
		
		CoreTools.init(this);
		transactionsBTC = CoreTools.getInstance().getPayoutTransactions();
		
		itemInfoUpdateList = new HashMap<String, ItemUpdateListener>();
		
		payoutButton = (Button) findViewById(R.id.forwardPayment);
		payoutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				forwardTrasactionsToWallet();
			}
		});
		if (transactionsBTC.size()==0) payoutButton.setEnabled(false);
		
		skipButton = (Button) findViewById(R.id.skipPayment);
		skipButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		this.textAtTheTop = (TextView) findViewById(R.id.textAtTheTop);
		this.itemsLayout = (LinearLayout) findViewById(R.id.itemsLayout);
		
		List<SupportItem> items = CoreTools.getInstance().getSupportItems();
		
		if (items.size()==0) {
			// TODO: Print Info
			payoutButton.setEnabled(false);
		} else 
		if (CoreTools.getInstance().getBudget()==0) {
			// TODO: Print Info
			payoutButton.setEnabled(false);
		}
		else {
			payoutButton.setEnabled(true);
		}
		
		Collections.sort(items, new Comparator<SupportItem>() {
			public int compare(SupportItem lhs, SupportItem rhs) {
				return (int) ((int) rhs.created - lhs.created);
			}
		});
		
		int counter = 0;
		for (SupportItem item : items) {
			counter++;
			LinearLayout itemLayout = GuiTools.buildPendingDonation(counter, this, item, this);
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 0, 5);
	
			itemsLayout.addView(itemLayout, layoutParams);
		}
		this.updateSupportItems();
		
		BtcPriceInfo.updateBtcPrice(this, new BtcPriceInfo.PriceUpdateListener() {
			@Override
			public void onNewPrice() {
				PayoutActivity.this.updateSupportItems();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void forwardTrasactionsToWallet() {
		/*
		 * Maybe notice about missing support for BIP70
		 */
		final AndroidToolsDependencies dependencies = AndroidToolsDependencies.checkDependencies(this);
		if ((transactionsBTC.size()>1) && (!dependencies.isLocalWalletSupportingPaymentRequest())) {
			GuiTools.showInfo(this, getString(R.string.info_walletNoPaymentRequest_text), getString(R.string.info_walletNoPaymentRequest_title), new DialogInterface.OnClickListener() {
				// Click OK
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AndroidTools.redirectToDownloadDefaultWallet(PayoutActivity.this);
				}
			}, new DialogInterface.OnClickListener() {
				// Click CANCEL
				@Override
				public void onClick(DialogInterface dialog, int which) {
					forwardTrasactionsToWallet(dependencies);
				}
			});
		} else {
			forwardTrasactionsToWallet(dependencies);
		}
	}
	
	public void forwardTrasactionsToWallet(AndroidToolsDependencies dependencies) {
		if (transactionsBTC.size()>0) WalletManager.getInstance(dependencies, CoreTools.getInstance()).getBtcWallet().makePayment(this, transactionsBTC);	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (this.listener!=null) listener.onActivityResult(resultCode, data);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void registerOnActivityResult(PaymentActivityResultListener listener) {
		this.listener = listener;
	}

	@Override
	public void onPaymentComplete(String currencyCode, List<PayoutTransaction> results) {
		
		if (results==null) return; // transaction didnt happend
		
		this.itemsLayout.removeAllViews();
		
		this.textAtTheTop.setVisibility(View.INVISIBLE);
				
		ImageView i = new ImageView(PayoutActivity.this);
		i.setImageResource(R.drawable.quote1);
		i.setPadding(0, 0, 0, 25);
		this.itemsLayout.addView(i);
		
		this.payoutButton.setEnabled(false);
		this.payoutButton.setVisibility(View.INVISIBLE);
		this.skipButton.setVisibility(View.INVISIBLE);
		
		LinearLayout nextLayout = new LinearLayout(this);
		nextLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //layoutParams.setMargins(0, 5, 0, 5);
		itemsLayout.addView(nextLayout, layoutParams);
		ImageView ni = new ImageView(this);
		ni.setImageResource(R.drawable.clock_blue);
		ni.setPadding(0, 10, 4, 0);
		nextLayout.addView(ni);
		TextView tw = new TextView(this);
		String dateStr = null;
		Date date = new Date(CoreTools.getInstance().getNextPayout());
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
		dateStr = dateFormat.format(date);
		dateStr = dateStr + " " + android.text.format.DateFormat.format("kk:mm:ss", date).toString();
		String text = " " + getString(R.string.items_payoutino) + " " + dateStr;
		tw.setText(text);
		nextLayout.addView(tw);
		
		/*
		
		      <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal" >

          <ImageView
              android:id="@+id/imageView1"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:paddingRight="4px"
              android:paddingTop="10px"
              android:src="@drawable/clock_blue" />
          
       <TextView
        android:id="@+id/payoutInfo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text=""
        android:textAppearance="?android:attr/textAppearanceMedium" />
       
       </LinearLayout>
		
		 */
		
		
		Button done = new Button(this);
		done.setText(R.string.noti_close);
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		this.itemsLayout.addView(done);
	}

	public void addItemUpdateListener(String addr, ItemUpdateListener listener) {
		this.itemInfoUpdateList.put(addr, listener);
	}

	public void updateSupportItems() {
		for (String addr : this.itemInfoUpdateList.keySet()) {
			this.itemInfoUpdateList.get(addr).onItemUpdate(addr);
		}
	}

	@Override
	public void refreshSupportItemList() {
		updateSupportItems();
	}

}
