package de.geektank.bitcoin.supporttr.wallets;

import java.util.List;

import android.app.Activity;
import de.geektank.bitcoin.supporttr.data.PayoutTransaction;

public interface PaymentActivity {

	Activity getActivity();
	void registerOnActivityResult(PaymentActivityResultListener listener);
	void onPaymentComplete(String currencyCode, List<PayoutTransaction> results);
	
}
