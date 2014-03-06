package de.geektank.bitcoin.supporttr.tools;

import de.geektank.bitcoin.supporttr.CoreTools;
import de.geektank.bitcoin.supporttr.R;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class AndroidTools {
	
	public static final String TAG = "AndroidTools";
	
	public static void redirectToDownloadQrCodeScanner(final Context context) {

		Toast.makeText(context, context.getString(R.string.toast_installQrCodeScanner), Toast.LENGTH_LONG).show();
		
		final Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+AndroidToolsDependencies.packetZXingQrcodeScanner));
		final Intent binaryIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/zxing/downloads/list"));
	
		final PackageManager pm = context.getPackageManager();
		if (pm.resolveActivity(marketIntent, 0) != null)
			context.startActivity(marketIntent);
		else if (pm.resolveActivity(binaryIntent, 0) != null)
			context.startActivity(binaryIntent);
		// else out of luck
	}
	
	@SuppressWarnings("unused")
	public static void redirectToDownloadDefaultWallet(final Context context)
	{
		
		Toast.makeText(context, context.getString(R.string.toast_installWallet), Toast.LENGTH_LONG).show();
		
		Intent marketIntent;
		if (CoreTools.RUNNING_ON_TESTNET) {
			marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=de.schildbach.wallet_test"));
		} else {
			marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=de.schildbach.wallet"));
		}
		
		final Intent binaryIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/bitcoin-wallet/downloads/list"));

		final PackageManager pm = context.getPackageManager();
		if (pm.resolveActivity(marketIntent, 0) != null)
			context.startActivity(marketIntent);
		else if ((!CoreTools.RUNNING_ON_TESTNET) && (pm.resolveActivity(binaryIntent, 0) != null))
			context.startActivity(binaryIntent);
		// else out of luck
	}
	
	public static Intent getQrCodeReaderIntent(Context ctx) {	
		
		AndroidToolsDependencies dependencies = AndroidToolsDependencies.checkDependencies(ctx);
		if (!dependencies.qrCodeScanner_GENERIC) {
			Toast.makeText(ctx, "No QR Scanner ... redirect to download.", Toast.LENGTH_LONG).show();
			redirectToDownloadQrCodeScanner(ctx);
			return null;
		}
		
		Intent intent = new Intent("com.google.zxing.client.android.SCAN"); 
		intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE"); 
		return intent;
	}
	
}
