package de.geektank.bitcoin.supporttr;

public interface SupportsActivityListener {

	public void refreshSupportItemList();
	public void addItemUpdateListener(String addr, ItemUpdateListener listener);
	public void updateSupportItems();
	
}
