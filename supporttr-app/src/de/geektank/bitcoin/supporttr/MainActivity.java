package de.geektank.bitcoin.supporttr;

import de.geektank.bitcoin.supporttr.R;
import de.geektank.bitcoin.supporttr.tools.AndroidTools;
import de.geektank.bitcoin.supporttr.tools.AndroidToolsDependencies;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnTabChangeListener {

	public final String TAG = "MainActivity";
	
	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		CoreTools.init(this);

		this.tabHost = getTabHost();
		
		// Tab for Start
        TabSpec startSpec = tabHost.newTabSpec("Start");
        startSpec.setIndicator(getString(R.string.tab_start));//, getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent startsIntent = new Intent(this, StartActivity.class);
        startSpec.setContent(startsIntent);
        tabHost.addTab(startSpec);
        
		// Tab for Supports
        TabSpec supportsSpec = tabHost.newTabSpec("Supports");
        supportsSpec.setIndicator(getString(R.string.tab_support));//, getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent supportsIntent = new Intent(this, SupportsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        supportsSpec.setContent(supportsIntent);
        tabHost.addTab(supportsSpec);
        
        check4QrCodeScanner();
        
	}
	
	public void check4QrCodeScanner() {
		
        AndroidToolsDependencies dependencies = AndroidToolsDependencies.checkDependencies(this);
        
        if (!dependencies.qrCodeScanner_GENERIC) {
        	GuiTools.showInfo(getCurrentActivity(), getString(R.string.info_qrreader_text), getString(R.string.info_qrreader_title), 
        		new DialogInterface.OnClickListener() {
        			// Click on OK
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AndroidTools.redirectToDownloadQrCodeScanner(MainActivity.this);
						check4AnyWallet();
					}},
				new DialogInterface.OnClickListener() {
					// Click on CANCEL
					@Override
					public void onClick(DialogInterface dialog, int which) {
						check4AnyWallet();
					}
				});	
        } else {
        	check4AnyWallet();
        }
		
	}
	
	public void check4AnyWallet() {
		
		AndroidToolsDependencies dependencies = AndroidToolsDependencies.checkDependencies(this);
		
        if (!dependencies.walletBitcoin_GENERIC) {
        	GuiTools.showInfo(getCurrentActivity(), getString(R.string.info_walletmissing_text), getString(R.string.info_walletmissing_title), 
            		new DialogInterface.OnClickListener() {
            			// Click on OK
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						AndroidTools.redirectToDownloadDefaultWallet(MainActivity.this);
    					}},
    				new DialogInterface.OnClickListener() {
    					// Click on CANCEL
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    					}
    				});
        } else {
        	     check4PaymentRequestSupportingWallets();   	
        }
	}
	
	public void check4PaymentRequestSupportingWallets() {
		AndroidToolsDependencies dependencies = AndroidToolsDependencies.checkDependencies(this);
    	if (!dependencies.isLocalWalletSupportingPaymentRequest()) {
    		
    		String title = getString(R.string.info_walletunsupported_title);
    		String text = getString(R.string.info_walletunsupported_text);
    		
    		if (dependencies.isBlockchainWalletOnly()) {
        		title = getString(R.string.info_walletBlockchain_title);
        		text = getString(R.string.info_walletBlockcain_text);
    		}
    		
    		if (dependencies.isKncWalletOnly()) {
        		title = getString(R.string.info_walletKnc_title);
        		text = getString(R.string.info_walletKnc_text);    			
    		}
    		
        	if (!dependencies.isLocalWalletTestedFallback()) GuiTools.showInfo(getCurrentActivity(), text , title, 
            		new DialogInterface.OnClickListener() {
            			// Click on OK
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						AndroidTools.redirectToDownloadDefaultWallet(MainActivity.this);
    					}},
    				new DialogInterface.OnClickListener() {
    					// Click on CANCEL
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    					}
    				});
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) {
		    
		  /*
		  case R.id.action_settings:
		     
		       Toast.makeText(getBaseContext(), R.string.menu_info_settings, Toast.LENGTH_LONG).show();	
		    	
		      return true;
		    */
		      
		    case R.id.action_uber:
		    	
		    	GuiTools.showInfo(this, getString(R.string.menu_info_uber));
		  
		      return true;
		    default:
		      return super.onOptionsItemSelected(item);
		  }
		}
	
	public void switchTab(int tab){
        tabHost.setCurrentTab(tab);
	}

	public void onTabChanged(String tabId) {
	}

}
