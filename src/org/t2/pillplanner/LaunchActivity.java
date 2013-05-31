package org.t2.pillplanner;

import java.util.ArrayList;

import org.t2.pillplanner.classes.DatabaseHelper;
import org.t2.pillplanner.classes.Global;
import org.t2.pillplanner.classes.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class LaunchActivity extends SherlockFragmentActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "LaunchActivity";

    @SuppressWarnings("unused")
	private boolean mSplashDisplayed;

    private boolean mEulaAccepted;

    private SharedPreferences mPrefs;

    private Handler mHandler;

	private DatabaseHelper db;
	static Context ctx;
		
    public void onAcceptEulaClick(View v) {
        mEulaAccepted = true;
        mPrefs.edit()
                .putBoolean("t2pp_eula_accepted", mEulaAccepted)
                .commit();
        showHome();
    }

    @Override
    protected void onCreate(final Bundle state) {
        super.onCreate(state);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_launch);

        mHandler = new Handler();

        ctx = this;
		db = new DatabaseHelper(ctx);
		
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEulaAccepted = mPrefs.getBoolean("t2pp_eula_accepted", false);
        
        //Override and always show eula for now.
        //mEulaAccepted = false;
        
        mSplashDisplayed = state != null;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.launch_fragments, new SplashFragment());
        ft.commit();
        
        new AsyncTask<Void, Integer, Boolean>() { 
			protected void onPostExecute(Boolean result) {
				
				if (state == null) {
		            final FragmentManager fm = getSupportFragmentManager();
		            final FragmentTransaction ft = fm.beginTransaction();

		                mHandler.postDelayed(new Runnable() {
		                    public void run() {
		                        mSplashDisplayed = true;
		                        if (!mEulaAccepted) {
		                            showEula(false);
		                        } else {
		                            showHome();
		                        }
		                    }
		                }, 2500);

		            ft.commit();
		        }
				
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				Global.USERS = (ArrayList<User>) db.getUserList();
				return null;
			}
		}.execute(); // start the background processing
		
        

    }

    private void showEula(boolean initial) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        if (initial) {
            ft.add(R.id.launch_fragments, new EulaFragment());
        } else {
            ft.replace(R.id.launch_fragments, new EulaFragment());
        }

        ft.setTransition(initial ? FragmentTransaction.TRANSIT_FRAGMENT_OPEN : FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        getSupportActionBar().setTitle("End User License Agreement");
        getSupportActionBar().show();
    }

    private void showHome() {
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
