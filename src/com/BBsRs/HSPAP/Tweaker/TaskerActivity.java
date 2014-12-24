package com.BBsRs.HSPAP.Tweaker;



import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.ScrollView;
import android.widget.TextView;

public class TaskerActivity extends Activity {

	private static final int ANIMATION_DURATION = 300;
	private float mFullScreenScale;
	
	private Context mContext;
	
	private ImageView mBackgroundShape;
	private TextView mLightbulb;
	private TextView mTextLogger;
	private ScrollView mScrollView;
	
	private String log = "";
	private int logLength = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().show();
        getActionBar().setTitle(R.string.app_name);					
	    getActionBar().setIcon(android.R.color.transparent);
	    
	    registerReceiver(uiUpdated, new IntentFilter("DOWNLOAD_UPDATED"));
        
		setContentView(R.layout.activity_tasker);
		
		mBackgroundShape = (ImageView) findViewById(R.id.bg);
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
		
		if (isMyServiceRunning(TaskerService.class))
    		onServiceOn();
    	else
    		onServiceOff();
	}
	
	@Override															
	public boolean onCreateOptionsMenu(Menu menu) {
																		
		getMenuInflater().inflate(R.menu.main, menu);
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
		super.onDestroy();
		unregisterReceiver(uiUpdated);
	}
	
	private BroadcastReceiver uiUpdated= new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if (logLength<500)
	    		log = log +"\n"+intent.getExtras().getString("lastLogStroke");
	    	else {
	    		log = intent.getExtras().getString("lastLogStroke");
	    		logLength=0;
	    	}
	    	logLength++;
	        mTextLogger.setText(log);
	        mScrollView.scrollTo(0, mScrollView.getBottom());
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
        getActionBar().hide();
        mBackgroundShape.animate()
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
        getActionBar().show();
        mBackgroundShape.animate()
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
}
