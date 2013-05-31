package org.t2.pillplanner;

import com.actionbarsherlock.app.SherlockFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MonitorsFragment extends SherlockFragment {

    //private Context ctx;
	//private DatabaseHelper db;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//ctx = getSherlockActivity();
		//db = new DatabaseHelper(ctx);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view; 
		view = inflater.inflate(R.layout.fragment_monitors, null);

		

		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		//final Bundle state = savedInstanceState != null ? savedInstanceState : getArguments();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
