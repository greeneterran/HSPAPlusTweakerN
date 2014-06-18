package com.BBsRs.hspaptweaker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskerActivity extends Activity {
	
																		//for animation
	SharedPreferences sharedPrefs;
	private Context mContext;
	public boolean animationPeriod = false;
	private static final int ANIMATION_DURATION = 300;
	private static final int ANIMATION_DURATION_TWO = 1300;
	private float mFullScreenScale;
	private boolean isStarted = false;
	
	private TextView textButton;										//text button
    private ImageView mBackgroundShape;									//orange oval shape wrap text
    private ImageView mBackgroundShape2;									//orange oval shape wrap text
    
    Menu mainMenu = null;												//global variable of menu 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    																//init
	    sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);			//show action bar
        getActionBar().show();
        getActionBar().setTitle(R.string.app_name);						//set title
	    getActionBar().setIcon(android.R.color.transparent);			//set no icon
        
	    super.setContentView(R.layout.activity_tasker);
	    mBackgroundShape = (ImageView) findViewById(R.id.bg);
	    mBackgroundShape2 = (ImageView) findViewById(R.id.bg2);
        textButton = (TextView)findViewById(R.id.textButton);
        mContext = this;
        
        																//text Button Clicked
        textButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!isStarted){
					onTaskServiceOn();									//animate button effect On
					startService(new Intent(getApplicationContext(), TaskerService.class));
				} else {
					onTaskServiceOff();									//animate button effect On
					stopService(new Intent(getApplicationContext(), TaskerService.class));
				}
			}
		});
	}
	
	@Override 
	public void onResume(){
		super.onResume();
		if (isMyServiceRunning(TaskerService.class)) {					//check is service already running
			onTaskServiceOn();
		} else {
			onTaskServiceOff();
		}
	}
	
	@Override															//create menu from menu_tasker
	public boolean onCreateOptionsMenu(Menu menu) {
																		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_tasker, menu);
		mainMenu = menu;												//grab menu to global variable
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	      case R.id.menu_settings:										//open settings on menu settings pressed
	    	  startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
	    	  break;
		}
		return true;
	}
	
	private timer CountDownTimer; 										// for timer4 Animation

	public class timer extends CountDownTimer {

		public timer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			
			if (!animationPeriod) {										//check what queue for animation
	        mBackgroundShape2.animate()
	        .alpha(1.0f)												//make second visible
	        .setInterpolator(new AccelerateDecelerateInterpolator())
	        .setDuration(ANIMATION_DURATION_TWO);
	        
	        mBackgroundShape.animate()
	        .alpha(0.0f)												//make  first invisible
	        .setInterpolator(new AccelerateDecelerateInterpolator())
	        .setDuration(ANIMATION_DURATION_TWO);
			} else {
				mBackgroundShape.animate()
		        .alpha(1.0f)											//make first visible
		        .setInterpolator(new AccelerateDecelerateInterpolator())
		        .setDuration(ANIMATION_DURATION_TWO);
		        
		        mBackgroundShape2.animate()
		        .alpha(0.0f)											//make second invisible
		        .setInterpolator(new AccelerateDecelerateInterpolator())
		        .setDuration(ANIMATION_DURATION_TWO);
			}
	        
	        animationPeriod = ! animationPeriod;
			startCicleAnimation();										//cicle animation
		}

		@Override
		public void onTick(long arg0) {
		}
	}
	
	public void startCicleAnimation() {
		if (isStarted) {												//if service is killed stop timer in new thread recursive
			CountDownTimer = new timer(ANIMATION_DURATION_TWO, ANIMATION_DURATION_TWO); 					// timer to 1 sec def value
			
			CountDownTimer.start(); 									// start timer
		}
	}
	

    private void onTaskServiceOn() {
    	isStarted = true;
        if (mBackgroundShape == null) {
            return;
        }
        if (mFullScreenScale <= 0.0f) {
            mFullScreenScale = getMeasureScale();
        }
        if (Integer.valueOf(android.os.Build.VERSION.SDK)>18)			//hide ab works only from api 19
        getActionBar().hide();
        if (mainMenu!=null)												//disable menu
			mainMenu.findItem(R.id.menu_settings).setEnabled(false);
        mBackgroundShape.animate()
                .scaleX(mFullScreenScale)
                .scaleY(mFullScreenScale)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(ANIMATION_DURATION);
        
        mBackgroundShape2.animate()										//make similar form to second oval
        .scaleX(mFullScreenScale)
        .scaleY(mFullScreenScale)
        .setInterpolator(new AccelerateDecelerateInterpolator())
        .setDuration(ANIMATION_DURATION);
        textButton.setTextColor(mContext.getResources().getColor(R.color.notWhite));
        
        startCicleAnimation();											//start nice animation
    }

    private void onTaskServiceOff() {
    	isStarted = false;
        if (mBackgroundShape == null) {
            return;
        }
        if (Integer.valueOf(android.os.Build.VERSION.SDK)>18)			//show ab works only from api 19
        getActionBar().show();
        if (mainMenu!=null)												//enable menu
			mainMenu.findItem(R.id.menu_settings).setEnabled(true);	
        mBackgroundShape.animate()
                .scaleX(1)
                .scaleY(1)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(ANIMATION_DURATION);
        
        mBackgroundShape2.animate()										//make similar form to second oval
        .scaleX(1)
        .scaleY(1)
        .setInterpolator(new OvershootInterpolator())
        .setDuration(ANIMATION_DURATION);
        textButton.setTextColor(mContext.getResources().getColor(R.color.notBlack));
    }
    
    private float getMeasureScale() {									//func returns which size we need increase oval to wrap all screen
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float displayHeight = outMetrics.heightPixels;
        float displayWidth  = outMetrics.widthPixels;
        return (Math.max(displayHeight, displayWidth) /
                mContext.getResources().getDimensionPixelSize(R.dimen.button_size)) * 2;
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
