package de.geektank.bitcoin.supporttr.web;

import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class BtcPriceInfo {

	public static final String TAG = "BtcPriceInfo";
	
	public interface PriceUpdateListener {
		public void onNewPrice();
	}
	
	static private double btcEuro = 0;
	static private double btcDollar = 0;
	
	static private String symbolEuro = "\u20AC";
	static private String symbolDollar = "\u0024";
	
	public static boolean gotInfo() {
		return (btcEuro!=0);
	}
	
	public static String convertToEuro(double btc) {
		return String.format(Locale.GERMAN, "%.2f", (btc*btcEuro)) + " "+symbolEuro;
	}
	
	public static String convertToDollar(double btc) {
		return String.format(Locale.US, "%.2f", (btc*btcDollar)) + " "+symbolDollar;
	}
	
	public static String convertToLocaleFiat(double btc) {
		if (Locale.getDefault().equals(Locale.GERMAN)) {
			return convertToEuro(btc);
		} else {
			return convertToDollar(btc);
		}
	}
	
	public static void updateBtcPrice(final Context ctx, final PriceUpdateListener priceUpdateListener) {
		
        new PublicHttpRequest(ctx, new PublicHttpRequest.ResultListener() {
			@Override
			public void onResult(String result) {
				
				try {
				
				JSONObject jObject = new JSONObject(result);
				
				// EUR
				JSONObject eurInfo = jObject.getJSONObject("EUR");
				btcEuro = eurInfo.getDouble("15m");
				
				// USD
				JSONObject usdInfo = jObject.getJSONObject("USD");
				btcDollar = usdInfo.getDouble("15m");
			
				} catch (Exception e) {
					Log.e(TAG, "Error on parsing JSON ("+result+")", e);
				}
				
				priceUpdateListener.onNewPrice();
			}
			@Override
			public void onError() {
				Log.w(TAG, "Error on getting BTC price from blockchain.info");
			}
		}).execute("http://blockchain.info/ticker");
	}
	
}
