package com.BBsRs.hspatweaker;

import java.io.IOException;

import org.jsoup.Jsoup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class TaskerActivity extends Activity {
	
	Button start;
	ProgressBar prBr;
	SharedPreferences sharedPrefs;
	Menu mainMenu = null; // local variable for menu

	boolean isStarted = false;
	boolean merged = false;
	
	private timer CountDownTimer;					// for timer4
	public class timer extends CountDownTimer{

		public timer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);}

		@Override
		public void onFinish() {
			Thread thr=new Thread(new Runnable() {				
		        public void run() {
			try {
				int SourceServer = Integer.parseInt(sharedPrefs.getString("downloadSource", "1"));
				
				switch (SourceServer){
				case 0:
					Log.i("TAG_VK", Jsoup.connect(getApplicationContext().getResources().getStringArray(R.array.vk_file_sources)[Integer.parseInt(sharedPrefs.getString("fileSize", "1"))-1]).get().text());
					break;
				case 1:
					Log.i("TAG_UCOZ", Jsoup.connect("http://brothers-rovers.3dn.ru/HPlusTweaker/"+sharedPrefs.getString("fileSize", "1")+".txt").get().text());
					break;
				case 2: 
					if (merged){
						Log.i("TAG_VK", Jsoup.connect(getApplicationContext().getResources().getStringArray(R.array.vk_file_sources)[Integer.parseInt(sharedPrefs.getString("fileSize", "1"))-1]).get().text());
					}
					else {
						Log.i("TAG_UCOZ", Jsoup.connect("http://brothers-rovers.3dn.ru/HPlusTweaker/"+sharedPrefs.getString("fileSize", "1")+".txt").get().text());
					}
					merged=!merged;
					break;
				}
				
				
			} catch (IOException e) {
				Log.e("ERROR", "ERROR");
				isStarted=false;
			}
			}
		    });
			thr.start();
			startMission();
		}

		@Override
		public void onTick(long arg0) {}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_tasker);
	    
	    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    start = (Button)findViewById(R.id.button1);
	    prBr = (ProgressBar)findViewById(R.id.progressBar1);
	    
	    start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isStarted=!isStarted;
				if (isStarted){
					if (mainMenu!=null)
						mainMenu.findItem(R.id.action_settings).setEnabled(false);	//remove item from menu
					startMission();
					start.setText(R.string.stop);
					prBr.setVisibility(View.VISIBLE);
				} else {
					if (mainMenu!=null)
						mainMenu.findItem(R.id.action_settings).setEnabled(true);	//remove item from menu
					start.setText(R.string.start);
					prBr.setVisibility(View.GONE);
				}
			}
		});
	
	    // TODO Auto-generated method stub
	}
	
	public void startMission(){
		if (isStarted) {
		CountDownTimer = new timer (Integer.parseInt(sharedPrefs.getString("downloadItnerval", "1000")), Integer.parseInt(sharedPrefs.getString("downloadItnerval", "1000")));   		//timer to 2 seconds (tick one second)
        CountDownTimer.start();							//start timer
		} else {
		start.setText(R.string.start);
		prBr.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loader, menu);
		mainMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	      case R.id.action_settings:
	    	  startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
	    	  break;
		}
		return true;
	}
	
}
