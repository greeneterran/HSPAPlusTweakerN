package com.BBsRs.HSPAP.Tweaker;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class TaskerService extends Service {
	
	boolean isNeedToStop = false;
	
	PowerManager pm;
	PowerManager.WakeLock wl;
	
	SharedPreferences sPref;

	public void onCreate() {
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
		wl.acquire();
		startMission();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		wl.release();
		isNeedToStop = true;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void startMission() {
		if (!isNeedToStop) {													
			CountDownTimer = new timer(1000, 1000); 			
			
			CountDownTimer.start(); 									
		}
	}
	
	private timer CountDownTimer; 										

	public class timer extends CountDownTimer {

		public timer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			Thread thr = new Thread(new Runnable() {
				public void run() {
					try {
						Document doc = Jsoup.connect("http://brothers-rovers.3dn.ru/HPlusTweaker/1.txt").get();
						Log.i("23", doc.text());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			thr.start();
			startMission();
		}

		@Override
		public void onTick(long arg0) {
		}
	}
	
	
}
