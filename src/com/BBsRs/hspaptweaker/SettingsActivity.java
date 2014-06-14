package com.BBsRs.hspaptweaker;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.main_settings);				//set settings from main_settings xml
	    // TODO Auto-generated method stub
	}

}
