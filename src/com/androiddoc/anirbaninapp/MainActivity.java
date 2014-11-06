package com.androiddoc.anirbaninapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;

public class MainActivity extends Activity{

	IabHelper mHelper;
	static final String ITEM_SKU = "music001";
	private Button buyButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buyButton = (Button)findViewById(R.id.buyButton);

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwxNBcT2iWZoERa1c9pnqZxFf1ykplq4HnlAUhUF8TOf2jvoL7WX6cmJtj5ttEzYZ7oYnjOwAcpHoqpz4Qggp6zBtoRoUCPLZcuRsTNWoonw0fhnUiN7dOMHCb+bk229Y+bquVowcxWvdnTW1eeHOG1A5k21HHOcJaVOqqpMRBuNN+/x2/18FZAECVn9LpNNOXmJQ7w3OzpcFEjzU7nrUBjN7L3KzzugnsFb6Qpx9BOHPcCXen1MXOVq/V8nc+9zEXZj5EWSiaPFh0zE9ELc5a2WgAbegw93JDjJUpBrjZ8hjXLwH16kmmEy5MimZY+BZvWrIc8LlRCqHfsLXiMnfwwIDAQAB";
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.enableDebugLogging(true);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {

				if (!result.isSuccess()) 
				{
					Log.i("TAG", "Problem setting up in-app billing: " + result);
					return;
				}
				else 
				{             
					Log.d("TAG", "In-app Billing is set up OK");
				}
			}
		});
	}


	public void buy60(View v) 
	{
		buyButton.setEnabled(false);
		mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) 
		{     
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() 
	{
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
		{
			if (result.isFailure()) 
			{
				// Handle error
				return;
			}      
			else if (purchase.getSku().equals(ITEM_SKU)) 
			{
				consumeItem();
			}
		}
	};

	public void consumeItem() 
	{
		mHelper.queryInventoryAsync(mReceivedInventoryListener);
	}

	IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() 
	{
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) 
		{
			if (result.isFailure()) 
			{
				// Handle failure
			} 
			else 
			{
				mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
			}
		}
	};

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() 
	{
		public void onConsumeFinished(Purchase purchase, IabResult result) 
		{

			alert("Purchase Result: " + purchase);
			if (result.isSuccess())
			{		  
				Toast.makeText(MainActivity.this, "Success !!", Toast.LENGTH_SHORT).show();
			} 
			else 
			{
				Toast.makeText(MainActivity.this, "Faild !!", Toast.LENGTH_SHORT).show();
			}
			buyButton.setEnabled(true);
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
	}

	void alert(String message) 
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}
}
