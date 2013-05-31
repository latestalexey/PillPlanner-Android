package org.t2.pillplanner;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class SettingsFragment extends SherlockFragment {

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
		view = inflater.inflate(R.layout.fragment_settings, null);

		LinearLayout llMonitors = (LinearLayout) view.findViewById(R.id.llMonitors);
		llMonitors.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MonitorsActivity.class);
				startActivity(intent);
			}
		});

		LinearLayout llAccount = (LinearLayout) view.findViewById(R.id.llAccount);
		llAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AccountActivity.class);
				startActivity(intent);
			}
		});

		LinearLayout llTutorial = (LinearLayout) view.findViewById(R.id.llTutorial);
		llTutorial.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), TutorialActivity.class);
				startActivity(intent);
			}
		});

		LinearLayout llHelp = (LinearLayout) view.findViewById(R.id.llHelp);
		llHelp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), HelpActivity.class);
				startActivity(intent);
			}
		});

		
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
