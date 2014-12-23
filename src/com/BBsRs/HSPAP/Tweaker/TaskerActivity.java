package com.BBsRs.HSPAP.Tweaker;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskerActivity extends Activity {

	private static final int ANIMATION_DURATION = 300;
	private float mFullScreenScale;
	
	private Context mContext;
	
	private ImageView mBackgroundShape;
	private TextView mLightbulb;
	
	private boolean isRunning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().show();
        
		setContentView(R.layout.activity_tasker);
		
		mBackgroundShape = (ImageView) findViewById(R.id.bg);
		mLightbulb = (TextView) findViewById(R.id.lightbulb);
		
		mLightbulb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (isRunning)
            		onServiceOff();
            	else
            		onServiceOn();
            	isRunning = !isRunning;
            }
        });
	}
	
	private void onServiceOn() {
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
}
