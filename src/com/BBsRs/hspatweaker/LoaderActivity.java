package com.BBsRs.hspatweaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class LoaderActivity extends Activity {

	private timer CountDownTimer;					// for timer4
	public class timer extends CountDownTimer{

		public timer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);}

		@Override
		public void onFinish() {
			startActivity(new Intent(getApplicationContext(), TaskerActivity.class));
			finish();
		}

		@Override
		public void onTick(long arg0) {}
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        CountDownTimer = new timer (3000, 1000);   		//timer to 2 seconds (tick one second)
        CountDownTimer.start();							//start timer
    }

}
