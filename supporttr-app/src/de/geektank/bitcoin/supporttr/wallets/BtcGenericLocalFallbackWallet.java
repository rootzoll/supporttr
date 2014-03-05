package de.geektank.bitcoin.supporttr.wallets;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import de.geektank.bitcoin.supporttr.data.PayoutTransaction;
import de.geektank.bitcoin.supporttr.tools.BitcoinTools;
import de.schildbach.wallet.integration.android.BitcoinIntegration;

public class BtcGenericLocalFallbackWallet implements Wallet, PaymentActivityResultListener {

	List<PayoutTransaction> transactionsToGo;
	List<PayoutTransaction> transactionsDone;
	PayoutTransaction transactionInTheWorks = null;
	PaymentActivity paymentActivity;
	
	
	@Override
	public void makePayment(PaymentActivity paymentActivity, List<PayoutTransaction> transactions) {
		this.paymentActivity = paymentActivity;
		this.transactionsToGo = transactions;
		this.transactionsDone = new ArrayList<PayoutTransaction>();
		transactionInTheWorks = null;
		pushNextResult();
	}

	private void pushNextResult() {
		if (transactionsToGo.size()>0) {
			transactionInTheWorks = this.transactionsToGo.remove(0);
			BitcoinIntegration.requestForResult(paymentActivity.getActivity(), 0, transactionInTheWorks.addr, BitcoinTools.double2NanoLong(transactionInTheWorks.amount));
		} else {
			//throw new Throwable("No next Result to push forward ... DEAD END.");
		}
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent data) {
		
		// TODO: anaylse result later
		this.transactionsDone.add(transactionInTheWorks);
		
		if (transactionsToGo.size()>0) {
			pushNextResult();
		} else {
			paymentActivity.onPaymentComplete(WalletManager.CURRENCYCODE_BITCOIN, this.transactionsDone);
		}	
	}
		
}
