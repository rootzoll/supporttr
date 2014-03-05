package de.geektank.bitcoin.supporttr;

import java.util.ArrayList;
import java.util.List;

import de.geektank.bitcoin.supporttr.data.PayoutTransaction;
import de.geektank.bitcoin.supporttr.wallets.PaymentActivity;
import de.geektank.bitcoin.supporttr.wallets.PaymentActivityResultListener;
import de.geektank.bitcoin.supporttr.wallets.WalletManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PayoutActivity extends Activity implements PaymentActivity {
	
	Button payoutButton;
	Button skipButton;
	ArrayList<PayoutTransaction> transactionsBTC;
	PaymentActivityResultListener listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payout);
		
		CoreTools.init(this);
		transactionsBTC = CoreTools.getInstance().getPayoutTransactions();
		
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
		
		TextView details = (TextView) findViewById(R.id.payouDetails);
		details.setText(CoreTools.getInstance().payoutTransactionsToString(this, transactionsBTC));
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
		if (transactionsBTC.size()>0) WalletManager.getInstance(CoreTools.getInstance()).getBtcWallet().makePayment(this, transactionsBTC);
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
		TextView details = (TextView) findViewById(R.id.payouDetails);
		details.setText("DONE ("+currencyCode+") ... present Close Buttons");
	}

}
