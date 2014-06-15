package com.BBsRs.hspaptweaker;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActionBar().setTitle(R.string.settings);						//set title
	    getActionBar().setIcon(android.R.color.transparent);			//set no icon
	    addPreferencesFromResource(R.xml.main_settings);				//set settings from main_settings xml
	    // TODO Auto-generated method stub
	}
	
}
