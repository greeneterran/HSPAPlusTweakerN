package com.BBsRs.hspaptweaker;

import java.io.IOException;

import org.jsoup.Jsoup;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class TaskerService extends Service {

	SharedPreferences sharedPrefs;
	boolean merged = false;												//help to merge download source
	boolean stop = false;												//help to fully stop the service in OnDestroy method
	private timer CountDownTimer; 										// for timer4

	public class timer extends CountDownTimer {

		public timer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			Thread thr = new Thread(new Runnable() {
				public void run() {
					try {
						int SourceServer = Integer.parseInt(sharedPrefs
								.getString("downloadSource", "1"));

						switch (SourceServer) {
						case 0:											//loading only VK
							Log.i("TAG_VK",
									Jsoup.connect(
											getApplicationContext()
													.getResources()
													.getStringArray(
															R.array.vk_file_sources)[Integer
													.parseInt(sharedPrefs
															.getString(
																	"fileSize",
																	"1")) - 1])
											.get().text());
							break;
						case 1:											//Load only ucoz
							Log.i("TAG_UCOZ",
									Jsoup.connect(
											"http://brothers-rovers.3dn.ru/HPlusTweaker/"
													+ sharedPrefs.getString(
															"fileSize", "1")
													+ ".txt").get().text());
							break;
						case 2:											//Load merged VK, Ucoz
							if (merged) {								//if current true load only VK
								Log.i("TAG_VK",
										Jsoup.connect(
												getApplicationContext()
														.getResources()
														.getStringArray(
																R.array.vk_file_sources)[Integer.parseInt(sharedPrefs
														.getString("fileSize",
																"1")) - 1])
												.get().text());
							} else {									//else Load only Ucoz
								Log.i("TAG_UCOZ",
										Jsoup.connect(
												"http://brothers-rovers.3dn.ru/HPlusTweaker/"
														+ sharedPrefs
																.getString(
																		"fileSize",
																		"1")
														+ ".txt").get().text());
							}
							merged = !merged;							//revert merged, to change source on next load from server
							break;
						}

					} catch (IOException e) {
						Log.e("ERROR", "ERROR");
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

	public void onCreate() {
		super.onCreate();
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		startMission();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		stop = true;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void startMission() {
		if (!stop) {													//if service is killed stop timer in new thread recursive
			CountDownTimer = new timer(Integer.parseInt(sharedPrefs.getString(
					"downloadItnerval", "1000")), Integer.parseInt(sharedPrefs
					.getString("downloadItnerval", "1000"))); 			// timer to manually secs edited in settings by user, or use 1 sec def value
			
			CountDownTimer.start(); 									// start timer
		}
	}
}