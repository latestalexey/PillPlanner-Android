package org.t2.pillplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class SplashFragment extends SherlockFragment {
    @SuppressWarnings("unused")
    private static final String TAG = "SplashFragment";
    public static String STATUSMESSAGE = "";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, null);
    }
}
