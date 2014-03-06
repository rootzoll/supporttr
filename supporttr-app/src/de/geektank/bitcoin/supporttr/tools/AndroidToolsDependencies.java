package de.geektank.bitcoin.supporttr.tools;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

public class AndroidToolsDependencies {

	public static final String TAG = "AndroidTools";
	
	private static final String INTENT_EXTRA_PAYMENTREQUEST = "paymentrequest";

	private static final String MIMETYPE_PAYMENTREQUEST = "application/bitcoin-paymentrequest"; // BIP 71
	
	public static String packetZXingQrcodeScanner = "com.google.zxing.client.android";
	
	public static String packetNameSchildbachTest = "de.schildbach.wallet_test";
	public static String packetNameSchildbach = "de.schildbach.wallet";
	
	public static String packetNameMyCeliumTest = "com.mycelium.testnetwallet";
	public static String packetNameMyCelium = "com.mycelium.wallet";
	
	public static String packetNameKncWallet = "com.kncwallet.wallet";
	
	
	
	public static String packetNameBlockchainInfoWallet = "piuk.blockchain.android";
	
	
	public boolean qrCodeScanner_GENERIC = false;
	public boolean qrCodeScanner_ZXing = false;
	
	public boolean walletBitcoin_GENERIC = false;
	public boolean walletBitcoin_SchildbachTest = false;
	public boolean walletBitcoin_Schildbach = false;
	public boolean walletBitcoin_myCeliumTest = false;
	public boolean walletBitcoin_myCelium = false;
	public boolean walletBitcoin_BlockchainInfo = false;
	public boolean walletBitcoin_KncWallet = false;
	
	public boolean paymentRequest_GENERIC = false;
	
	
	/**
	 * Just give back TRUE if a well tested Wallet with payment request support is installed.
	 */
	public boolean isLocalWalletSupportingPaymentRequest() {
		if (walletBitcoin_Schildbach) return true;
		if (walletBitcoin_SchildbachTest) return true;
		return false;
	}
	
	/**
	 * Just give back TRUE if wallet if tested with fallback single payments.
	 * @return
	 */
	public boolean isLocalWalletTestedFallback() {
		if (walletBitcoin_myCelium) return true;
		if (walletBitcoin_myCeliumTest) return true;
		return false;
	}
	
	public boolean isKncWalletOnly() {
		
		if (isLocalWalletSupportingPaymentRequest()) return false;
		if (!walletBitcoin_KncWallet) return false;
	
		if (walletBitcoin_myCelium) return false;
		if (walletBitcoin_BlockchainInfo) return false;
		return true;
	}
	
	public boolean isBlockchainWalletOnly() {
		
		if (isLocalWalletSupportingPaymentRequest()) return false;
		if (!walletBitcoin_BlockchainInfo) return false;
	
		if (walletBitcoin_myCelium) return false;
		if (walletBitcoin_KncWallet) return false;
		return true;
	}
	
	public static AndroidToolsDependencies checkDependencies(Context context) {
		
		AndroidToolsDependencies dependencies = new AndroidToolsDependencies();
		
		/*
		 * 1. check by package names 
		 */
		
		final PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo packageInfo : packages) {
			
			String appName = packageInfo.packageName;
			if (appName==null) continue;
			
			Log.d(TAG, "App Available: "+appName+"");
			
			if (appName.equals(packetZXingQrcodeScanner)) dependencies.qrCodeScanner_ZXing = true;
			
			if (appName.equals(packetNameSchildbach)) dependencies.walletBitcoin_Schildbach = true;
			if (appName.equals(packetNameSchildbachTest)) dependencies.walletBitcoin_SchildbachTest = true;
			if (appName.equals(packetNameBlockchainInfoWallet)) dependencies.walletBitcoin_BlockchainInfo = true;
			if (appName.equals(packetNameMyCeliumTest)) dependencies.walletBitcoin_myCeliumTest = true;
			if (appName.equals(packetNameMyCelium)) dependencies.walletBitcoin_myCelium = true;
			if (appName.equals(packetNameKncWallet)) dependencies.walletBitcoin_KncWallet = true;
		}
		
		if (dependencies.qrCodeScanner_ZXing) {
			Log.i(TAG, "AVAILABLE APP: "+packetZXingQrcodeScanner);
		} else {
			Log.w(TAG, "MISSING APP:"+packetZXingQrcodeScanner);
		}
		
		if (dependencies.walletBitcoin_Schildbach) {
			Log.i(TAG, "AVAILABLE APP: "+packetNameSchildbach);
		} else {
			Log.w(TAG, "MISSING APP:"+packetNameSchildbach);
		}
		
		if (dependencies.walletBitcoin_SchildbachTest) {
			Log.i(TAG, "AVAILABLE APP: "+packetNameSchildbachTest);
		} else {
			Log.w(TAG, "MISSING APP:"+packetNameSchildbachTest);
		}
		
		if (dependencies.walletBitcoin_BlockchainInfo) {
			Log.i(TAG, "AVAILABLE APP: "+packetNameBlockchainInfoWallet);
		} else {
			Log.w(TAG, "MISSING APP:"+packetNameBlockchainInfoWallet);
		}
		
		if (dependencies.walletBitcoin_myCeliumTest) {
			Log.i(TAG, "AVAILABLE APP: "+packetNameMyCeliumTest);
		} else {
			Log.w(TAG, "MISSING APP:"+packetNameMyCeliumTest);
		}
		
		if (dependencies.walletBitcoin_myCelium) {
			Log.i(TAG, "AVAILABLE APP: "+packetNameMyCelium);
		} else {
			Log.w(TAG, "MISSING APP:"+packetNameMyCelium);
		}
		
		if (dependencies.walletBitcoin_KncWallet) {
			Log.i(TAG, "AVAILABLE APP: "+packetNameKncWallet);
		} else {
			Log.w(TAG, "MISSING APP:"+packetNameKncWallet);
		}
				
		/*
		 * 2. Check by Intent
		 */
		
		Intent qrCodeIntent = new Intent("com.google.zxing.client.android.SCAN"); 
		qrCodeIntent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE"); 
		if (isAvailable(context, qrCodeIntent)) {
			dependencies.qrCodeScanner_GENERIC = true;
			Log.i(TAG, "QR Code Intent SUPPORTED");
		} else {
			Log.w(TAG, "QR Code Intent NOT SUPPORTED");
		}
		
		final Intent walletIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("bitcoin:15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD"));
		if (isAvailable(context, walletIntent)) {
			dependencies.walletBitcoin_GENERIC = true;
			Log.i(TAG, "Bitcoin Wallet Intent SUPPORTED");
		} else {
			Log.w(TAG, "Bitcoin Wallet Intent NOT SUPPORTED");
		}
		
		final Intent paymentRequestIntent = new Intent(Intent.ACTION_VIEW);
		paymentRequestIntent.setType(MIMETYPE_PAYMENTREQUEST);
		paymentRequestIntent.putExtra(INTENT_EXTRA_PAYMENTREQUEST, "");
		if (isAvailable(context, walletIntent)) {
			dependencies.paymentRequest_GENERIC = true;
			Log.i(TAG, "PaymentRequest Intent SUPPORTED");
		} else {
			Log.w(TAG, "PaymentRequest Intent NOT SUPPORTED");
		}
		
		return dependencies;
		
	}
	
	public static boolean isAvailable(Context ctx, Intent intent) {	
		final PackageManager pm = ctx.getPackageManager();
		if (pm.resolveActivity(intent, 0) != null) {
			return true;
		} else  { 
			return false;
		}
	}

}
