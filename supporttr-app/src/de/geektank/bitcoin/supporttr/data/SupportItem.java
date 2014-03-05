package de.geektank.bitcoin.supporttr.data;

import java.io.Serializable;
import java.util.Date;

import de.geektank.bitcoin.supporttr.wallets.WalletManager;

public class SupportItem implements Serializable {

	private static final long serialVersionUID = 1L;

	public SupportItem() {
		this.created = new Date().getTime();
	}
	
	public String address;
	public String currencyCode = WalletManager.CURRENCYCODE_BITCOIN;
	public String comment;
	public float weight = (float) 0.5;
	public long created;
}
