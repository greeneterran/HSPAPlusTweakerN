package com.BBsRs.hspatweaker;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActionBar().setTitle(R.string.settings);
	    getActionBar().setHomeButtonEnabled(true);
	    addPreferencesFromResource(R.xml.settings);
	    // TODO Auto-generated method stub
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	      case android.R.id.home:
	    	  finish();
	    	  break;
		}
		return true;
	}

}
