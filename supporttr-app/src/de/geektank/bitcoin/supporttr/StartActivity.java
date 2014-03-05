package de.geektank.bitcoin.supporttr;

import de.geektank.bitcoin.supporttr.R;
import de.geektank.bitcoin.supporttr.data.SupportItem;
import de.geektank.bitcoin.supporttr.tools.BitcoinTools;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StartActivity extends Activity {

	private final static String TAG = "StartActivity";
	
	TextView mbtcInfo;
	EditText mbtcSpending;
	Button enterAddress;
	Button scanAddress;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	
		enterAddress = (Button) findViewById(R.id.inputAddress);
		enterAddress.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				enterBtcAddress();
			}
		});
		
		scanAddress = (Button) findViewById(R.id.scanAddress);
		scanAddress.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN"); 
				intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE"); 
				startActivityForResult(intent, 0);
			}
		});
		
		mbtcInfo = (TextView) findViewById(R.id.mbtcinfo);
		
		mbtcSpending = (EditText) findViewById(R.id.inputMBTC);
		if (CoreTools.getInstance().getBudget()!=0) mbtcSpending.setText(""+CoreTools.getInstance().getBudget());
		
		mbtcSpending.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				updateMBTCInfo();
			}

			public void afterTextChanged(Editable s) { }

			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
		});
		updateMBTCInfo();
		
	}
	
	private void updateMBTCInfo() {
		
		if (mbtcSpending.getText().toString().length()==0) {
			mbtcInfo.setText("<-- " + getString(R.string.start_pls_set));
			mbtcInfo.setTextColor(Color.RED);
			mbtcSpending.setTag("0");
			return;
		}
		
		int mbtc = Integer.parseInt(mbtcSpending.getText().toString());
		
		if (mbtc < 0) {
			mbtcSpending.setText("0");
			return;
		}
		
		if (mbtc > 1000) {
			mbtcSpending.setText("1000");
			return;
		}
		
		if (mbtc==0) {
			mbtcInfo.setText("<-- " + getString(R.string.start_pls_set));
			mbtcInfo.setTextColor(Color.RED);
		} else {
			mbtcInfo.setText(" " + getString(R.string.start_ok));
			mbtcInfo.setTextColor(Color.GREEN);
		}
		
		if (mbtc!=CoreTools.getInstance().getBudget()) CoreTools.getInstance().setBudget(mbtc);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	public void enterBtcAddress() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("New Support");
		alert.setMessage("Enter Bitcoinaddress");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String value = input.getText().toString();
			if (value!=null) value = BitcoinTools.stripBitcoinAddress(value);
			if (BitcoinTools.validateBitcoinAddress(value, CoreTools.RUNNING_ON_TESTNET)) {
				SupportItem supportItem = new SupportItem();
				supportItem.address = value;
				enterComment(supportItem);
			} else {
				GuiTools.showInfo(StartActivity.this, getString(R.string.info_addressNotValid));
			}
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	
	public void enterComment(final SupportItem item) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(item.address);
		alert.setMessage("Enter a short comment to remember who you are supporting:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String value = input.getText().toString();
			if ((value!=null) && (value.length()>25)) value = value.substring(0,25);
			item.comment = value;
			CoreTools.getInstance().addSupportItem(item);
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			MainActivity parentActivity;
	        parentActivity = (MainActivity) StartActivity.this.getParent();
	        parentActivity.switchTab(1);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {     
		if(requestCode == 0)     {         
			if(resultCode == RESULT_OK)         {             
				String value = intent.getStringExtra("SCAN_RESULT");  
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT"); 
				
				Log.i("xZing", "contents: "+value+" format: "+format);    
				
				if (value!=null) value = BitcoinTools.stripBitcoinAddress(value);
				
				Log.i(TAG, "Stripped Address: "+value);
				
				if (BitcoinTools.validateBitcoinAddress(value, CoreTools.RUNNING_ON_TESTNET)) {
					SupportItem supportItem = new SupportItem();
					supportItem.address = value;
					enterComment(supportItem);
				} else {
					GuiTools.showInfo(StartActivity.this, getString(R.string.info_addressNotValid)+" : "+value);
				}
				          
				// Handle successful scan         
			}  else if(resultCode == RESULT_CANCELED) {              
				// Handle cancel             
				Log.i("xZing", "Cancelled");         
			}
		}
	}
	
}