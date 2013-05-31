package org.t2.pillplanner;

import org.t2.pillplanner.classes.Global;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Start the reminder service
		//Intent alarmIntent = new Intent(this, AlarmService.class);
		//this.startService(alarmIntent);

		if(Global.currentUser == null)
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			this.finish();
		}
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setIcon(mSectionsPagerAdapter.getIconResourceID(i))
					.setTabListener(this));
		}
		
		actionBar.setSelectedNavigationItem(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*if(Global.authTokenGoogle.trim().equals(""))
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			this.finish();
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        //Intent intent;
	        switch (item.getItemId()) {
	            case R.id.mainmenu_logout:
	                Global.currentUser = null;
	                this.finish();

	                break;
	            default:
	                return super.onOptionsItemSelected(item);
	        }

	        return true;
	    }
	 
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			if(position == 0) //Overview
			{
				Fragment fragment = new OverviewFragment();
				return fragment;
			}
			else if(position == 1) //Medications
			{
				Fragment fragment = new MedicationsFragment();
				return fragment;
			}
			else if(position == 2) //Reminders
			{
				Fragment fragment = new RemindersFragment();
				return fragment;
			}
			else if(position == 3) //Settings
			{
				Fragment fragment = new SettingsFragment();
				return fragment;
			}
			else
			{
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			}
			
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.tab_overview);
			case 1:
				return getString(R.string.tab_medications);
			case 2:
				return getString(R.string.tab_reminders);
			case 3:
				return getString(R.string.tab_settings);
			}
			return null;
		}
		
		public int getIconResourceID(int position)
		{
			switch (position) {
			case 0:
				return R.drawable.ab_book;
			case 1:
				return R.drawable.ab_edit;
			case 2:
				return R.drawable.ab_clock;
			case 3:
				return R.drawable.ab_about;
			}
			return 0;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
