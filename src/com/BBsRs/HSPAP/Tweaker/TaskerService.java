package com.BBsRs.HSPAP.Tweaker;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class TaskerService extends Service {
	
	boolean isNeedToStop = false;
	
	PowerManager pm;
	PowerManager.WakeLock wl;
	
	SharedPreferences sPref;
	
	String LOG_TAG = "TakserServiceH+";
	
	public void onCreate() {
		sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
		Intent i = new Intent("DOWNLOAD_UPDATED");
		i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageStopping));
		sendBroadcast(i);
		wl.release();
		isNeedToStop = true;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void startMission() {
		if (!isNeedToStop) {													
			CountDownTimer = new timer(Integer.parseInt(sPref.getString("downloadItnerval", getResources().getString(R.string.defaultDownloadInterval))), 
										Integer.parseInt(sPref.getString("downloadItnerval", getResources().getString(R.string.defaultDownloadInterval)))); 			
			
			CountDownTimer.start(); 									
		} else {
			this.stopSelf();
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
						Document doc = Jsoup.connect("http://brothers-rovers.3dn.ru/HPlusTweaker/"+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+".txt").get();
						Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageSuccess)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", false);
						sendBroadcast(i);
					} catch (NotFoundException e) {
						if (sPref.getBoolean("stopOnError", false))
						isNeedToStop=true;
    					Log.e(LOG_TAG, "data Error");
    					Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageError)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", true);
						sendBroadcast(i);
    					e.printStackTrace();
    				} catch (IOException e) {
    					if (sPref.getBoolean("stopOnError", false))
    					isNeedToStop=true;
    					Log.e(LOG_TAG, "Load Error");
    					Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageError)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", true);
						sendBroadcast(i);
    					e.printStackTrace();
    				} catch (NullPointerException e) {
    					if (sPref.getBoolean("stopOnError", false))
    					isNeedToStop=true;
    	        		Log.e(LOG_TAG, "null Load Error"); 
    	        		Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageError)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", true);
						sendBroadcast(i);
    					e.printStackTrace();
    				} catch (Exception e) {
    					if (sPref.getBoolean("stopOnError", false))
    					isNeedToStop=true;
    	        		Log.e(LOG_TAG, "other Load Error");
    	        		Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageError)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", true);
						sendBroadcast(i);
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
