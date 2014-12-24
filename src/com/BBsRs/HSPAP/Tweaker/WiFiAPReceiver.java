package com.BBsRs.HSPAP.Tweaker;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WiFiAPReceiver extends BroadcastReceiver {
	
	String LOG_TAG = "TakserReceiverH+";
	SharedPreferences sPref;
	Context mContext;
	
	public enum WIFI_AP_STATE {WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED}

	@Override
	public void onReceive(Context context, Intent intent) {
		sPref = PreferenceManager.getDefaultSharedPreferences(context);
		mContext = context;
		
		if (sPref.getBoolean("startOnWiFiAPEnabling", false)){
		
			String action = intent.getAction();
			if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
				// get Wi-Fi Hotspot state here 
				int state = intent.getIntExtra("wifi_state", 0);
				// Fix for Android 4
				if (state >= 10) {
					state = state - 10;
				}
				
				if (isWifiApEnabling(WIFI_AP_STATE.class.getEnumConstants()[state])){
					if (!(isMyServiceRunning(TaskerService.class)))
						context.startService(new Intent(context, TaskerService.class));
				}
				
				if (isWifiApDisabling(WIFI_AP_STATE.class.getEnumConstants()[state])){
					if ((isMyServiceRunning(TaskerService.class)))
						context.stopService(new Intent(context, TaskerService.class));
				}
			}
		}
	}
	
    public boolean isWifiApEnabling(WIFI_AP_STATE state) {
        return state == WIFI_AP_STATE.WIFI_AP_STATE_ENABLING;
     }
    
    public boolean isWifiApDisabling(WIFI_AP_STATE state) {
        return state == WIFI_AP_STATE.WIFI_AP_STATE_DISABLING;
     }
    
    private boolean isMyServiceRunning(Class<?> serviceClass) {			//returns true is service running
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
