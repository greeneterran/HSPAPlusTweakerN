package com.BBsRs.HSPAP.Tweaker;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
	
	NotificationManager mNotificationManager;
	Notification not;
	PendingIntent contentIntent;
	
	String LOG_TAG = "TakserServiceH+";
	
	int errorCounter = 0;
	
	public void onCreate() {
		sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (sPref.getBoolean("showPendingNotification", true))
		showPendingNotification();
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
		if (sPref.getBoolean("showPendingNotification", true))
		mNotificationManager.cancelAll();
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void startMission() {
		if (!isNeedToStop) {													
			CountDownTimer = new timer(Integer.parseInt(sPref.getString("downloadItnerval", getResources().getString(R.string.defaultDownloadInterval))), 
										Integer.parseInt(sPref.getString("downloadItnerval", getResources().getString(R.string.defaultDownloadInterval)))); 			
			CountDownTimer.start(); 
			if (sPref.getBoolean("showPendingNotification", true)){
			not.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), getResources().getString(R.string.serviceRunning)+" "+getResources().getString(R.string.errors)+": "+String.valueOf(errorCounter), contentIntent);
		    mNotificationManager.notify(1, not);
			}
		} else {
			this.stopSelf();
		}
	}
	
	private void showPendingNotification(){
	    not = new Notification(R.drawable.ic_launcher, getResources().getString(R.string.serviceRunning), System.currentTimeMillis());
	    contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, TaskerActivity.class), Notification.FLAG_ONGOING_EVENT);        
	    not.flags = Notification.FLAG_ONGOING_EVENT;
	    not.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), getResources().getString(R.string.serviceRunning), contentIntent);
	    mNotificationManager.notify(1, not);
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
						Document doc = Jsoup.connect(sPref.getString("downloadSource", "http://brothers-rovers.3dn.ru/HPlusTweaker/")+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+".txt").get();
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
						errorCounter++;
						sendBroadcast(i);
    					e.printStackTrace();
    				} catch (IOException e) {
    					if (sPref.getBoolean("stopOnError", false))
    					isNeedToStop=true;
    					Log.e(LOG_TAG, "Load Error");
    					Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageError)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", true);
						errorCounter++;
						sendBroadcast(i);
    					e.printStackTrace();
    				} catch (NullPointerException e) {
    					if (sPref.getBoolean("stopOnError", false))
    					isNeedToStop=true;
    	        		Log.e(LOG_TAG, "null Load Error"); 
    	        		Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageError)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", true);
						errorCounter++;
						sendBroadcast(i);
    					e.printStackTrace();
    				} catch (Exception e) {
    					if (sPref.getBoolean("stopOnError", false))
    					isNeedToStop=true;
    	        		Log.e(LOG_TAG, "other Load Error");
    	        		Intent i = new Intent("DOWNLOAD_UPDATED");
						i.putExtra("lastLogStroke",getResources().getString(R.string.defaultLogMessageError)+" "+sPref.getString("fileSize", getResources().getString(R.string.defaultFileSize))+" "+getResources().getString(R.string.defaultLogMessageByte));
						i.putExtra("errorOccurred", true);
						errorCounter++;
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
