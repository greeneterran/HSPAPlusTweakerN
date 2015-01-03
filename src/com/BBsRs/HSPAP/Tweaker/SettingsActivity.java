package com.BBsRs.HSPAP.Tweaker;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;


public class SettingsActivity extends PreferenceActivity {
	
	
	//!----------------------------------BILLING-----------------------------------------------------!
	// PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "ad_disabler";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzEkpr/BGK3QP6wKLoC4k+M6GEkC9KM6QJrZlku4YLEB9i8R0zsXBLpT/ulOGeInfsxzm8v/MVh08d8dYiZG1XVBuLXVVhIM6woCTe/2Hp2gzRc2gnotKVIG6UuRu7BMa9ZGILodbqaDjPY3f6dFYylp57ye+XQTyK5GfjcBRA3a0N26+2Py1Goq2c4PTz4JsPoGNdf1h8GzCnhAO+9w4SyZbLPSH8xUEBo6f2KD6S5skSAeUIvdGUSGVvoUiG+eYlG3WzBOfGj0NBA8SzIFUDc1EFXOz6WY3TRu9ymPsnflRvZpj9OEFVoRX/aB+8R6GxhNtNQcVkKPMxvbh6HIaTQIDAQAB"; // PUT YOUR MERCHANT KEY HERE;

	private BillingProcessor bp;
	private boolean readyToPurchase = false;
	//!----------------------------------BILLING-----------------------------------------------------!

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActionBar().setTitle(R.string.action_settings);						
	    getActionBar().setIcon(android.R.color.transparent);
	    addPreferencesFromResource(R.xml.preferences);
	    
	    //!----------------------------------BILLING-----------------------------------------------------!
	    bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
            	startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            	overridePendingTransition(0, 0);
            	finish();
            }
            @Override
            public void onBillingError(int errorCode, Throwable error) {
            }
            @Override
            public void onBillingInitialized() {
            	mainTask();
                readyToPurchase = true;
            }
            @Override
            public void onPurchaseHistoryRestored() {
            }
        });
	    //!----------------------------------BILLING-----------------------------------------------------!
	}
	
	public void mainTask() {
		@SuppressWarnings("deprecation")
		Preference buyAd = findPreference("buyAd");
		if (bp.isPurchased(PRODUCT_ID)) {buyAd.setSelectable(false); buyAd.setTitle(R.string.buyedAd);}
		buyAd.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (!readyToPurchase) {
		            Toast.makeText(getApplication(), "Billing not initialized.", Toast.LENGTH_LONG).show();
		            return false;
		        } else{
		        	bp.purchase(PRODUCT_ID);
		        }
				return false;
			}

		});
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onDestroy() {
        if (bp != null) 
            bp.release();
        super.onDestroy();
    }

}
