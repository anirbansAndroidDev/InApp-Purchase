package com.androiddoc.inapppurchase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.polus.inappbillingtest.util.IabHelper;
import com.polus.inappbillingtest.util.IabResult;
import com.polus.inappbillingtest.util.Inventory;
import com.polus.inappbillingtest.util.Purchase;

public class MainActivity extends Activity{
	IabHelper mHelper;
	static final int RC_REQUEST = 10001;
	boolean mIsPremium = false;

	
	//++++++++++++++++++++++++++++++++++++++++++ 
	//Change this two value
	//+++++++++++++++++++++++++++++++++++++++++++++

	String product_id = "music_album_01";
	String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzPhylcowMNe8zW7lQHm0fIweF0je2XSLdfWjZ14DnRdID5T1TiWlCoidwi9KEcI6HPrpPBougvOe/TAF1CTzO+C4iWUTQm85JHUq8I4tAFxTSOujWSC5hYeJpRpqBTKrSrnLSmFBs1qJh3Hk1M17pw/hGxTwEHdvSAd96glXIUb4VIUjBqFLSNDBm+7+wRm/ulaTfHsaJc6NBof+vFo0IVwxNdtitW5MCJ96YFV3HJL7a0t3ohUkg1BUw0KrMZjazcEvnJpyqQu3wPB2Q+Vmi04QbqniUziDIl7gE1xaUMvZHoPzHcq3c0rb6v+XCiFZLOUZGs8K0H+Lf7kA40ZdSwIDAQAB";

	//++++++++++++++++++++++++++++++++++++++++++ 
	//Change this two value
	//+++++++++++++++++++++++++++++++++++++++++++++

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);

		loadHelper();
	}

	//=========================================================================================================
	//++++++++++++++++++++++++++++++++++++++++++ FOR INAPP PURCHASE  +++++++++++++++++++++++++++++++++++++++++++++
	//=========================================================================================================
	
	private void loadHelper() 
	{
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() 
		{
			public void onIabSetupFinished(IabResult result) 
			{
				if (!result.isSuccess()) 
				{
					Log.d("TAG", "Problem setting up in-app billing: " + result);
					return;
				}

				if (mHelper == null) return;
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Log.d("TAG", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if (mHelper == null) return;

		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) 
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
		else 
		{
			Log.d("TAG", "onActivityResult handled by IABUtil.");
		}
	}

	public void buyingMusic(View v) 
	{
		String payload = "";
		mHelper.launchPurchaseFlow(this, product_id, IabHelper.ITEM_TYPE_SUBS, RC_REQUEST, mPurchaseFinishedListener, payload);
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() 
	{
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
		{
			if (mHelper == null) return;

			if (result.isFailure()) 
			{
				Log.d("TAG", "Error purchasing: " + result);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) 
			{
				Log.d("TAG", "Error purchasing. Authenticity verification failed.");
				return;
			}

			if (purchase.getSku().equals(product_id)) 
			{
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};

	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		return true;
	}

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() 
	{
		public void onConsumeFinished(Purchase purchase, IabResult result) 
		{
			
			//++++++++++++++++++++++++++++++++++++++++++ 
			//play with the responce  
			//+++++++++++++++++++++++++++++++++++++++++++++

			alert("Consumption finished. Purchase: " + purchase + ", result: " + result);
			Log.d("TAG", "End consumption flow.");
			
			//++++++++++++++++++++++++++++++++++++++++++ 
			//play with the responce  
			//+++++++++++++++++++++++++++++++++++++++++++++

		}
	};

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() 
	{
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) 
		{

			if (mHelper == null) return;

			if (result.isFailure()) {
				return;
			}

			Purchase premiumPurchase = inventory.getPurchase(product_id);
			mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
			mIsPremium = false;
		}
	};

	private void alert(String string) 
	{
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
	}
	
	//=========================================================================================================
	//++++++++++++++++++++++++++++++++++++++++++ FOR INAPP PURCHASE  ++++++++++++++++++++++++++++++++++++++++++
	//=========================================================================================================
}
