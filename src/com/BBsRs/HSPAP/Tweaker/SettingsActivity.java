package com.BBsRs.HSPAP.Tweaker;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActionBar().setTitle(R.string.settings);						
	    getActionBar().setIcon(android.R.color.transparent);
	    addPreferencesFromResource(R.xml.preferences);
	    // TODO Auto-generated method stub
	}

}
