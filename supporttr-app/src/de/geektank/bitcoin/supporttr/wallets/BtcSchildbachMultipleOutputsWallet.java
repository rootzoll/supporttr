package de.geektank.bitcoin.supporttr.wallets;

import java.util.List;

import android.content.Intent;
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
				
				// TODO: analyse result later
				paymentActivity.onPaymentComplete(WalletManager.CURRENCYCODE_BITCOIN, transactions);
				
			}
		});
		BitcoinIntegration.requestForResult(paymentActivity.getActivity(), 0, BitcoinTools.generatePaymentRequest(CoreTools.getInstance().getPayoutTransactions(),CoreTools.RUNNING_ON_TESTNET));	
	}

}
