package de.geektank.bitcoin.supporttr.wallets;

import android.util.Log;
import de.geektank.bitcoin.supporttr.tools.AndroidToolsDependencies;

/**
 * Use to interface the wallet.
 */
public class WalletManager {

	public static final String TAG = "WalletManager";
	
	public static final String CURRENCYCODE_BITCOIN = "BTC";
	
	// Later On remember settings
	public interface WalletManagerPersitence {
	}
	
	static WalletManager instance;
	
	@SuppressWarnings("unused")
	private WalletManagerPersitence persitence;
	
	private Wallet btcWallet;
	
	public static WalletManager getInstance(AndroidToolsDependencies dependencies, WalletManagerPersitence persitence) {
		if (instance==null) instance = new WalletManager(dependencies, persitence);
		return instance;
	}
	
	private WalletManager(AndroidToolsDependencies dependencies, WalletManagerPersitence persitence) {
		this.persitence = persitence;
		
		if ((dependencies.walletBitcoin_Schildbach) || (dependencies.walletBitcoin_SchildbachTest)) {
			Log.i(TAG, "USING PAYMENT REQUEST");
			this.btcWallet = new BtcSchildbachMultipleOutputsWallet();
		} else {
			Log.i(TAG, "USING FALLBACK SINGLE PAYMENT");
			this.btcWallet = new BtcGenericLocalFallbackWallet();
		}
	}
	
	public Wallet getBtcWallet() {
		return this.btcWallet;
	}
}
