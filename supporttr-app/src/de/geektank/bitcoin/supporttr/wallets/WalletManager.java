package de.geektank.bitcoin.supporttr.wallets;

/**
 * Use to interface the wallet.
 */
public class WalletManager {

	public static final String CURRENCYCODE_BITCOIN = "BTC";
	
	// Later On remember settings
	public interface WalletManagerPersitence {
	}
	
	static WalletManager instance;
	
	@SuppressWarnings("unused")
	private WalletManagerPersitence persitence;
	
	private Wallet btcWallet;
	
	public static WalletManager getInstance(WalletManagerPersitence persitence) {
		if (instance==null) instance = new WalletManager(persitence);
		return instance;
	}
	
	private WalletManager(WalletManagerPersitence persitence) {
		this.persitence = persitence;
		
		this.btcWallet = new BtcSchildbachMultipleOutputsWallet();
		
		// TODO: use as fallback when 
		// this.btcWallet = new BtcGenericLocalFallbackWallet();
	}
	
	public Wallet getBtcWallet() {
		return this.btcWallet;
	}
}
