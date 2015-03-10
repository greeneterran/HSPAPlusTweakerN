package com.BBsRs.HSPAP.Tweaker;



import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class TaskerActivity extends Activity {
	
	//!----------------------------------BILLING-----------------------------------------------------!
		// PRODUCT & SUBSCRIPTION IDS
	    private static final String PRODUCT_ID = "ad_disabler";
	    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzEkpr/BGK3QP6wKLoC4k+M6GEkC9KM6QJrZlku4YLEB9i8R0zsXBLpT/ulOGeInfsxzm8v/MVh08d8dYiZG1XVBuLXVVhIM6woCTe/2Hp2gzRc2gnotKVIG6UuRu7BMa9ZGILodbqaDjPY3f6dFYylp57ye+XQTyK5GfjcBRA3a0N26+2Py1Goq2c4PTz4JsPoGNdf1h8GzCnhAO+9w4SyZbLPSH8xUEBo6f2KD6S5skSAeUIvdGUSGVvoUiG+eYlG3WzBOfGj0NBA8SzIFUDc1EFXOz6WY3TRu9ymPsnflRvZpj9OEFVoRX/aB+8R6GxhNtNQcVkKPMxvbh6HIaTQIDAQAB"; // PUT YOUR MERCHANT KEY HERE;

		private BillingProcessor bp;
		private boolean readyToPurchase = false;
	//!----------------------------------BILLING-----------------------------------------------------!

	private static final int ANIMATION_DURATION = 300;
	private static final int ANIMATION_DURATION_ERROR = 600;
	private float mFullScreenScale;
	
	private Context mContext;
	
	private ImageView mBackgroundShape;
	private ImageView mBackgroundShape2;
	private TextView mLightbulb;
	private TextView mTextLogger;
	private ScrollView mScrollView;
	
	private String log = "";
	private int logLength = 0;
	
	SharedPreferences sPref;
	
	Menu mainMenu = null;	
	
	//!----------------------------------AD-----------------------------------------------------!
	private AdView adView;
	//!----------------------------------AD-----------------------------------------------------!
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().show();
        getActionBar().setTitle(R.string.app_name);					
	    getActionBar().setIcon(android.R.color.transparent);
	    
	    registerReceiver(uiUpdated, new IntentFilter("DOWNLOAD_UPDATED"));
        
		setContentView(R.layout.activity_tasker);
		
		 //!----------------------------------BILLING-----------------------------------------------------!
	    bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
            }
            @Override
            public void onBillingError(int errorCode, Throwable error) {
            }
            @Override
            public void onBillingInitialized() {
            	if (!bp.isPurchased(PRODUCT_ID))
            	showAd();
                readyToPurchase = true;
            }
            @Override
            public void onPurchaseHistoryRestored() {
            }
        });
	    //!----------------------------------BILLING-----------------------------------------------------!
	    
	    if(!(savedInstanceState == null))
			log = savedInstanceState.getString("log");
	    
	    mBackgroundShape = (ImageView) findViewById(R.id.bg);
		mBackgroundShape2 = (ImageView) findViewById(R.id.bgTwo);
		mLightbulb = (TextView) findViewById(R.id.lightbulb);
		mTextLogger = (TextView) findViewById(R.id.textLogger);
		mScrollView = (ScrollView) findViewById(R.id.scrollView1);
		
		mLightbulb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (isMyServiceRunning(TaskerService.class))
            		onServiceOff();
            	else
            		onServiceOn();
            }
        });
	}
	
	public void showAd(){
		
		//!----------------------------------AD-----------------------------------------------------!
		if (!bp.isPurchased(PRODUCT_ID)){
		// Создание экземпляра adView.
	    adView = new AdView(this);
	    adView.setAdUnitId("ca-app-pub-6690318766939525/6003666896");
	    adView.setAdSize(AdSize.BANNER);

	    // Поиск разметки LinearLayout (предполагается, что ей был присвоен
	    // атрибут android:id="@+id/mainLayout").
	    LinearLayout layout = (LinearLayout)findViewById(R.id.mainRtLt);
	    
	    // Добавление в разметку экземпляра adView.
	    layout.addView(adView);
	    layout.setVisibility(View.VISIBLE);
	    // Инициирование общего запроса.
	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Загрузка adView с объявлением.
	    adView.loadAd(adRequest);
		} else {
			LinearLayout layout = (LinearLayout)findViewById(R.id.mainRtLt);
			layout.setVisibility(View.GONE);
		}
		//!----------------------------------AD-----------------------------------------------------!
	}
	
	@Override
	  public void onPause() {
		if (adView != null)
	    adView.pause();
	    super.onPause();
	  }
	
	private final Handler handler = new Handler();
	
	@Override
	public void onResume() {
		super.onResume();
		if (adView != null)
		adView.resume();
		
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				if (isMyServiceRunning(TaskerService.class))
		    		onServiceOn();
		    	else
		    		onServiceOff();
			}
		}, 500);
	}
	
	@Override															
	public boolean onCreateOptionsMenu(Menu menu) {
																		
		getMenuInflater().inflate(R.menu.main, menu);
		mainMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	      case R.id.menu_settings:										
	    	  startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
	    	  break;
		}
		return true;
	}
	
	@Override
	public void onDestroy() {
		if (adView != null)
		adView.destroy();
		unregisterReceiver(uiUpdated);
		if (bp != null) 
	    bp.release();
		super.onDestroy();
	}
	
    @Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		 outState.putString("log", log);
	}
	
	private BroadcastReceiver uiUpdated= new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if (sPref.getBoolean("showLog", false)){
	    		mTextLogger.setVisibility(View.VISIBLE);
	    		mScrollView.setVisibility(View.VISIBLE);
	    		if (logLength<500)
	    			log = log +"\n"+intent.getExtras().getString("lastLogStroke");
	    		else {
	    			log = intent.getExtras().getString("lastLogStroke");
	    			logLength=0;
	    		}
	    		logLength++;
	    		mTextLogger.setText(log);
	    		mScrollView.scrollTo(0, mTextLogger.getBottom());
	    	} else {
	    		mTextLogger.setVisibility(View.GONE);
	    		mScrollView.setVisibility(View.GONE);
	    	}
	    	
	    	//pulse animation
	    	if (sPref.getBoolean("showRedError", true)){
	    	if (intent.getExtras().getBoolean("errorOccurred")) {
		        mBackgroundShape2.animate()
		        .alpha(1.0f)												//make second visible
		        .setInterpolator(new AccelerateDecelerateInterpolator())
		        .setDuration(ANIMATION_DURATION_ERROR);
		        
		        mBackgroundShape.animate()
		        .alpha(0.0f)												//make  first invisible
		        .setInterpolator(new AccelerateDecelerateInterpolator())
		        .setDuration(ANIMATION_DURATION_ERROR);
				} else {
					mBackgroundShape.animate()
			        .alpha(1.0f)											//make first visible
			        .setInterpolator(new AccelerateDecelerateInterpolator())
			        .setDuration(ANIMATION_DURATION_ERROR);
			        
			        mBackgroundShape2.animate()
			        .alpha(0.0f)											//make second invisible
			        .setInterpolator(new AccelerateDecelerateInterpolator())
			        .setDuration(ANIMATION_DURATION_ERROR);
				}
	    	}

	    }
	};
	
	private void onServiceOn() {
		if (!isMyServiceRunning(TaskerService.class))
		startService(new Intent(getApplicationContext(), TaskerService.class));
        if (mBackgroundShape == null) {
            return;
        }
        if (mFullScreenScale <= 0.0f) {
            mFullScreenScale = getMeasureScale();
        }
        if (Integer.valueOf(android.os.Build.VERSION.SDK)>18)
        getActionBar().hide();
        if (mainMenu!=null)												//disable menu
			mainMenu.findItem(R.id.menu_settings).setEnabled(false);
        mBackgroundShape.animate()
                .scaleX(mFullScreenScale)
                .scaleY(mFullScreenScale)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(ANIMATION_DURATION);
        mBackgroundShape2.animate()
        .scaleX(mFullScreenScale)
        .scaleY(mFullScreenScale)
        .setInterpolator(new AccelerateDecelerateInterpolator())
        .setDuration(ANIMATION_DURATION);
    }

    private void onServiceOff() {
    	if (isMyServiceRunning(TaskerService.class))
    	stopService(new Intent(getApplicationContext(), TaskerService.class));
        if (mBackgroundShape == null) {
            return;
        }
        if (Integer.valueOf(android.os.Build.VERSION.SDK)>18)
        getActionBar().show();
        if (mainMenu!=null)												//disable menu
			mainMenu.findItem(R.id.menu_settings).setEnabled(true);
        mBackgroundShape.animate()
                .scaleX(1)
                .scaleY(1)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(ANIMATION_DURATION);
        mBackgroundShape2.animate()
        .scaleX(1)
        .scaleY(1)
        .setInterpolator(new OvershootInterpolator())
        .setDuration(ANIMATION_DURATION);
    }
	
    private float getMeasureScale() {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float displayHeight = outMetrics.heightPixels;
        float displayWidth  = outMetrics.widthPixels;
        return (Math.max(displayHeight, displayWidth) /
                mContext.getResources().getDimensionPixelSize(R.dimen.button_size)) * 2;
    }
    
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFullScreenScale = getMeasureScale();
    }
    
    private boolean isMyServiceRunning(Class<?> serviceClass) {			//returns true is service running
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
}
