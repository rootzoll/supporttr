package de.geektank.bitcoin.supporttr.wallets;

import java.util.List;

import de.geektank.bitcoin.supporttr.data.PayoutTransaction;

public interface Wallet {

	void makePayment(PaymentActivity paymentActivity, List<PayoutTransaction> transactions);
	
}
