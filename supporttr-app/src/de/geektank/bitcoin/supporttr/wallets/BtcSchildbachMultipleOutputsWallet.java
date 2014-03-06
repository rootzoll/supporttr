package de.geektank.bitcoin.supporttr.wallets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.geektank.bitcoin.supporttr.CoreTools;
import de.geektank.bitcoin.supporttr.data.PayoutTransaction;
import de.geektank.bitcoin.supporttr.tools.BitcoinTools;
import de.schildbach.wallet.integration.android.BitcoinIntegration;

public class BtcSchildbachMultipleOutputsWallet implements Wallet {

	@Override
	public void makePayment(final PaymentActivity paymentActivity, final List<PayoutTransaction> transactions) {
		paymentActivity.registerOnActivityResult(new PaymentActivityResultListener() {
			@Override
			public void onActivityResult(int resultCode, Intent data) {
				
				List<PayoutTransaction> result = null;
				if (data!=null) {
					Bundle extra = data.getExtras();
					if (extra.containsKey("transaction_hash")) result = transactions;
				}
				
				paymentActivity.onPaymentComplete(WalletManager.CURRENCYCODE_BITCOIN, result);
				
			}
		});
		BitcoinIntegration.requestForResult(paymentActivity.getActivity(), 0, BitcoinTools.generatePaymentRequest(CoreTools.getInstance().getPayoutTransactions(),CoreTools.RUNNING_ON_TESTNET));	
	}

}
